package namrata.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StallWarning;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;


public class TweetTopology {

  
  public static class TweetSpout extends BaseRichSpout
  {
 
    String custkey, custsecret;
    String accesstoken, accesssecret;

 
    SpoutOutputCollector collector;

    // Twitter4j - twitter stream to get tweets
    TwitterStream twitterStream;

 
    LinkedBlockingQueue<String> queue = null;

    // Class for listening on the tweet stream - for twitter4j
    private class TweetListener implements StatusListener {

      // Implement the callback function when a tweet arrives
      @Override
      public void onStatus(Status status)
      {
        // add the tweet into the queue buffer
        queue.offer(status.getText());
      }

      @Override
      public void onDeletionNotice(StatusDeletionNotice sdn)
      {
      }

      @Override
      public void onTrackLimitationNotice(int i)
      {
      }

      @Override
      public void onScrubGeo(long l, long l1)
      {
      }

      @Override
      public void onStallWarning(StallWarning warning)
      {
      }

      @Override
      public void onException(Exception e)
      {
        e.printStackTrace();
      }
    };

   
    public TweetSpout(
        String                key,
        String                secret,
        String                token,
        String                tokensecret)
    {
      custkey = key;
      custsecret = secret;
      accesstoken = token;
      accesssecret = tokensecret;
    }

    @Override
    public void open(
        Map                     map,
        TopologyContext         topologyContext,
        SpoutOutputCollector    spoutOutputCollector)
    {
      
      queue = new LinkedBlockingQueue<String>(1000);

 
      collector = spoutOutputCollector;


      // build the config with credentials for twitter 4j
      ConfigurationBuilder config =
          new ConfigurationBuilder()
                 .setOAuthConsumerKey(custkey)
                 .setOAuthConsumerSecret(custsecret)
                 .setOAuthAccessToken(accesstoken)
                 .setOAuthAccessTokenSecret(accesssecret);

     
      TwitterStreamFactory fact =
          new TwitterStreamFactory(config.build());

      // get an instance of twitter stream
      twitterStream = fact.getInstance();


      twitterStream.addListener(new TweetListener());

     
      twitterStream.sample();
    }

    @Override
    public void nextTuple()
    {
     
      String ret = queue.poll();

     
      if (ret==null)
      {
        Utils.sleep(50);
        return;
      }

    
      collector.emit(new Values(ret));
    }

    @Override
    public void close()
    {
     
      twitterStream.shutdown();
    }

  
    @Override
    public Map<String, Object> getComponentConfiguration()
    {
      
      Config ret = new Config();

     
      ret.setMaxTaskParallelism(1);

      return ret;
    }

    @Override
    public void declareOutputFields(
        OutputFieldsDeclarer outputFieldsDeclarer)
    {
      
      outputFieldsDeclarer.declare(new Fields("tweet"));
    }
  }


  public static class ParseTweetBolt extends BaseRichBolt
  {
    // To output tuples from this bolt to the count bolt
    OutputCollector collector;

    @Override
    public void prepare(
        Map                     map,
        TopologyContext         topologyContext,
        OutputCollector         outputCollector)
    {
  
      collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple)
    {
      
      String tweet = tuple.getString(0);

     
      String delims = "[ .,?!]+";

      
      String[] tokens = tweet.split(delims);

      
      for (String token: tokens) {
        collector.emit(new Values(token));
      }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
      
      declarer.declare(new Fields("tweet-word"));
    }
  }

  
  static class CountBolt extends BaseRichBolt {

    
    private OutputCollector collector;

    
    private Map<String, Integer> countMap;

    @Override
    public void prepare(
        Map                     map,
        TopologyContext         topologyContext,
        OutputCollector         outputCollector)
    {

   
      collector = outputCollector;

 
      countMap = new HashMap<String, Integer>();
    }

    @Override
    public void execute(Tuple tuple)
    {
      
      String word = tuple.getString(0);

      
      if (countMap.get(word) == null) {

       
        countMap.put(word, 1);
      } else {

       
        Integer val = countMap.get(word);

       
        countMap.put(word, ++val);
      }

      // emit the word and count
      collector.emit(new Values(word, countMap.get(word)));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
    {
      
      outputFieldsDeclarer.declare(new Fields("word","count"));
    }
  }

 
  static class ReportBolt extends BaseRichBolt
  {
    // place holder to keep the connection to redis
    transient RedisConnection<String,String> redis;

    @Override
    public void prepare(
        Map                     map,
        TopologyContext         topologyContext,
        OutputCollector         outputCollector)
    {
      // instantiate a redis connection
      RedisClient client = new RedisClient("localhost",6379);

      // initiate the actual connection
      redis = client.connect();
    }

    @Override
    public void execute(Tuple tuple)
    {
      
      String word = tuple.getStringByField("word");

      
      Integer count = tuple.getIntegerByField("count");

     
      redis.publish("WordCountTopology", word + "|" + Long.toString(count));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
      
    }
  }

  public static void main(String[] args) throws Exception
  {
    // create the topology
    TopologyBuilder builder = new TopologyBuilder();

    

    // now create the tweet spout with the credentials
    TweetSpout tweetSpout = new TweetSpout(
        "fPcmBzpfpRhBjWvX66eFE45zy",
        "eCd6UpXQ18UHMV3arUVqZaL8qQh4P9btD4JnWnki3WiNp127na",
        "4788455719-2RnQ9gwMlnXzMAn4495soVOL7H6XajjMn4SJm8W",
        "7mOdoq57Jpmr8WKrfKWApncleqFdQ70401ujiN26VoFf2"
    );

    
    builder.setSpout("tweet-spout", tweetSpout, 1);

   
    builder.setBolt("parse-tweet-bolt", new ParseTweetBolt(),10).shuffleGrouping("tweet-spout");
    builder.setBolt("count-bolt", new CountBolt(),15).fieldsGrouping("parse-tweet-bolt", new Fields("tweet-word"));
    builder.setBolt("report-bolt", new ReportBolt(),1).globalGrouping("count-bolt");
    

   
    Config conf = new Config();

    
    conf.setDebug(true);

    if (args != null && args.length > 0) {

     
      conf.setNumWorkers(3);

      
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());

    } else {

      
      conf.setMaxTaskParallelism(3);

     
      LocalCluster cluster = new LocalCluster();

      
      cluster.submitTopology("tweet-word-count", conf, builder.createTopology());

      
      Utils.sleep(30000);

      
      cluster.killTopology("tweet-word-count");

      
      cluster.shutdown();
    }
  }
}

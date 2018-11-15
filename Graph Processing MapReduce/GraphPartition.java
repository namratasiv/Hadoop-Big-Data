import java.io.*;
import java.util.Scanner;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;


class Vertex implements Writable {
    public long id;                   // the vertex ID
    public Vector<Long> adjacent;     // the vertex neighbors
    public long centroid;             // the id of the centroid in which this vertex belongs to
    public short depth;               // the BFS depth
    
    
    public Vertex() {
    	
    }
    
    public Vertex(long id,Vector<Long> adjacent,long centroid, short depth ) {
    	this.id = id;
    	this.adjacent= adjacent;
    	this.centroid=centroid;
    	this.depth=depth;
    }
    
    public void readFields(DataInput input) throws IOException{
    	id = input.readLong();
    	centroid= input.readLong();
    	depth=input.readShort();
    	adjacent = new Vector<Long>();
    	for (int i=0;i<depth;i++)
    	{
    		adjacent.add(input.readLong());
    	}
    		
    	
 }
    public void write(DataOutput output) throws IOException{
    	
    	output.writeLong(id);
    	output.writeLong(centroid);
    	output.writeShort(depth);
    	for(Long v:adjacent)
    	{
    		output.writeLong(v);
    	}
    }
}

public class GraphPartition {
    static Vector<Long> centroids = new Vector<Long>();
    final static short max_depth = 8;
    static short BFS_depth = 0;

    public static class FirstMapper extends Mapper<Object, Text, LongWritable, Vertex> {
		static int count=0;
		private long id;
		private long centroid;
		private Vector<Long> adjacent = new Vector<Long>();
		@Override
		public void map(Object key, Text line, Context context) throws IOException, InterruptedException {
			String[] l = line.toString().split(",");
			id = Long.parseLong(l[0]);
			for(int i = 0; i<l.length; i++)
			{
				adjacent.addElement(Long.parseLong(l[i]));
			}
			
			while(count<10) {
				centroid=id;
				count++;
			}
			centroid=-1;
	
		}

	}
public static class SecondMapper extends Mapper<LongWritable, Vertex, LongWritable, Vertex> {
		
		@Override
		public void map(LongWritable id, Vertex vertex, Context context) throws IOException, InterruptedException {
			
			context.write(new LongWritable(vertex.id), vertex);
			if(vertex.centroid > 0) {
				for(Long n: vertex.adjacent){
				context.write(new LongWritable(n), new Vertex(n, new Vector<Long>(), vertex.centroid, new Short(BFS_depth)));
				}
			}

		}

	}
public static class SecondReducer extends Reducer<LongWritable, Vertex, LongWritable, Vertex> {

		@Override
		public void reduce(LongWritable id, Iterable<Vertex> values, Context context) throws IOException, InterruptedException {
            Vertex m = new Vertex();
			short min_depth=1000;

 m = new Vertex(id.get(), new Vector<Long>(), new Long(-1), new Short("0"));
for (Vertex v: values) {
	if(v.adjacent!=null) {
		m.adjacent = v.adjacent;
	}
	
    if (v.centroid > 0 && v.depth < min_depth) {
		        min_depth = v.depth;
		        m.centroid = v.centroid;
    }
		 m.depth = min_depth;
		  context.write( id, m);
}
			

		}
	}
public static class ThirdMapper extends Mapper<LongWritable, Vertex, LongWritable, LongWritable> {
		
		@Override
		public void map(LongWritable id , Vertex value, Context context) throws IOException, InterruptedException {
			context.write(new LongWritable(value.centroid), new LongWritable(1));

		}

	}
public static class ThirdReducer extends Reducer<LongWritable, LongWritable, LongWritable, LongWritable> {

		@Override
		public void reduce(LongWritable centroid, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        long m=0;
			for(LongWritable v: values) {
				m = m+v.get();
			}
			context.write(centroid, new LongWritable(m));

		}
	}


    public static void main ( String[] args ) throws Exception {
    	Job job = Job.getInstance();
		job.setJobName("FirstJob");
		/* ... First Map-Reduce job to read the graph */
		job.setJarByClass(GraphPartition.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Vertex.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Vertex.class);
        job.setMapperClass(FirstMapper.class);
        job.setNumReduceTasks(0);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        //FileOutputFormat.setOutputPath(job,new Path(args[2]));
        SequenceFileOutputFormat.setOutputPath(job, new Path(args[1]+"/i0"));
		job.waitForCompletion(true);
		
		for (short i = 0; i < max_depth; i++) {
			BFS_depth++;
			job = Job.getInstance();
			/* ... Second Map-Reduce job to do BFS */
			job.setJobName("SecondJob");
			job.setJarByClass(GraphPartition.class);
	        job.setOutputKeyClass(LongWritable.class);
	        job.setOutputValueClass(Vertex.class);
	        job.setMapOutputKeyClass(LongWritable.class);
	        job.setMapOutputValueClass(Vertex.class);
	        job.setMapperClass(SecondMapper.class);
	        job.setReducerClass(SecondReducer.class);
	        job.setInputFormatClass(SequenceFileInputFormat.class);
	        job.setOutputFormatClass(SequenceFileOutputFormat.class);
	        SequenceFileInputFormat.setInputPaths(job,new Path(args[1]+"/i"+i));
	        SequenceFileOutputFormat.setOutputPath(job, new Path(args[1]+"/i"+(i+1)));
			job.waitForCompletion(true);
		}
		job = Job.getInstance();
		/* ... Final Map-Reduce job to calculate the cluster sizes */
		job.setJobName("ThirdJob");
		job.setJarByClass(GraphPartition.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(LongWritable.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setMapperClass(ThirdMapper.class);
        job.setReducerClass(ThirdReducer.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        SequenceFileInputFormat.setInputPaths(job,new Path(args[1]+"/i8"));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }
}

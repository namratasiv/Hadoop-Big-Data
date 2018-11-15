import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object Partition {

  val depth = 6
  
  
  def mapperFunc(vertex : (Long, Long, List[Long])) : List[(Long,Either[(Long,List[Long]),(Long)])] = {
	 				
				var v = List[(Long,Either[(Long,List[Long]),(Long)])]()
				
				var id = vertex._1
				var cluster = vertex._2
				var adjacent = vertex._3
				
  
        if (cluster > -1L && !vertex._3.isEmpty){
         for(x <- adjacent)
         var b = v :+ ( x, Right(cluster) )	
         v=b
         }
      
        else
        {
        var d = v :+(id,Left(cluster,adj))
         v=d
        }
       	
				v	
	}

def reducer_function ( vertex : (Long, Iterable[Either[(Long,List[Long]),Long]])):(Long, Long, List[Long])={
  var id = vertex._1
  var s = vertex._2
  var adjacent: List[Long] = Nil
  var cluster = -1L
  var adj :List[Long] = List()
  for ( p <- s ){
     p match {
       case Right((c))=> {cluster = c}
       case Left((c ,adj)) =>{if (c>0){ return (id,c,adj)  }
       else if(c == -1L){
         var a :List[Long]= adj
         adjacent = a
       }
       
       }/* the node has already been assigned a cluster */
       
     }
  }  
 return ( id, cluster, adjacent )
}
  def main ( args: Array[ String ] ) {
    val conf = new SparkConf().setAppName("Partition")
		val sc = new SparkContext(conf)
    var graph = sc.textFile(args(0)).map( line => { val a = line.split(",")
		var x = 10
    var c = -1L
		var adj : List[Long] = List[Long]()
		if(x>10){
		  c=a(0).toLong
		  x=x-1
		}
    for(k <-1 until a.length){
      var y = adj:+a(k).toLong
      adj = y
    }
    (a(0).toLong, c, adj)})

    for (i <- 1 to depth)
    graph = graph.flatMap{ map1 => mapperFunc(map1) }.groupByKey.map{ red1=>reducer_function(red1)  }

    val t = graph.map(u => (u._2, 1)).reduceByKey(_ + _)
    val sizes=t.collect()
    sizes.foreach(println)
    sc.stop()

  }
}

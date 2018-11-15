import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object KMeans {
	type Point = (Double,Double)

			var centroids: Array[Point] = Array[Point]()
			
			

			def findClosestCent(point: Point, centroids: Array[Point]): Point = {
					
							
							var closesttt = Double.PositiveInfinity
							var c : Point  = centroids(0);

							for (i <- 0 until centroids.length) {
								val distance = scala.math.pow((point._1 - centroids(i)._1),2)+ scala.math.pow((point._2 - centroids(i)._2),2)
										if (distance < closesttt) {
											closesttt = distance
											 c  = centroids(i)
										}
							}

					c
	}

	def main(args: Array[ String ]) {
		    val conf = new SparkConf().setAppName("KMeans")
			
				val sc = new SparkContext(conf)
				val p = sc.textFile(args(0)).map(line  => { val a = line.split(",")
				(a(0).toDouble,a(1).toDouble) } ) 

				centroids = sc.textFile(args(1)).map(line  => { val b = line.split(",")
				(b(0).toDouble,b(1).toDouble) } ).collect()


		for ( i <- 1 to 5 )
		val closest = p.map (point => (findClosestCent(point, centroids), point))
				
        centroids = closest.groupByKey().map { case(i, p) =>
							var newSeq = p.toSeq
							var sumx : Double=0
							var sumy : Double =0
							for ( k<- 0 until newSeq.length){

								sumx+=newSeq(k)._1
								sumy+=newSeq(k)._2
								
							}
						   (sumx/newSeq.length,sumy/newSeq.length)
					}.collect()

				centroids.foreach(println)
				sc.stop()
	}
}

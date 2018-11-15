import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.ToolRunner;

class Point implements WritableComparable<Point> {
	public double x;
	public double y;

	public Point() {
		// TODO Auto-generated constructor stub
	}

	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void readFields(DataInput input) throws IOException {
		x = input.readDouble();
		y = input.readDouble();
	}

	public void write(DataOutput output) throws IOException {

		output.writeDouble(this.x);
		output.writeDouble(y);

	}

	public int compareTo(Point arg0) {
		if (this.x == arg0.x) {
			if (this.y == arg0.y)
				return 0;
			else if (this.y < arg0.y)
				return -1;
			else
				return 1;
		} else if (this.x < arg0.x)
			return -1;
		else
			return 1;

	}

	public String toString () { 	
    	return x+","+y ; 
    	}

}

public class KMeans {

	static Vector<Point> centroids = new Vector<Point>(100);

	public static class AvgMapper extends Mapper<Object, Text, Point, Point> {

		protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
				throws IOException, InterruptedException {

			URI[] paths = context.getCacheFiles();
			Configuration conf = context.getConfiguration();
			FileSystem fs = FileSystem.get(conf);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(new Path(paths[0]))));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split(",");
				double x = Double.parseDouble(temp[0]);
				double y = Double.parseDouble(temp[1]);
				Point p = new Point(x, y);
				centroids.add(p);
			}

		}

		@Override
		public void map(Object key, Text line, Context context) throws IOException, InterruptedException {

			double dis = Double.MAX_VALUE;
			Point closest = new Point();
			Scanner s = new Scanner(line.toString()).useDelimiter(",");
			Point p = new Point(s.nextDouble(), s.nextDouble());

			for (Point c : centroids) {

				double d1 = Math.abs(c.x - p.x);
				double d2 = Math.abs(c.y - p.y);

				double dist = Math.sqrt((d1 * d1 + d2 * d2));

				if (dist < dis) {

					closest = c;
					dis = dist;

				}

			}
			

			context.write(closest, p);

			s.close();
		}

	}

	public static class AvgReducer extends Reducer<Point, Point, Point, Object> {

		@Override
		public void reduce(Point c, Iterable<Point> points, Context context) throws IOException, InterruptedException {

			int count = 0;
			double sx = 0.0, sy = 0.0;
			for (Point p : points) {

				count++;
				sx += p.x;
				sy += p.y;

			}
			;
			c.x = sx / count;
			c.y = sy / count;

			context.write(c, null);

		}
	}

		public static void main(String[] args) throws Exception {

			Job job = Job.getInstance();
			job.setJobName("MyJob");
			job.setJarByClass(KMeans.class);
			job.setOutputKeyClass(Point.class);
			job.setOutputValueClass(Point.class);
			job.setMapOutputKeyClass(Point.class);
			job.setMapOutputValueClass(Point.class);
			job.setMapperClass(AvgMapper.class);
			job.setReducerClass(AvgReducer.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			FileInputFormat.setInputPaths(job, new Path(args[0]));
			job.addCacheFile(new URI(args[1]));
			FileOutputFormat.setOutputPath(job, new Path(args[2]));
			job.waitForCompletion(true);
		}
	
}

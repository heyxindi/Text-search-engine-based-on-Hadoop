package WordCount;
import java.io.IOException; 
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.PropertyConfigurator; 

public class WordCount {
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private String pattern = "[^a-zA-Z0-9]";
		//replace all those pattern with " ".	
		public void map(Object key, Text value, Context context) throws
		               IOException, InterruptedException{
			String line = value.toString();                 //take one row at one time
			line = line.replaceAll(pattern, " ");
			line = line.toLowerCase();
			StringTokenizer itr= new StringTokenizer(line); //split each row into many words
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}
	
	public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
		private IntWritable result = new IntWritable();
		public void reduce(Text key, Iterable<IntWritable> values, Context
				context ) throws IOException, InterruptedException{ 
			    int sum = 0;
			    for (IntWritable val: values) {
			    	sum += val.get();//count the total number of each word
			    }
			    result.set(sum);
			    context.write(key, result); 
		} 
	}
	
	public static void main(String[] args) throws Exception{
	PropertyConfigurator.configure("config/log4j.properties");
	Configuration conf = new Configuration();
	Job job = Job.getInstance(conf, "word count");
	job.setJarByClass(WordCount.class);
	job.setMapperClass(TokenizerMapper.class);
	job.setCombinerClass(IntSumReducer.class);
	job.setReducerClass(IntSumReducer.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);
	FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/data/dataset"));
	FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/output/Q1_result"));
	System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}

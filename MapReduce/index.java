package index;
import java.io.IOException; import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.PropertyConfigurator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class index {
	public static class TokenizerMapper
	extends Mapper<Object, Text, Text, Text>{		    
		private String pattern = "[\\pP|\\pS|\\d+]";
		int lineNumber = 0; // used to record the line number 
		private Text word = new Text();//acquire word
		private Text lineNum = new Text();// map output 
		public void map(Object key, Text value, Context context) throws
		                IOException, InterruptedException{
			String line = value.toString();//take one row at one time
			line = line.replaceAll(pattern, "");//replace all symbols with " "
            FileSplit inputSplit = (FileSplit) context.getInputSplit();
            String Name = inputSplit.getPath().getName();
            String fileName = Name.substring(0, Name.lastIndexOf("."));
            // end 
			StringTokenizer itr = new StringTokenizer(line);//split each row into words
			lineNumber = lineNumber + 1; 
			String lineN = String.valueOf(lineNumber);
			while (itr.hasMoreTokens()) {
				String W_N = (itr.nextToken()+ ">" + fileName);
				W_N = W_N.replaceAll("\\s+", "").toLowerCase();//turn to lower case
				word.set(W_N);
				lineNum.set("("+lineN+")");
				context.write(word,lineNum);
			}
		}
	}
public static class Combiner // combine the filename and the line they exist
extends Reducer<Text, Text, Text, Text>{
	public void reduce(Text key, Iterable<Text> values, Context
			context) throws IOException, InterruptedException{
		Text value = new Text();
		String fS = new String();
		for (Text val:values) {
			String[] str = key.toString().split(">"); 
			//split the key from ">", put one part into the new key, another part into the new value
			key.set(str[0]);
		    fS += val.toString();
		    value.set(fS+"<"+str[1]);			
		}
		context.write(key ,value);
}
}
public static class reducer
extends Reducer<Text, Text, Text, Text>{
	// finally get the <word><filename and [the line they exist]>
	private Text result = new Text();
	public void reduce(Text key, Iterable<Text> values, Context
			context) throws IOException, InterruptedException{
		String fileList = new String();
		for(Text value: values) {//combine all the data with the same key(word)
			String[] str1 = value.toString().split("<");
			
			fileList += str1[1]+": "+"["+str1[0]+"]"+",   ";
		}
		result.set("{"+fileList+"}");
		context.write(key, result);
	}
}
public static void main(String[] args) throws Exception {
//	long start = System.currentTimeMillis();
	PropertyConfigurator.configure("config/log4j.properties");
	Configuration conf = new Configuration();
	Job job = Job.getInstance(conf, "word count");
	job.setJarByClass(index.class);
	job.setMapperClass(TokenizerMapper.class);
	job.setCombinerClass(Combiner.class);
	job.setReducerClass(reducer.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	
	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));
	//FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/data/test"));
	//FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/output_test/Q2_result_time"));
//	long end = System.currentTimeMillis();
//	long time = (end - start);
//	System.out.println("The MapReduce took " + time +" ms");
	System.exit(job.waitForCompletion(true) ? 0 : 1);
	//System.out.println("The result is" + time);
	
}
}

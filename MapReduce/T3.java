package T3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.PropertyConfigurator;

public class T3 {
	//to get the list of words in all the file
	public static class TokenizerMapper extends Mapper <Object, Text, Text, IntWritable> {
		private String pattern="[^a-zA-Z0-9]|\\d";
		private final static IntWritable one = new IntWritable(1);
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			FileSplit inputSplit=(FileSplit) context.getInputSplit();
			String Name=inputSplit.getPath().getName();
			String fileName=Name.substring(0,Name.lastIndexOf("."));
			String line=value.toString().replaceAll(pattern," ").toLowerCase();
			StringTokenizer itr = new StringTokenizer(line);
			Text keyValue=new Text();
			while(itr.hasMoreTokens()){
				String s1 = new String();
				
				s1 = itr.nextToken()+":"+fileName;
				keyValue.set(s1);
				context.write(keyValue, one);
			}
		}
	}
	//calculate the number of different words in one file
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result=new IntWritable();
		public void reduce(Text key, Iterable <IntWritable> values, Context context) throws IOException, InterruptedException{
			int sum=0;
			for(IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}
	//to get the document name of the word
	public static class Mapper2 extends Mapper <Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			String line=value.toString();
			StringTokenizer itr = new StringTokenizer(line);
			Text keyValue=new Text();
			Text Value=new Text();
			while(itr.hasMoreTokens()){
				String s1 = new String();
				String[] wf=itr.nextToken().toString().split(":");
				s1 = wf[0]+":"+itr.nextToken();
				Value.set(s1);
				keyValue.set(wf[1]);
		        context.write(keyValue, Value);
			}
		}
	}
	//calculate the total number of terms in the document
	public static class Reducer2 extends Reducer<Text, Text, Text, Text>{
		int sum = 0;
		public void reduce(Text key, Iterable <Text> values, Context context) throws IOException, InterruptedException{
			List<Text> cache=new ArrayList<Text>();
			Text keyValue=new Text();
			Text Value=new Text();
		    for(Text value:values){
		    	cache.add(new Text(value));
				String[] val=value.toString().split(":");
			    int va=Integer.parseInt(val[1]);
			    sum+=va;
			}
		    for(Text line:cache) {
		    	String[] val1=line.toString().split(":");
			    keyValue.set(val1[0]+":"+key);
			    Value.set(val1[1]+","+Integer.toString(sum));
			    context.write(keyValue, Value);
		    }
		}
	}
	
	public static class Mapper3 extends Mapper <Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			String line=value.toString();
			StringTokenizer itr = new StringTokenizer(line);
			Text keyValue=new Text();
			Text Value=new Text();
			while(itr.hasMoreTokens()){
				String s1=new String();
				String s2=new String();
				String[] wf=itr.nextToken().toString().split(":");
				s1=wf[0];
				s2=wf[1]+";"+itr.nextToken().toString()+";"+"1";
				keyValue.set(s1);
				Value.set(s2);
				context.write(keyValue,Value);
			}
		}
	}
	//to get the TF-IDF
	public static class Reducer3 extends Reducer<Text, Text, Text, DoubleWritable>{
		Double sum = 0.0;
		public void reduce(Text key, Iterable <Text> values, Context context) throws IOException, InterruptedException{
			Text keyValue=new Text();
			DoubleWritable Value=new DoubleWritable();
			List<Text> cache=new ArrayList<Text>();
			Double TF=0.0;
			Double IDF=0.0;
		    for(Text line:values) {
		    	cache.add(new Text(line));
		    	String[] val=line.toString().split(";");
			    double va=Integer.parseInt(val[2]);
			    sum+=va;
		    }
		    for(Text value:cache){
		    	String[] val=value.toString().split(";");
		    	String[] str1=val[1].split(",");
		    	double num_of_times=Integer.parseInt(str1[0]);
		    	double total_num=Integer.parseInt(str1[1]);
		    	TF=num_of_times/total_num;
		    	keyValue.set(key+";"+val[0]);
		    	IDF=(double) Math.log(224/sum+1);
		        double TF_IDF=TF*IDF;
		        //BigDecimal d1=new BigDecimal(Double.valueOf(TF_IDF));
			    Value.set(TF_IDF);
		    	context.write(keyValue, Value);
		    }
		}
	}
	
	public static class Mapper4 extends Mapper<Object, Text, DoubleWritable, Text>{
		public void map(Object key, Text values, Context context) throws IOException, InterruptedException{
			String line=values.toString();
			StringTokenizer itr = new StringTokenizer(line);
			DoubleWritable Value=new DoubleWritable();
			Text keyValue=new Text();
			while(itr.hasMoreTokens()){
				String wf=itr.nextToken().toString();
				Value.set(Double.valueOf(itr.nextToken().toString()));
				keyValue.set(wf);
				context.write(Value, keyValue);
			}
		}
	}
	
	public static class Reducer4 extends Reducer<DoubleWritable, Text, Text, Text>{
		public void reduce(DoubleWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			Text keyValue=new Text();
			Text Value=new Text();
			for(Text line:values) {
				keyValue.set(line);
				Value.set(key.toString());
				context.write(keyValue, Value);
			}
		}
	}
/*	public static class Mapper4 extends Mapper<Object, DoubleWritable, Text, DoubleWritable>{
		public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException{
			//ArrayList<DoubleWritable> Value=new ArrayList<DoubleWritable>();
			//DoubleWritable Value=new DoubleWritable();
			ArrayList<DoubleWritable> str=new ArrayList<DoubleWritable>();
			for(DoubleWritable val : values) {
				str.add(val);//sum the number of the same character
			}
			Collections.sort(str);
			System.out.println(str);
			for(DoubleWritable num:str) {
				context.write(key, num);
			}
		}
	}
	*/
	public static class myComparator extends IntWritable.Comparator {
		@SuppressWarnings("rawtypes")
		public int compare (WritableComparable a,WritableComparable b){
			return -super.compare(a,b);
		}
		public int compare(byte[] b1, int s1,int l1,byte[] b2, int s2, int l2) {
			return -super.compare(b1, s1,l1,b2,s2,l2);
		}
	}
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("config/log4j.properties");
		Configuration conf = new Configuration();
		
		
		Job job = Job.getInstance(conf, "word3");
		job.setJarByClass(T3.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job,  new Path("hdfs://master:9000/data/dataset"));
		FileOutputFormat.setOutputPath(job,  new Path("hdfs://master:9000/output/Q3_result_1"));
		ControlledJob ctrlJob1= new ControlledJob(conf);
		ctrlJob1.setJob(job);
		
		Job job2 = Job.getInstance(conf, "word3");
		job2.setJarByClass(T3.class);
		job2.setMapperClass(Mapper2.class);
		job2.setReducerClass(Reducer2.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job2,  new Path("hdfs://master:9000/output/Q3_result_1"));
		FileOutputFormat.setOutputPath(job2,  new Path("hdfs://master:9000/output/Q3_result_2"));
		ControlledJob ctrlJob2= new ControlledJob(conf);
		ctrlJob2.setJob(job2);
		
		Job job3 = Job.getInstance(conf, "word3");
		job3.setJarByClass(T3.class);
		job3.setMapperClass(Mapper3.class);
		//job3.setCombinerClass(Combiner3.class);
		job3.setReducerClass(Reducer3.class);
		job3.setMapOutputValueClass(Text.class);
		job3.setOutputKeyClass(Text.class);
		job3.setOutputValueClass(DoubleWritable.class);
		//job3.setSortComparatorClass(myComparator.class);
		FileInputFormat.addInputPath(job3,  new Path("hdfs://master:9000/output/Q3_result_2"));
		FileOutputFormat.setOutputPath(job3,  new Path("hdfs://master:9000/output/Q3_result_3"));
		ControlledJob ctrlJob3= new ControlledJob(conf);
		ctrlJob3.setJob(job3);
		
		Job job4 = Job.getInstance(conf, "word3");
		job4.setJarByClass(T3.class);
		job4.setMapperClass(Mapper4.class);
		job4.setReducerClass(Reducer4.class);
		job4.setMapOutputKeyClass(DoubleWritable.class);
		job4.setMapOutputValueClass(Text.class);
		//job4.setOutputKeyClass(DoubleWritable.class);
		//job4.setOutputValueClass(Text.class);
		job4.setOutputKeyClass(Text.class);
		job4.setOutputValueClass(DoubleWritable.class);
		job4.setSortComparatorClass(myComparator.class);
		FileInputFormat.addInputPath(job4,  new Path("hdfs://master:9000/output/Q3_result_3"));
		FileOutputFormat.setOutputPath(job4,  new Path("hdfs://master:9000/output/Q3_result_4"));
		ControlledJob ctrlJob4= new ControlledJob(conf);
		ctrlJob4.setJob(job4);
		
		
		ctrlJob2.addDependingJob(ctrlJob1);
		ctrlJob3.addDependingJob(ctrlJob2);
		ctrlJob4.addDependingJob(ctrlJob3);
		JobControl jobCtrl=new JobControl("myCtrl");
		jobCtrl.addJob(ctrlJob1);
		jobCtrl.addJob(ctrlJob2);
		jobCtrl.addJob(ctrlJob3);
		jobCtrl.addJob(ctrlJob4);
		Thread thread=new Thread(jobCtrl);
		thread.start();
		while(true) {
			if(jobCtrl.allFinished()) {
				System.out.println(jobCtrl.getSuccessfulJobList());
				jobCtrl.stop();
				break;
			}
		}
		
		System.exit(job.waitForCompletion(true) ? 0:1);
	}
}
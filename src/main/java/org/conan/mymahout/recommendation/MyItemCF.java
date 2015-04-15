package org.conan.mymahout.recommendation;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.elex.util.MahoutUtil;

public class MyItemCF {
	
	public static class UserItemMap extends Mapper<LongWritable, Text, Text, Text>{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			StringTokenizer strTMP = new StringTokenizer(value.toString(), "\t");
			String user = strTMP.nextToken();
			String item = strTMP.nextToken();
			String score = strTMP.nextToken();
			Text outputKey = new Text();
			Text outputValue = new Text();
			outputKey.set(user);
			outputValue.set(item + ":" + score);
			context.write(outputKey, outputValue);
		}
	}
	
	public static class UserItemReduce extends Reducer<Text, Text, Text, Text>{
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			Text outputValue = new Text();
			StringBuffer strTMP = new StringBuffer();
			for(Text value:values){
				strTMP.append(value.toString() + ",");
			}
			outputValue.set(strTMP.toString());
			context.write(key, outputValue);
		}
	}
	
	public static class TwoItemMap extends Mapper<LongWritable, Text, Text, Text>{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			StringTokenizer strTMP = new StringTokenizer(value.toString(), "\t");
			String user = strTMP.nextToken();
			String itemScore = strTMP.nextToken();
			String []itemScoreTMP = itemScore.split(",");
			for(int i = 0, len = itemScoreTMP.length; i < len - 1; i++){
				String []itemScoreOne = itemScoreTMP[i].split(":");
				for(int j = i + 1; j < len; j++){
					String []itemScoreTwo = itemScoreTMP[j].split(":");
					Text outputKey1 = new Text();
					Text outputValue1 = new Text();
					outputKey1.set("item\t" + itemScoreOne[0]);
					outputValue1.set(itemScoreTwo[0]);
					context.write(outputKey1, outputValue1);
				}
				Text outputKey2 = new Text();
				Text outputValue2 = new Text();
				outputKey2.set(itemScoreOne[0]);
				outputValue2.set(user + ":" + itemScoreOne[1]);
				context.write(outputKey2, outputValue2);
			}
		}
	}
	
//	public static class TwoItemMap extends Mapper<LongWritable, Text, Text, Text>{
//		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
//			StringTokenizer strTMP = new StringTokenizer(value.toString(), "\t");
//			String user = strTMP.nextToken();
//			String itemScore = strTMP.nextToken();
//			String []itemScoreTMP = itemScore.split(",");
//			for(int i = 0, len = itemScoreTMP.length; i < len - 1; i++){
//				String []itemScoreOne = itemScoreTMP[i].split(":");
//				for(int j = i + 1; j < len; j++){
//					String []itemScoreTwo = itemScoreTMP[j].split(":");
//					Text outputKey1 = new Text();
//					Text outputValue1 = new Text();
//					Text outputKey2 = new Text();
//					Text outputValue2 = new Text();
//					outputKey1.set(user + ":" + itemScoreOne[0]);
//					outputValue1.set(itemScoreTwo[0]);
//					outputKey2.set(user + ":" + itemScoreTwo[0]);
//					outputValue2.set(itemScoreOne[0]);
//					context.write(outputKey1, outputValue1);
//					context.write(outputKey2, outputValue2);
//				}
//				Text outputKey = new Text();
//				Text outputValue = new Text();
//				outputKey.set(user + ":" + itemScoreOne[0]);
//				outputValue.set(itemScoreOne[0] + "\t" + itemScoreOne[1]);
//				context.write(outputKey, outputValue);
//			}
//		}
//	}
//	
	
	public static class TwoItemReduce extends Reducer<Text, Text, Text, Text>{
		private MultipleOutputs<Text,Text> mos;
		public void setup(Context context){
			mos = new MultipleOutputs<Text,Text>(context);
		}
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			String []keyStr = key.toString().split("\t");
			for(Text value:values){
				mos.write("coexistencematrix", key, value);
			}
//			if(keyStr.length == 2){
//				String outputValueTMP = MahoutUtil.getItemCount(values);
//				Text outputValue = new Text();
//				Text outputKey = new Text();
//				outputKey.set(keyStr[1]);
//				outputValue.set(outputValueTMP);
//				mos.write("coexistencematrix", outputKey, outputValue);
//			} else{
//				StringBuffer outputValueTMP = new StringBuffer();
//				for(Text value:values){
//					outputValueTMP.append(value.toString() + ",");
//				}
//				Text outputValue = new Text();
//				mos.write("itemuser", key, outputValue);
//			}
		}
	}
	
//	public static class TwoItemReduce extends Reducer<Text, Text, Text, Text>{
//		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
//			float weight = MahoutUtil.getWeight(values);
//			Text outputValue  = new Text();
//			outputValue.set(Float.toString(weight));
//			context.write(key, outputValue);
//		}
//	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "data cleaning");
		job.setJarByClass(MyItemCF.class);
		job.setMapperClass(UserItemMap.class);
		job.setReducerClass(UserItemReduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job, new Path("/tmp/intro"));
		FileOutputFormat.setOutputPath(job, new Path("/tmp/myItemCF1"));
		job.waitForCompletion(true);
		
		Job job1 = Job.getInstance(conf, "data cleaning");
		job1.setJarByClass(MyItemCF.class);
		job1.setMapperClass(TwoItemMap.class);
		job1.setReducerClass(TwoItemReduce.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job1, new Path("/tmp/myItemCF1"));
		MultipleOutputs.addNamedOutput(job1, "coexistencematrix", TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job1, "itemuser", TextOutputFormat.class, Text.class, Text.class);
		FileOutputFormat.setOutputPath(job1, new Path("/tmp/myItemCF"));
		System.exit(job1.waitForCompletion(true)?0:1);
	}

}

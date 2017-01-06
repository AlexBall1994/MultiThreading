package cmsc433.p5;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import cmsc433.p5.TweetPopularityMR.PopularityReducer;
import cmsc433.p5.TweetPopularityMR.TweetMapper;

/**
 * Map reduce which sorts the output of {@link TweetPopularityMR}.
 * The input will either be in the form of: </br>
 * 
 * <code></br>
 * &nbsp;(screen_name,  score)</br>
 * &nbsp;(hashtag, score)</br>
 * &nbsp;(tweet_id, score)</br></br>
 * </code>
 * 
 * The output will be in the same form, but with results sorted on the score.
 * 
 */
public class TweetSortMR {

	/**
	 * Minimum <code>int</code> value for a pair to be included in the output.
	 * Pairs with an <code>int</code> less than this value are omitted.
	 */
	private static int CUTOFF = 10;

	public static class SwapMapper
	extends Mapper<Object,Text, IntWritable, Text> {
		private Text word = new Text();
		String      id;
		int         score;

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] columns = value.toString().split("\t");
			id = columns[0];
			score = Integer.valueOf(columns[1]);
			
			// TODO: Your code goes here
			if (score >= CUTOFF){
				score = score *-1;
				word.set(id);
				IntWritable writableValue = new IntWritable(score);
				context.write( writableValue, word);

			}
		}
	}

	public static class SwapReducer
	extends Reducer<IntWritable, Text, Text, IntWritable> {

		@Override
		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			int newVal = key.get();
			newVal = newVal * -1;
			IntWritable v = new IntWritable(newVal);
			for (Text val : values) {
				context.write(val, v);
			}
		}
	}

	public static class ReverseSort extends WritableComparator {
		protected ReverseSort(){
			super(IntWritable.class, true);
		}
		@SuppressWarnings("rawtypes")
	    @Override
		public int compare(WritableComparable w1, WritableComparable w2){
			IntWritable v1 = (IntWritable) w1;
			IntWritable v2 = (IntWritable) w2;
			return -1 * v1.compareTo(v2);
		}
	}


	/**
	 * This method performs value-based sorting on the given input by configuring
	 * the job as appropriate and using Hadoop.
	 * 
	 * @param job
	 *          Job created for this function
	 * @param input
	 *          String representing location of input directory
	 * @param output
	 *          String representing location of output directory
	 * @return True if successful, false otherwise
	 * @throws Exception
	 */
	public static boolean sort(Job job, String input, String output, int cutoff)
			throws Exception {

		CUTOFF = cutoff;

		job.setJarByClass(TweetSortMR.class);
		job.setReducerClass(SwapReducer.class);
		job.setMapperClass(SwapMapper.class);

		// TODO: Set up map-reduce...
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);


		job.setInputFormatClass(TextInputFormat.class);		
		job.setOutputFormatClass(TextOutputFormat.class);
		//job.setSortComparatorClass(ReverseSort.class);
		
		// End

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job.waitForCompletion(true);
	}
}

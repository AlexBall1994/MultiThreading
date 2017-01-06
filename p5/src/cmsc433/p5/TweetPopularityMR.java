package cmsc433.p5;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Map reduce which takes in a CSV file with tweets as input and output
 * key/value pairs.</br>
 * </br>
 * The key for the map reduce depends on the specified {@link TrendingParameter}
 * , <code>trendingOn</code> passed to
 * {@link #score(Job, String, String, TrendingParameter)}).
 */
public class TweetPopularityMR {

	// For your convenience...
	public static final int          TWEET_SCORE   = 1;
	public static final int          RETWEET_SCORE = 2;
	public static final int          MENTION_SCORE = 1;
	public static final int			 PAIR_SCORE = 1;

	// Is either USER, TWEET, HASHTAG, or HASHTAG_PAIR. Set for you before call to map()
	private static TrendingParameter trendingOn;
	private final static IntWritable one = new IntWritable(1);
	private final static IntWritable two = new IntWritable(2);


	public static class TweetMapper
	extends Mapper<LongWritable, Text, Text, IntWritable> {
		private Text word = new Text();
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// Converts the CSV line into a tweet object
			Tweet tweet = Tweet.createTweet(value.toString());
			// TODO: Your code goes here

			if (trendingOn == TrendingParameter.HASHTAG){
				for (int i  = 0; i < tweet.getHashtags().size(); i++){
					word.set(tweet.getHashtags().get(i));
					context.write(word, one);


				}
			}
			if (trendingOn == TrendingParameter.USER){
				for(int i = 0; i < tweet.getMentionedUsers().size(); i++){
					word.set(tweet.getMentionedUsers().get(i));
					context.write(word, one);

				}
				if (tweet.wasRetweetOfTweet()){

					word.set(tweet.getRetweetedUser());
					context.write(word, two);
				}
				else {
					if(tweet.getUserScreenName().equals("Calum5SOS"))
						System.out.println("Hir");
					word.set(tweet.getUserScreenName());
					context.write(word, one);
				}

			}
			if (trendingOn == TrendingParameter.HASHTAG_PAIR){
				if(tweet.getHashtags().size() < 2){

				}
				else {
					Collections.sort(tweet.getHashtags(), new Comparator<String>() {
						@Override
						public int compare(String s1, String s2) {
							return s1.compareTo(s2);
						}
					});

					for(int i = 0; i < tweet.getHashtags().size(); i++){
						String pair = "("+tweet.getHashtags().get(i)+",";
						

						for(int j = i+1; j < tweet.getHashtags().size(); j++){
							if (tweet.getHashtags().get(i).compareTo(tweet.getHashtags().get(j)) < 0){
								pair = "("+tweet.getHashtags().get(i)+",";
								pair = pair.concat(tweet.getHashtags().get(j));
								pair = pair.concat(")");
							}
							else {
								pair = "("+tweet.getHashtags().get(j)+",";
								pair = pair.concat(tweet.getHashtags().get(i));
								pair = pair.concat(")");
							}
							
							word.set(pair);
							context.write(word, one);
						}
					}
				}
			}
			if (trendingOn == TrendingParameter.TWEET){

				if (!tweet.wasRetweetOfTweet()){
					String id = Long.toString(tweet.getId());
					word.set(id);
					context.write(word, one);

				}
				else {

					String id = Long.toString(tweet.getRetweetedTweet());
					word.set(id);
					context.write(word, two);
				}

			}
		}
	}


	public static class PopularityReducer
	extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {

			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			if (sum > 20 ){
					//System.out.println("key:"+ key+ " " + sum);
			}

			context.write(key, new IntWritable(sum));
		}
	}

	/**
	 * Method which performs a map reduce on a specified input CSV file and
	 * outputs the scored tweets, users, or hashtags.</br>
	 * </br>
	 * 
	 * @param job
	 * @param input
	 *          The CSV file containing tweets
	 * @param output
	 *          The output file with the scores
	 * @param trendingOn
	 *          The parameter on which to score
	 * @return true if the map reduce was successful, false otherwise.
	 * @throws Exception
	 */
	public static boolean score(Job job, String input, String output,
			TrendingParameter trendingOn) throws Exception {

		TweetPopularityMR.trendingOn = trendingOn;

		job.setJarByClass(TweetPopularityMR.class);

		// TODO: Set up map-reduce...
		// Set key, output classes for the job.
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapperClass(TweetMapper.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setReducerClass(PopularityReducer.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		// End

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job.waitForCompletion(true);
	}
}

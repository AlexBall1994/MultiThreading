package cmsc433.p5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

public class Main {
	/**
	 * Temporary directory used to store output from TweetPopularityMR to feed as
	 * input into TweetSortMR
	 */
	protected static final String TEMP_DIR = "aaaaaasa";

	/**
	 * Entry-point for the program. Should accept three command line arguments:
	 * <ul>
	 * <li><code>param </code>The parameter on which to map reduce the data set.
	 * The choices are 'user', 'tweet', 'hashtag', or 'hashtag_pair'. For example, if 'tweet' is
	 * specified, then the map reduce will rank the tweets.</li>
	 * <li><code>cutoff </code>The min score required for an entry in order for it
	 * to be listed in the results of the map reduce.</li>
	 * <li><code>in </code>Path to directory containing posts to match</li>
	 * <li><code>out </code>Path to output directory to store final result</li>
	 * </ul>
	 * The final output should be a list of post titles followed by their
	 * relevance count sorted in descending order.
	 */
	
	public static int a;
	public static int b;
	public static Object l = new Object();
	
	public static Runnable r = new Runnable(){
		public void run () {
			synchronized(l) {
				b++;
			}
			a =1;
		}
	};
	
	public static void main(String[] args) throws Exception {
		// Create configuration and parse arguments
		new Thread(r).start();
		synchronized(l){
			b--;
		}
		a=2;
		System.out.println(a);
		System.out.println(b);
	}
}

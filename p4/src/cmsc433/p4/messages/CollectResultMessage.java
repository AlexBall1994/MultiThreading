package cmsc433.p4.messages;

import cmsc433.p4.support.IntegerCollector;

/**
 * Class for returning result of collect operation. The embedded collector
 * contains the result.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 * @param <T>
 *            Type of the result
 */
public class CollectResultMessage<T> {

	private final IntegerCollector<T> collector;

	public CollectResultMessage(CollectRequestMessage<T> req) {
		this.collector = req.getIntegerCollector();
	}

	public IntegerCollector<T> getIntegerCollector() {
		return collector;
	}
}

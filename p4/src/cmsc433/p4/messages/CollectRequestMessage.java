package cmsc433.p4.messages;

import akka.actor.ActorRef;
import cmsc433.p4.support.IntegerCollector;

/**
 * Class of messages that request a collect operation on a list.  A collect operation traverses the
 * list, processing the data and collecting it into a single result.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 * @param <T>	The type of the result being computed
 */
public class CollectRequestMessage<T> {
	private final IntegerCollector<T> collector;	// The collector class
	private final ActorRef originator;				// The list to which the initial collect operation was applied
	private final ActorRef replyTo;					// The actor to send the result to
	
	/**
	 * Constructor that generates a collect request message, when the originator and replyTo fields
	 * are not yet known.
	 * 
	 * @param collector
	 */
	public CollectRequestMessage(IntegerCollector<T> collector) {
		this.collector = collector;
		this.originator = null;
		this.replyTo = null;
	}
	
	/**
	 * Constructor for generating a new request message from an existing one.  The new one
	 * includes the given originator and replyTo values.
	 * 
	 * @param msg
	 * @param originator
	 * @param replyTo
	 */
	public CollectRequestMessage(CollectRequestMessage<T> msg, ActorRef originator, ActorRef replyTo) {
		this.collector = msg.collector;
		this.originator = originator;
		this.replyTo = replyTo;
	}
	
	public IntegerCollector<T> getIntegerCollector() {
		return collector;
	}
	
	public ActorRef getOriginator() {
		return originator;
	}
	
	public ActorRef getReplyTo() {
		return replyTo;
	}
}

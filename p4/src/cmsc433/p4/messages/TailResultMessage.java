package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of messages for returning the tail field of a cons actor.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class TailResultMessage {

	private final ActorRef tail;
	
	public TailResultMessage (ActorRef tail) {
		this.tail = tail;
	}
	
	public ActorRef getTail() {
		return tail;
	}
}

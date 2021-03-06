package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of messages for returning the result of creating a cons actor.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ConsActorResultMessage {
	
	private final ActorRef consActor;
	
	public ConsActorResultMessage (ActorRef consActor) {
		this.consActor = consActor;
	}
	
	public ActorRef getConsActor() {
		return consActor;
	}
 
}

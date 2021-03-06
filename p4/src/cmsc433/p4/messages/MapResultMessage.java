package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of messages containing the result of a map operation. The result in
 * this case is the actor corresponding to the new list that was constructed.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class MapResultMessage {
	private ActorRef result;

	public MapResultMessage(ActorRef listActor) {
		this.result = listActor;
	}

	public ActorRef getResult() {
		return result;
	}
}

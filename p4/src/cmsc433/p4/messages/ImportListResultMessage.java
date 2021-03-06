package cmsc433.p4.messages;

import akka.actor.ActorRef;

/**
 * Class of messages for returning result of an import operation. What is
 * returned is either a null actor, if the original list was empty, or the cons
 * actor corresponding to the first element in the list, if the origina list was
 * non-empty.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ImportListResultMessage {
	private final ActorRef listActor;

	public ImportListResultMessage(ActorRef listActor) {
		this.listActor = listActor;
	}

	public ActorRef getListActor() {
		return listActor;
	}
}

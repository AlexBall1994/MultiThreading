package cmsc433.p4.messages;

import akka.actor.ActorRef;
import cmsc433.p4.support.IntegerCompute;

/**
 * Class of messages requesting that a given map operation be applied to a(n
 * actor-based) list. The fields include an IntegerComputer object, which
 * contains the operation to be applied to each element in the list, and the
 * list to which the operation is to be applied.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class MapRequestMessage {
	private final IntegerCompute compute;
	private final ActorRef argumentListActor; // List to apply compute function
												// to.

	/**
	 * Constructor for constructing a MapRequestMessage from compute and list
	 * objects.
	 * 
	 * @param compute
	 * @param listActor
	 */
	public MapRequestMessage(IntegerCompute compute, ActorRef listActor) {
		this.compute = compute;
		this.argumentListActor = listActor;
	}

	/**
	 * Constructor for constructing a new MapRequestMessage from a given message
	 * and a new list. The new message is the same as the old, except that the
	 * list value is changed.
	 * 
	 * @param msg
	 * @param listActor
	 */
	public MapRequestMessage(MapRequestMessage msg, ActorRef listActor) {
		this.compute = msg.compute;
		this.argumentListActor = listActor;
	}

	public ActorRef getArgumentListActor() {
		return argumentListActor;
	}

	public IntegerCompute getIntegerCompute() {
		return compute;
	}
}

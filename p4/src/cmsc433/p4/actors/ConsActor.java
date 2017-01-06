package cmsc433.p4.actors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.*;

/**
 * Class of actors corresponding to cons ("internal") nodes in applicative
 * lists. Each such actor stores a head field and a tail field; the later is a
 * reference to an actor that manages the rest of the list.
 * 
 * You must complete the implementations of makeAndDeliver() and onReceive().
 * You may add new private fields and methods if you wish.
 * 
 * @author Rance Cleaveland
 *
 */
public class ConsActor extends UntypedActor {

	/**
	 * Props structure-generator for this class.
	 * 
	 * @param head
	 *            head of the list the new node will manage
	 * @param tail
	 *            rest of the list
	 * @return Props structure for creating actor
	 */
	static Props props(Integer head, ActorRef tail) {
		return Props.create(ConsActor.class, head, tail);
	}

	private final Integer head; // Head of list being managed by cons actor
	private final ActorRef tail; // Actor managing tail of this list

	private int referenceCount = 0; // Reference count

	private ConsActor(Integer head, ActorRef tail) {
		this.head = head;
		this.tail = tail;

	}

	/**
	 * Factory method for making ConsActors. Ensures update of reference count
	 * of tail.
	 * 
	 * @param head
	 *            Integer to store in new node
	 * @param tail
	 *            Tail node
	 * @param destination
	 *            Actor to deliver constructed node to
	 * @param context
	 *            Actor context used to "start" new node
	 */
	public static void makeAndDeliver(Integer head, ActorRef tail, ActorRef destination, ActorContext context) {

		// Fill in your implementation here
		ActorRef link = context.actorOf(ConsActor.props(head, tail));
		tail.tell(new ReferenceCountIncrementMessage(), context.self());
		ConsActorResultMessage  n = new ConsActorResultMessage(link);

		destination.tell(n, context.self());

	}

	@Override
	public void onReceive(Object msg) throws Exception {

		// Fill in your implementation here

		if (msg instanceof ReferenceCountRequestMessage) {			
			getSender().tell(new ReferenceCountResultMessage(referenceCount), getSelf());	
		}
		else if (msg instanceof CollectRequestMessage){
			
			CollectRequestMessage req = (CollectRequestMessage) msg;
			req.getIntegerCollector().collect(this.head);
			this.tail.tell(req, getSender());
		}
		else if (msg instanceof ReferenceCountIncrementMessage){
			referenceCount++;
		}
		else if (msg instanceof TailRequestMessage){
			tail.tell(new ReferenceCountIncrementMessage(), getSelf());
			getSender().tell(new TailResultMessage(this.tail), getSelf());
		}
		else if (msg instanceof HeadRequestMessage){
			getSender().tell(new HeadResultMessage(this.head), getSelf());
		}
		else if (msg instanceof ReferenceDecrementRequestMessage){
			referenceCount--;
			if (referenceCount == 0){
				tail.tell(new ReferenceDecrementRequestMessage(), getSelf());
				this.getContext().stop(this.getSelf());
			}
		}
		else if (msg instanceof MapRequestMessage){


		}
		else if (msg instanceof NullStatusRequestMessage){
			NullStatusResultMessage m = new NullStatusResultMessage(false);
			getSender().tell(m, getSelf());
		}

	}
}

package cmsc433.p4.actors;

import java.util.List;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.ConsActorRequestMessage;
import cmsc433.p4.messages.ConsActorResultMessage;
import cmsc433.p4.messages.ImportListRequestMessage;
import cmsc433.p4.messages.MapProcessingStartMessage;
import cmsc433.p4.messages.MapRequestMessage;
import cmsc433.p4.messages.NullActorRequestMessage;
import cmsc433.p4.messages.NullActorResultMessage;

/**
 * Class of actors for managing lists. The primary task of this agent is to
 * allocate new cons actors and null actors and send them to those requesting
 * them. These actors are supervised by this actor.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ListManagerActor extends UntypedActor {

	/**
	 * reply sends given message back to sender of current message.
	 * 
	 * @param msg
	 *            Message to send back.
	 */
	private void reply(Object msg) {
		getSender().tell(msg, getSelf());
	}

	private ActorContext context = getContext(); // Context of this actor.

	@Override
	public void onReceive(Object msg) throws Exception {

		// This message indicates another actor has requested the creation of a
		// new
		// cons actor with the given head and tail. Create this actor and send
		// it
		// back. Since the reference count for the tail must be incremented, the
		// return of the actor is delegated to the tail.

		if (msg instanceof ConsActorRequestMessage) {
			ConsActorRequestMessage payload = (ConsActorRequestMessage) msg;
			Integer head = payload.getHead();
			ActorRef tail = payload.getTail();
			ConsActor.makeAndDeliver(head, tail, getSender(), context);
		}

		// This message indicates that a list object should be converted into an
		// actor-based one. The list manager creates a special actor and
		// delegates this
		// task to that actor.

		else if (msg instanceof ImportListRequestMessage) {
			List<Integer> list = ((ImportListRequestMessage) msg).getList();
			Props delegateProps = ImportListProcessingActor.props(list, getSelf(), getSender());
			ActorRef delegate = context.actorOf(delegateProps);
			delegate.tell(new MapProcessingStartMessage(), null); // Start the
																	// new actor
		}

		// Requests for map come to the list manager because map requires
		// creation of
		// new cons actors, in general. The list manager creates a special
		// actor to process this request.

		else if (msg instanceof MapRequestMessage) {
			MapRequestMessage payload = (MapRequestMessage) msg;
			Props mapProps = MapProcessingActor.props(getSelf(), getSender(), payload);
			ActorRef delegate = context.actorOf(mapProps);
			delegate.tell(new MapProcessingStartMessage(), null);
		}

		// This message indicates someone is requesting a new null actor. Create
		// it and send it
		// back.

		else if (msg instanceof NullActorRequestMessage) {
			ActorRef nullActor = context.actorOf(NullActor.props);
			reply(new NullActorResultMessage(nullActor));
		}

		// Print error message and call unhandled() if any other message is
		// encountered.

		else {
			System.out.printf("Bad message to ListManagerActor:  %s%n", msg.toString());
			unhandled(msg); // Recommended practice
		}
	}
}

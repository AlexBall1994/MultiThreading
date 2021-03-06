package cmsc433.p4.actors;

import java.util.List;
import java.util.ListIterator;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import cmsc433.p4.messages.ConsActorRequestMessage;
import cmsc433.p4.messages.ConsActorResultMessage;
import cmsc433.p4.messages.ImportListResultMessage;
import cmsc433.p4.messages.MapProcessingStartMessage;
import cmsc433.p4.messages.NullActorRequestMessage;
import cmsc433.p4.messages.NullActorResultMessage;

/**
 * Class of actors for converting a Java List object into an actor-based
 * representation, with cons actors and null actors.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ImportListProcessingActor extends UntypedActor {

	static public Props props(List<Integer> list, ActorRef listManager, ActorRef replyTo) {
		return Props.create(ImportListProcessingActor.class, list, listManager, replyTo);
	}

	private List<Integer> list; // List of values to translate into actors.
	private ListIterator<Integer> iterator; // Position in list
	private ActorRef listManager; // Actor for creating cons, null actors
	private ActorRef replyTo; // Actor to send result (i.e. cons actor) to

	public ImportListProcessingActor(List<Integer> list, ActorRef listManager, ActorRef replyTo) {
		this.list = list;
		this.listManager = listManager;
		this.replyTo = replyTo;
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		// Message sent by list-manager actor starting processing. List will
		// be constructed back to front, so we ask for null (terminal) actor
		// first and also position iterator at end.

		if (msg instanceof MapProcessingStartMessage) { // Ask for null actor
			listManager.tell(new NullActorRequestMessage(), getSelf());
			iterator = list.listIterator(list.size());
		}

		// When null actor is delivered, and while there are nodes in list to
		// process, get element from list, move iterator back, and ask for a new
		// cons actor.

		else if (msg instanceof NullActorResultMessage) {
			ActorRef nullActor = ((NullActorResultMessage) msg).getNullActor();
			if (iterator.hasPrevious()) {
				Integer head = iterator.previous();
				listManager.tell(new ConsActorRequestMessage(head, nullActor), getSelf());
			} else { // Construction complete!
				replyTo.tell(new ImportListResultMessage(nullActor), listManager);
				getContext().stop(getSelf());
			}
		}

		// When new cons actor is delivered, repeat process of moving list
		// iterator
		// backwards and requesting another cons actor if needed.

		else if (msg instanceof ConsActorResultMessage) {
			ActorRef consActor = ((ConsActorResultMessage) msg).getConsActor();
			if (iterator.hasPrevious()) {
				Integer head = iterator.previous();
				listManager.tell(new ConsActorRequestMessage(head, consActor), getSelf());
			} else { // Construction complete!
				replyTo.tell(new ImportListResultMessage(consActor), listManager);
				getContext().stop(getSelf());
			}
		}

		// Print error message and call unhandled() if any other message is
		// encountered.

		else {
			System.out.printf("Bad message to ImportListProcessingActor:  %s%n", msg.toString());
			unhandled(msg);
		}
	}
}

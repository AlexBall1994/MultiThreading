package cmsc433.p4.messages;

/**
 * Class of messages used to return the head value of a cons actor.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class HeadResultMessage {
	private final Integer head;
	
	public HeadResultMessage(Integer head) {
		this.head = head;
	}
	
	public Integer getHead() {
		return head;
	}

}

package cmsc433.p4.messages;

import java.util.List;

/**
 * Class of messages for requesting the import of a Java List object into an
 * actor-based representation based on cons and null actors.
 * 
 * DO NOT CHANGE THIS FILE!
 * 
 * @author Rance Cleaveland
 *
 */
public class ImportListRequestMessage {

	private List<Integer> list;

	public ImportListRequestMessage(List<Integer> list) {
		this.list = list;
	}

	public List<Integer> getList() {
		return list;
	}
}

package ttu.vorgu2.hw1;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean ok;
	
	private List<String> groups = null;
	
	private long userId;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Message(boolean ok) {
		this.ok = ok;
	}
	
	public Message(boolean ok, List<String> groups) {
		this.ok = ok;
		this.groups = groups;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	

}

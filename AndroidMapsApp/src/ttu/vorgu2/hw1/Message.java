package ttu.vorgu2.hw1;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean ok;
	private long userId = 0;
    private List<String> groups = null;
    
	public Message(boolean ok) {
		this.ok = ok;
	}
	
	public Message(boolean ok, List<String> groups) {
		this.ok = ok;
		this.groups = groups;
	}
	
	public Message(boolean ok, long userId, List<String> groups) {
		this.ok = ok;
		this.userId = userId;
		this.groups = groups;
	}

	/**
	 * @return the ok
	 */
	public boolean isOk() {
		return ok;
	}

	/**
	 * @param ok the ok to set
	 */
	public void setOk(boolean ok) {
		this.ok = ok;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}

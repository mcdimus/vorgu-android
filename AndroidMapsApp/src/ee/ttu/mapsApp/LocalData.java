package ee.ttu.mapsApp;

import java.util.List;

/**
 * Class serves as local data storage for android application.
 * 
 * Oct 19, 2011
 * 
 * @author Dmitri Maksimov
 * 
 */
public class LocalData {

	/**
	 * Current user id.
	 */
	private static long userId;
	/**
	 * Current user name.
	 */
	private static String username = null;
	/**
	 * List with available groups.
	 */
	private static List<String> groups = null;
	/**
	 * User`s current group.
	 */
	private static String myGroup = null;

	/**
	 * Get user id.
	 * 
	 * @return the userId
	 */
	public static long getUserId() {
		return userId;
	}

	/**
	 * Set user id.
	 * 
	 * @param userId
	 *            the userId to set
	 */
	public static void setUserId(long userId) {
		LocalData.userId = userId;
	}

	/**
	 * Get user name.
	 * 
	 * @return the username
	 */
	public static String getUsername() {
		return username;
	}

	/**
	 * Set user name.
	 * 
	 * @param username
	 *            the username to set
	 */
	public static void setUsername(String username) {
		LocalData.username = username;
	}

	/**
	 * Get groups.
	 * 
	 * @return the groups
	 */
	public static List<String> getGroups() {
		return groups;
	}

	/**
	 * Set groups.
	 * 
	 * @param groups
	 *            the groups to set
	 */
	public static void setGroups(List<String> groups) {
		LocalData.groups = groups;
	}

	/**
	 * Get group of current user.
	 * 
	 * @return the myGroup
	 */
	public static String getMyGroup() {
		return myGroup;
	}

	/**
	 * Set group for current user.
	 * 
	 * @param myGroup
	 *            the myGroup to set
	 */
	public static void setMyGroup(String myGroup) {
		LocalData.myGroup = myGroup;
	}

}

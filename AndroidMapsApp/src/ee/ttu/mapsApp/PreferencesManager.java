package ee.ttu.mapsApp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class wrapper for data manipulation stored in preferences file.
 * 
 * Oct 19, 2011
 * 
 * @author Dmitri Maksimov
 * 
 */
public class PreferencesManager {

	/**
	 * Instance of a context, where PreferencesManger is working.
	 */
	private Context context;

	private static final String PREFS_NAME = "AppSettings";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_GROUP = "myGroup";

	/**
	 * Constructor. Creates new PreferencesManager for the specified context.
	 * 
	 * @param context
	 *            - context where PreferencesManager will be working.
	 */
	public PreferencesManager(Context context) {
		this.context = context;
	}

	/**
	 * Check if there is no stored data in the preferences file.
	 * 
	 * @return true, if there is no data.
	 */
	public boolean isEmpty() {
		SharedPreferences preferences = getSharedPrefs();
		String username = preferences.getString(PREF_USERNAME, null);
		String password = preferences.getString(PREF_PASSWORD, null);

		// if there are data in the file
		if (username != null && password != null) {
			return false;
		}

		return true;
	}
	
	/**
	 * Removes the saved preferences.
	 */
	public void clearPreferences() {
		SharedPreferences preferences = getSharedPrefs();
		preferences.edit().clear().commit();
	}
	
	/**
	 * Store username in the preferences file.
	 * 
	 * @param username
	 *            - username to store.
	 */
	public void putUsername(String username) {
		getSharedPrefs().edit().putString(PREF_USERNAME, username).commit();
	}
	
	/**
	 * Get username.
	 * 
	 * @return current username or null if there is no data.
	 */
	public String getUsername() {
		SharedPreferences preferences = getSharedPrefs();

		String username = preferences.getString(PREF_USERNAME, null);

		if (username != null) {
			return username;
		}
		return null;
	}
	
	/**
	 * Store group in the preferences file.
	 * 
	 * @param myGroup
	 *            - group to store.
	 */
	public void putGroup(String myGroup) {
		getSharedPrefs().edit().putString(PREF_GROUP, myGroup).commit();
	}
	
	/**
	 * Get group.
	 * 
	 * @return current group or null if there is no data.
	 */
	public String getGroup() {
		SharedPreferences preferences = getSharedPrefs();

		String myGroup = preferences.getString(PREF_GROUP, null);

		if (myGroup != null) {
			return myGroup;
		}
		return null;
	}
	

	/**
	 * Store username and password in the preferences file.
	 * 
	 * @param username
	 *            - username to store.
	 * @param password
	 *            - password to store.
	 */
	public void putUsernameAndPassword(String username, String password) {
		getSharedPrefs().edit().putString(PREF_USERNAME, username)
				.putString(PREF_PASSWORD, password).commit();
	}

	/**
	 * Get username and password as parameters for html request. Example:
	 * "username=vasja&password=qwerty"
	 * 
	 * @return parameters for html request; <b>null</b> if no data found.
	 */
	public String getUsernameAndPasswordAsParams() {
		SharedPreferences preferences = getSharedPrefs();

		String username = preferences.getString(PREF_USERNAME, null);
		String password = preferences.getString(PREF_PASSWORD, null);

		if (username != null || password != null) {
			return "username=" + username + "&password=" + password;
		}
		return null;
	}

	/**
	 * Get SharedPreferences for current context. <br>
	 * 
	 * <i> context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); </i> <br>
	 * Method for inner use only.
	 * 
	 * @return SharedPreferences object.
	 */
	private SharedPreferences getSharedPrefs() {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFS_NAME, Context.MODE_PRIVATE);

		return preferences;
	}

}

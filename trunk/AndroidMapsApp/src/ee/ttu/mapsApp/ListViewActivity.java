package ee.ttu.mapsApp;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewActivity extends ListActivity {

	private String parameters;
	private String URL = "http://mcdimus.appspot.com/join_group";
	
	public static final String PREFS_NAME = "UserIdSettings";
	private static final String PREF_USERID= "userid";
	
	protected static List<String> groups;

//	private static String username;
	
	private static String myGroup;
	
	private static long userId;
	
	/**
	 * Object to manipulate preferences file. Store and fetch desired data.
	 */
	private PreferencesManager preferencesManager = new PreferencesManager(this);
	
	public String savedSettings() {
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		String userId = preferences.getString(PREF_USERID, null);
		if (userId != null) {
			return userId;
		}
		return null;
	}

	public static String getMyGroup() {
		return myGroup;
	}

	public static void setMyGroup(String myGroup) {
		ListViewActivity.myGroup = myGroup;
	}

	public static long getUserId() {
		return userId;
	}

	public static void setUserId(long userId) {
		ListViewActivity.userId = userId;
	}

//	public static String getUsername() {
//		return username;
//	}
//
//	public static void setUsername(String username) {
//		ListViewActivity.username = username;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, groups));
		
		getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
        .edit()
        .putString(PREF_USERID, String.valueOf(getUserId()))
        .commit();
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Connection connection = new Connection();
		parameters = "id=" + savedSettings()
				+ "&groupname=" + groups.get(position);
		myGroup = groups.get(position);
		try {
			if (connection.connect(parameters, URL)) {
				Toast.makeText(ListViewActivity.this,
						"Now you are group's " + groups.get(position) + " member!!!", Toast.LENGTH_SHORT)
						.show();
				startActivity(new Intent(ListViewActivity.this,
						AndroidMapsAppActivity.class));
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(ListViewActivity.this, "ERROR: ClientProtocolException.",
					Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			Toast.makeText(ListViewActivity.this, "ERROR: ClassNotFoundException.",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(ListViewActivity.this, "ERROR: IOException.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.join_new_group:
			Toast.makeText(this, "You clicked on Item 1", Toast.LENGTH_LONG)
					.show();
			return true;
		case R.id.create_group:
			startActivity(new Intent(ListViewActivity.this, CreateGroup.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

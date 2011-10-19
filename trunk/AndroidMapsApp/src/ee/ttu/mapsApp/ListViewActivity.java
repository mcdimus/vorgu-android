package ee.ttu.mapsApp;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Intent;
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

	private List<String> groups;
	private String myGroup;

//	public static String getMyGroup() {
//		return myGroup;
//	}
//
//	public static void setMyGroup(String myGroup) {
//		ListViewActivity.myGroup = myGroup;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);

		// redirected from Login Activity
		// get groups from message NB! there SHOULD be groups in messages
		groups = LocalData.getGroups();
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, groups));
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Connection connection = new Connection();
		// parameters for joining group with <groupname>; 
		// <id> is needed to change group on user with <id>
		parameters = "id=" + LocalData.getUserId() + "&groupname="
				+ groups.get(position);
		myGroup = groups.get(position);
		LocalData.setMyGroup(myGroup);
		try {
			if (connection.connect(parameters, URL)) {
				Toast.makeText(
						ListViewActivity.this,
						"Now you are group's " + groups.get(position)
								+ " member!!!", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(ListViewActivity.this,
						AndroidMapsAppActivity.class));
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(ListViewActivity.this,
					"ERROR: ClientProtocolException.", Toast.LENGTH_SHORT)
					.show();
		} catch (ClassNotFoundException e) {
			Toast.makeText(ListViewActivity.this,
					"ERROR: ClassNotFoundException.", Toast.LENGTH_SHORT)
					.show();
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

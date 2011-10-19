package ee.ttu.mapsApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroup extends Activity {

	private Button createGroupButton;
	private EditText groupNameEdit;
	private EditText descriptionEdit;
	private String parameters;
	private String URL = "http://mcdimus.appspot.com/new_group";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creategroup);
		this.createGroupButton = (Button) this
				.findViewById(R.id.buttonCreateGroup);
		final Connection connection = new Connection();
		this.createGroupButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				groupNameEdit = (EditText) findViewById(R.id.editGroupName);
				descriptionEdit = (EditText) findViewById(R.id.editGroupDescription);
				parameters = "id="+LocalData.getUserId()+"&creator=" + LocalData.getUsername()
						+ "&groupname=" + groupNameEdit.getText().toString()
						+ "&description="
						+ descriptionEdit.getText().toString();
				try {
					if (connection.connect(parameters, URL)) {
						Toast.makeText(CreateGroup.this,
								"New group has been added!", Toast.LENGTH_SHORT)
								.show();
						LocalData
								.setMyGroup(groupNameEdit.getText().toString());
						
						// TODO: instead of ListViewActvity should open a map.
						startActivity(new Intent(CreateGroup.this,
								ListViewActivity.class));
					}
				} catch (ClientProtocolException e) {
					Toast.makeText(CreateGroup.this,
							"ERROR: ClientProtocolException.",
							Toast.LENGTH_SHORT).show();
				} catch (ClassNotFoundException e) {
					Toast.makeText(CreateGroup.this,
							"ERROR: ClassNotFoundException.",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(CreateGroup.this, "ERROR: IOException.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}

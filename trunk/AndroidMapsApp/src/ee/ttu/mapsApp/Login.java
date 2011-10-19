package ee.ttu.mapsApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Login extends Activity {

	private Button signUpButton;
	private Button logInButton;
	private EditText userNameEdit;
	private EditText passwordEdit;
	private ProgressDialog dialog;
	private String parameters;
	private String URL = "http://mcdimus.appspot.com/login";
	private AlertDialog.Builder builder;
	private CheckBox rememberPassword;

	public static final String PREFS_NAME = "AppSettings";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Connection connection = new Connection();
		if(savedSettings() == null) {
			setContentView(R.layout.login);
			this.signUpButton = (Button) this.findViewById(R.id.buttonSignUp);
			this.logInButton = (Button) this.findViewById(R.id.buttonLogIn);
			this.rememberPassword = (CheckBox) this.findViewById(R.id.chkRememberPassword);
			final Drawable drawable = this.getResources().getDrawable(
					R.drawable.alert_dialog_icon);
			builder = new AlertDialog.Builder(Login.this);
			this.signUpButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(Login.this, Registration.class));
				}
			});
			this.logInButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog = ProgressDialog.show(Login.this, "",
							"Loading. Please wait...", true);
					dialog.setCancelable(true);
					userNameEdit = (EditText) findViewById(R.id.editUserName);
					passwordEdit = (EditText) findViewById(R.id.editPassword);
					parameters = "username=" + userNameEdit.getText().toString()
							+ "&password=" + passwordEdit.getText().toString();
					try {
						if (connection.connect(parameters, URL)) {
							dialog.dismiss();
							ListViewActivity.setUsername(userNameEdit.getText().toString());
							if(rememberPassword.isChecked()) {
								getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
						        .edit()
						        .putString(PREF_USERNAME, userNameEdit.getText().toString())
						        .putString(PREF_PASSWORD, passwordEdit.getText().toString())
						        .commit();
							}
							startActivity(new Intent(Login.this,
									ListViewActivity.class));
						} else {
							dialog.dismiss();
							alertBuilder("Username or password is incorrect",
									"Please, check if they are correctly written.",
									drawable);
						}

					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						dialog.dismiss();
						alertBuilder("Client Protocol Exception",
								"Try again...",
								drawable);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						dialog.dismiss();
						alertBuilder("Class not found",
								"Try again...",
								drawable);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						dialog.dismiss();
						alertBuilder("Connection problem",
								"Check your internet properties and try again.",
								drawable);
					}
				}
			});

		} else {
			parameters = savedSettings();
			
		try {
			if (connection.connect(parameters, URL)) {
				startActivity(new Intent(Login.this, ListViewActivity.class));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

	public void alertBuilder(String title, String text, Drawable drawable) {
		builder.setMessage(text).setIcon(drawable).setCancelable(false)
				.setTitle(title).setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
	}

	public String savedSettings() {
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		String username = preferences.getString(PREF_USERNAME, null);
		String password = preferences.getString(PREF_PASSWORD, null);
		if (username != null || password != null) {
			return "username=" + username + "&password=" + password;
		}
		return null;
	}
}
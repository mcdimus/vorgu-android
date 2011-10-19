package ee.ttu.mapsApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
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
	// private ProgressDialog dialog;
	private String parameters;
	private String URL = "http://mcdimus.appspot.com/login";
	// private AlertDialog.Builder builder;
	private CheckBox rememberPassword;

	/**
	 * Object to manipulate preferences file. Store and fetch desired data.
	 */
	private PreferencesManager preferencesManager = new PreferencesManager(this);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Connection connection = new Connection();

		// if no saved settings found show login activity
		if (preferencesManager.isEmpty()) {
			setContentView(R.layout.login);

			userNameEdit = (EditText) findViewById(R.id.editUserName);
			passwordEdit = (EditText) findViewById(R.id.editPassword);
			signUpButton = (Button) findViewById(R.id.buttonSignUp);
			logInButton = (Button) findViewById(R.id.buttonLogIn);
			rememberPassword = (CheckBox) findViewById(R.id.chkRememberPassword);

			this.signUpButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(Login.this, Registration.class));
				}
			});
			this.logInButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					parameters = "username="
							+ userNameEdit.getText().toString() + "&password="
							+ passwordEdit.getText().toString();
					try {
						// if connected and answer received
						if (connection.connect(parameters, URL)) {

							if (rememberPassword.isChecked()) {
								preferencesManager.putUsernameAndPassword(
										userNameEdit.getText().toString(),
										passwordEdit.getText().toString());
							} else {
								// ListViewActivity.setUsername(userNameEdit.getText()
								// .toString());
								preferencesManager.putUsername(userNameEdit
										.getText().toString());
							}
							startActivity(new Intent(Login.this,
									ListViewActivity.class));
						} else {
							showAlert("Username or password is incorrect",
									"Please, check if they are correctly written.");
						}

					} catch (ClientProtocolException e) {
						showAlert("Shit happened!", "Client Protocol Exception");
					} catch (ClassNotFoundException e) {
						showAlert("Shit happened!", "Class Not Found Exception");
					} catch (IOException e) {
						showAlert("Connection problem",
								"Check your internet properties and try again.");
					}
				}
			});

		} else {
			// get login and password from preferences file
			parameters = preferencesManager.getUsernameAndPasswordAsParams();

			try {
				if (connection.connect(parameters, URL)) {
					startActivity(new Intent(Login.this, ListViewActivity.class));
				}
			} catch (ClientProtocolException e) {
				showAlert("Shit happened!", "Client Protocol Exception");
			} catch (ClassNotFoundException e) {
				showAlert("Shit happened!", "Class Not Found Exception");
			} catch (IOException e) {
				showAlert("Connection problem",
						"Check your internet properties and try again.");
			}
		}

	}

	/**
	 * Build and show alert dialog with specified settings. <br>
	 * Method for inner use only.
	 * 
	 * @param title
	 *            - title of the alert dialog.
	 * @param text
	 *            - main message.
	 */
	private void showAlert(String title, String text) {
		final Drawable drawable = this.getResources().getDrawable(
				R.drawable.alert_dialog_icon);

		AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
		builder.setMessage(text).setIcon(drawable).setCancelable(false)
				.setTitle(title)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}
}
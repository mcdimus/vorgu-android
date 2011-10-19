package ee.ttu.mapsApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends Activity {

	private Button registerSignUpButton;
	private EditText userNameEdit;
	private EditText passwordEdit;
	private EditText firstNameEdit;
	private EditText lastNameEdit;
	private EditText phoneNumberEdit;
	private String parameters;
	private String URL;
	private ProgressDialog dialog;
	private AlertDialog.Builder builder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		this.registerSignUpButton = (Button) this
		.findViewById(R.id.regButtonSignUp);
		final Connection connection = new Connection();
		final Drawable drawable = this.getResources().getDrawable(R.drawable.alert_dialog_icon);
		this.registerSignUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog = ProgressDialog.show(Registration.this, "",
						"Loading. Please wait...", true);
				dialog.setCancelable(true);
				userNameEdit = (EditText) findViewById(R.id.regUserName);
				passwordEdit = (EditText) findViewById(R.id.regPassword);
				firstNameEdit = (EditText) findViewById(R.id.regFirstName);
				lastNameEdit = (EditText) findViewById(R.id.regLastName);
				phoneNumberEdit = (EditText) findViewById(R.id.regPhoneNumber);
				parameters = "username=" + userNameEdit.getText().toString() + "&password=" + passwordEdit.getText().toString()
				+ "&firstname=" + firstNameEdit.getText().toString() + "&lastname=" + lastNameEdit
				.getText().toString() + "&phonenumber=" + phoneNumberEdit.getText().toString();
				URL = "http://mcdimus.appspot.com/register";
				try {
					if (connection.connect(parameters, URL)) {
						dialog.dismiss();
						Toast.makeText(Registration.this, 
					            "Registration was successful!", 
					            Toast.LENGTH_SHORT).show();
						startActivity(new Intent(Registration.this,
								Login.class));
					} else {
						dialog.dismiss();
						
						builder = new AlertDialog.Builder(
								Registration.this);
						builder
								.setMessage(
										"Please, try again...")
								.setIcon(drawable)
								.setCancelable(false).setTitle(
										"An error occurred!").setNeutralButton(
										"Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						builder.show();
						
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					dialog.dismiss();
					  Toast.makeText(Registration.this, 
					            "Error: ClientProtocolException. Try again..." , 
					            Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					dialog.dismiss();
					 Toast.makeText(Registration.this, 
					            "Error: IOException. Try again..." , 
					            Toast.LENGTH_SHORT).show();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					dialog.dismiss();
					 Toast.makeText(Registration.this, 
					            "Error: ClassNotFoundException. Try again..." , 
					            Toast.LENGTH_SHORT).show();
					
				}
			}
		});
	}
}

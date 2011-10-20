package ee.ttu.mapsApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class AndroidMapsAppActivity extends MapActivity {
	private Handler handler;
	private Context context = this;
	private Location location;

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;

	private Connection connection;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.maps); // bind the layout to the activity

		// handler fror thread
		handler = new Handler();

		// ???? xz za4em eto nyzhno bilo :)
		// // create a map view
		// RelativeLayout linearLayout = (RelativeLayout)
		// findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		mapController = mapView.getController();
		mapController.setZoom(5); // Zoom 1 is world view
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Location listener. Listener is called when new coordinates received (via
	 * geo fix). It reads coordinates, animates view to the point on the map and
	 * calls method, which will send these coordinates to the server.
	 * 
	 * Oct 21, 2011
	 * 
	 * @author Dmitri Maksimov
	 * 
	 */
	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point); // mapController.setCenter(point);

			sendMyCoordsToServer(lat, lng);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * Method creates new connection and sends coordinates to the server. Method
	 * also calls asynchronous method, which will be run in separate thread and
	 * do some tricks with data received from server.
	 * 
	 * @param lat
	 *            - latitude.
	 * @param lng
	 *            - longitude.
	 */
	public void sendMyCoordsToServer(int lat, int lng) {
		connection = new Connection();
		String URL = "http://mcdimus.appspot.com/set_coords";
		String parameters = "id=" + LocalData.getUserId() + "&latitude=" + lat
				+ "&longitude=" + lng;

		try {
			// if connected and answer received
			if (connection.connect(parameters, URL)) {
				processListWithPersons();
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

	/**
	 * TODO: Here should be the processing of received list of persons from server.
	 * List is stored in LocalData.persons. Also see some code in the Connection.connect.
	 */
	private void processListWithPersons() {
		// Do something long
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "FetchedList",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		};
		new Thread(runnable).start();
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

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AndroidMapsAppActivity.this);
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
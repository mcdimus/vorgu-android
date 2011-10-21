package ee.ttu.mapsApp;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class AndroidMapsAppActivity extends MapActivity {
	private Handler handler;
	private Context context = this;
	
	private List<Overlay> mapOverlays;

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;

	private Connection connection;
	
	private GeoUpdateHandler locationListener;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.maps); // bind the layout to the activity

		// handler for thread
		handler = new Handler();

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoom 1 is world view
	}
	

	@Override
	protected void onResume() {
		mapOverlays = mapView.getOverlays();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new GeoUpdateHandler();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true); // na telefone rabotaet!!!
		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		/* GPS, as it turns out, consumes battery like crazy */
		locationManager.removeUpdates(locationListener);
		super.onResume();
	}

	@Override
	protected void onStop() {
		if(locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
		locationManager = null;
		super.onStop();
		finish();
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
//			if(!mapOverlays.isEmpty()) {
//				mapOverlays.remove(0);
//			}
			mapOverlays.clear();
			Drawable myDrawable = context.getResources().getDrawable(R.drawable.mappinred);
		    Marker marker = new Marker(myDrawable,context);
		    OverlayItem my_marker = new OverlayItem(point, LocalData.getUsername(), "You are here!!!");
		    marker.addOverlay(my_marker);
		    mapOverlays.add(marker);
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
						String str = "";
						for(int i = 0; i < LocalData.getPersons().size(); i++) {
							str += LocalData.getPersons().get(i).getUsername();
							
						}
						Toast.makeText(context, str,
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
package ee.ttu.mapsApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

	private Connection connection = new Connection();

	private GeoUpdateHandler locationListener;
	
	private PreferencesManager preferencesManager = new PreferencesManager(this);
	
	private String parameters;
	private String URL;
	
	private String selectedMember = LocalData.getUsername();
	
	private Location myLocation;
	private Timer timer;
	private ArrayList<String> distanceBetween = new ArrayList<String>();
	
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
		String provider = locationManager.getBestProvider(criteria, true); 
		locationManager
				.requestLocationUpdates(provider, 0, 0, locationListener);
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
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
		locationManager = null;
		super.onStop();
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		int selected = -1;
		switch (item.getItemId()) {
		case R.id.find_person:
			final String[] usernames = new String[LocalData.getPersons().size()];
			for(int i = 0; i < LocalData.getPersons().size(); i++) {
				if(LocalData.getPersons().get(i).getUsername().equals(LocalData.getUsername())) {
					usernames[i] = LocalData.getPersons().get(i).getUsername() + " (you)";
				} else {
					usernames[i] = LocalData.getPersons().get(i).getUsername();
				}
				if(selectedMember.contains(LocalData.getPersons().get(i).getUsername())) {
					selected = i;
				}
			}
			AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
			dialog2.setTitle("Select the member");
			dialog2.setSingleChoiceItems(usernames, selected, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog2, int user) {
			    	int latitude = (int)LocalData.getPersons().get(user).getLatitude();
			    	int longitude = (int)LocalData.getPersons().get(user).getLongitude();
			    	GeoPoint point = new GeoPoint(latitude, longitude);
			    	selectedMember = usernames[user];
			    	mapController.animateTo(point);
					 dialog2.dismiss();
			    }
			});
			dialog2.show();
			return true;
			
		case R.id.distance:
			final CharSequence[] distances = distanceBetween.toArray(new CharSequence[distanceBetween.size()]);
			AlertDialog.Builder dialog3 = new AlertDialog.Builder(this);
			dialog3.setTitle("Distance");
			dialog3.setItems(distances, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog3, int item) {
			        dialog3.cancel();
			    }
			});
			dialog3.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog3, int id) {
					dialog3.cancel();
				}
			});
			dialog3.show();
			return true;
		case R.id.changegroup:
			List<String> list = LocalData.getGroups();
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).equals(LocalData.getMyGroup())) {
					selected = i;
				}
			}
			final CharSequence[] items = list.toArray(new CharSequence[list.size()]);
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Select the desired group");
			dialog.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	parameters = "id=" + LocalData.getUserId() + "&groupname="
					+ items[item];
			    	URL = "http://mcdimus.appspot.com/join_group";
			    	try {
						if(connection.connect(parameters, URL)) {
							LocalData.setMyGroup(items[item].toString());
							preferencesManager.putGroup(LocalData.getMyGroup());
							startActivity(new Intent(AndroidMapsAppActivity.this,
									AndroidMapsAppActivity.class));
						}
					} catch (ClientProtocolException e) {
						showAlert("ERROR!", "Client Protocol Exception");
					} catch (ClassNotFoundException e) {
						showAlert("ERROR!", "Class Not Found Exception");
					} catch (IOException e) {
						showAlert("Connection problem",
								"Check your internet properties and try again.");
					}
					 dialog.dismiss();
			    }
			});
			dialog.show();
			return true;
		case R.id.logout:
			preferencesManager.clearPreferences();
			LocalData.setMyGroup(null);
			LocalData.setUsername(null);
			LocalData.setUserId(0);
			startActivity(new Intent(this, Login.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * To show distance to the specified user.
	 * @param lat - latitude of the user.
	 * @param lng - longitude of the user.
	 * @param user - the specified user.
	 */
	private void distance(double lat, double lng, String user) {
		Location location = new Location("Point B");
		location.setLatitude(lat);
		location.setLongitude(lng);
		double distance;
		distance = myLocation.distanceTo(location) / 1000;
		String result = "To " + user + ": " + String.valueOf((int)distance) + " km";
		distanceBetween.add(result);
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
			myLocation = location;
			final int lat = (int) (location.getLatitude() * 1E6);
			final int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapOverlays.clear();
			Drawable myDrawable = context.getResources().getDrawable(
					R.drawable.mappinred);
			newOverlay(lat, lng, myDrawable, LocalData.getUsername());
			mapController.animateTo(point);
			
			if (timer != null) {timer.cancel();}
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				
				@Override
				public void run() {
					sendMyCoordsToServer(lat, lng);
				}
			}, 1000, 20000);
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
		URL = "http://mcdimus.appspot.com/set_coords";
		parameters = "id=" + LocalData.getUserId() + "&latitude=" + lat
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
			showAlert("ERROR!", "Client Protocol Exception");
		} catch (ClassNotFoundException e) {
			showAlert("ERROR!", "Class Not Found Exception");
		} catch (IOException e) {
			showAlert("Connection problem",
					"Check your internet properties and try again.");
		}
	}

	/**
	 * To get the real address with the help of
	 * longitude and latitude.
	 * @param currentLatitude - the latitude.
	 * @param currentLongitude - the longitude.
	 * @return - string with address.
	 */
	private String getAddress(int currentLatitude, int currentLongitude, String username) {
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation((double)(currentLatitude / 1E6),
					(double)(currentLongitude / 1E6), 1);
			String result = "";
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
					result += addresses.get(0).getAddressLine(i) + "\n";
				}
				result += addresses.get(0).getCountryName();
			}
			if(username.equals(LocalData.getUsername()) && !result.equals("")) {
				result = "Your address is:\n" + result;
			} else if(username.equals(LocalData.getUsername()) && result.equals("")) {
				result = "Your address is unknown";
			} else if(result.equals("")) {
				result = "Address is unknown";
			} else {
				result = "Address:\n" + result;
			}
			return result;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * TODO: Here should be the processing of received list of persons from
	 * server. List is stored in LocalData.persons. Also see some code in the
	 * Connection.connect.
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
						Drawable drawable = context.getResources().getDrawable(
								R.drawable.mappingreen);
						distanceBetween.clear();
						for (int i = 0; i < LocalData.getPersons().size(); i++) {
							if (!LocalData.getPersons().get(i).getUsername()
									.equals(LocalData.getUsername())) {
								distance((LocalData.getPersons().get(i)
										.getLatitude()/1E6), (LocalData.getPersons().get(i)
												.getLongitude()/1E6), LocalData.getPersons().get(i).getUsername());
								newOverlay((int)LocalData.getPersons().get(i)
										.getLatitude(), (int)LocalData.getPersons()
										.get(i).getLongitude(), drawable,
										LocalData.getPersons().get(i)
												.getUsername());
							}

						}
					}
				});
			}
		};
		new Thread(runnable).start();
	}

	private void newOverlay(int latitude, int longitude,
			Drawable drawable, String username) {
		GeoPoint point = new GeoPoint(latitude, longitude);
		Marker marker = new Marker(drawable, context);
		OverlayItem my_marker = new OverlayItem(point, "Member: " + username,
				getAddress(latitude, longitude, username));
		marker.addOverlay(my_marker);
		mapOverlays.add(marker);
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
				.setTitle(title).setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
	}
}
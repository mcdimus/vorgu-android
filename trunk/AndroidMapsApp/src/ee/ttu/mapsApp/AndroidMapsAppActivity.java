package ee.ttu.mapsApp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.maps.MapActivity;

public class AndroidMapsAppActivity extends MapActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		Drawable drawable = this.getResources().getDrawable(R.drawable.pushpin);
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); 
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


}
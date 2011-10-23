package ee.ttu.mapsApp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("unchecked")
public class Marker extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	Context mContext;

	public Marker(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

}

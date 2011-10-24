package ee.ttu.mapsApp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("unchecked")
public class Marker extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	private Context mContext;
	private int markerHeight;

	public Marker(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		markerHeight = ((BitmapDrawable) defaultMarker).getBitmap().getHeight();
		populate();
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
	
    
    public void draw(android.graphics.Canvas canvas, MapView mapView,
            boolean shadow) {
        super.draw(canvas, mapView, shadow);

        // go through all OverlayItems and draw title for each of them
        for (OverlayItem item:mOverlays)
        {
            /* Converts latitude & longitude of this overlay item to coordinates on screen.
             * As we have called boundCenterBottom() in constructor, so these coordinates
             * will be of the bottom center position of the displayed marker.
             */
            GeoPoint point = item.getPoint();
            Point markerBottomCenterCoords = new Point();
            mapView.getProjection().toPixels(point, markerBottomCenterCoords);

            /* Find the width and height of the title*/
            TextPaint paintText = new TextPaint();
            Paint paintRect = new Paint();

            Rect rect = new Rect();
            paintText.setTextSize(12.0f);
            paintText.getTextBounds(item.getTitle(), 0, item.getTitle().length(), rect);
            rect.inset(-5, -5);
            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y - markerHeight - rect.height());
            paintText.setTextAlign(Paint.Align.CENTER);
//            paintText.setTextSize(12.0f);
            paintText.setARGB(255, 255, 255, 255);
            paintRect.setARGB(130, 0, 0, 0);
            
            canvas.drawRoundRect( new RectF(rect), 2, 2, paintRect);
            canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,
                    rect.bottom - 5, paintText);
            
        }
    }

    public void addOverlay(int latitude, int longitude, String title,
            String snippet)
    {
        OverlayItem item;

        GeoPoint geopoint = new GeoPoint(latitude, longitude);
        item = new OverlayItem(geopoint, title, snippet);
        mOverlays.add(item);
        populate();

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

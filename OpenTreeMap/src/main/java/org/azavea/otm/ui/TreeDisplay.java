package org.azavea.otm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.azavea.helpers.Logger;
import org.azavea.otm.R;
import org.azavea.otm.data.Geometry;
import org.azavea.otm.data.Plot;
import org.json.JSONException;
import org.json.JSONObject;

public class TreeDisplay extends UpEnabledActionBarActivity {
    protected LatLng plotLocation;
    protected Plot plot;
    public static int RESULT_PLOT_DELETED = Activity.RESULT_FIRST_USER + 1;
    public static int RESULT_PLOT_EDITED = Activity.RESULT_FIRST_USER + 2;
    protected static final int DEFAULT_TREE_ZOOM_LEVEL = 18;
    protected GoogleMap mMap;
    protected Marker plotMarker;
    protected int mapFragmentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            plot = new Plot(new JSONObject(getIntent().getStringExtra("plot")));
            plotLocation = getPlotLocation(plot);
        } catch (JSONException e) {
            Toast.makeText(this, "Could not retrieve Tree information", Toast.LENGTH_SHORT).show();
            Logger.error("Failed to create tree view", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MapHelper.checkGooglePlay(this);
    }

    protected LatLng getPlotLocation(Plot plot) {
        try {
            Geometry geom = plot.getGeometry();
            double lon = geom.getX();
            double lat = geom.getY();
            return new LatLng(lat, lon);
        } catch (Exception e) {
            Logger.error("Unable to get plot geometry", e);
            return null;
        }
    }

    protected void showPositionOnMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(plotLocation, DEFAULT_TREE_ZOOM_LEVEL));
        if (plotMarker != null) {
            plotMarker.remove();
        }
        plotMarker = mMap.addMarker(new MarkerOptions().position(plotLocation).title("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mapmarker)));
    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            // we have to try for 2 different fragment id's, using the reasonable
            // assumption that the base classes are going to be instantiated one
            // at a time.
            Log.d("VIN", String.format("%d", mapFragmentId));
            FragmentManager fragmentManager = getSupportFragmentManager();
            SupportMapFragment fragment = (SupportMapFragment) fragmentManager.findFragmentById(mapFragmentId);
            mMap = fragment.getMap();
            if (mMap != null) {
                setUpMap();
            } else {
                Log.e("VIN", "map was null.");
            }
        }
    }

    private void setUpMap() {
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
    }

    protected void setText(int resourceId, String text) {
        // Only set the text if it exists, letting the layout define default text
        if (text != null && !"".equals(text)) {
            ((TextView) findViewById(resourceId)).setText(text);
        }
    }

}

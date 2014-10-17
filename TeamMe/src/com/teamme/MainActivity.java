package com.teamme;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {
    // Google Map
    public GoogleMap googleMap;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
        Log.e("loading map. . . .", "loading map. . . ");
        // Loading map
        initilizeMap();

    } catch (Exception e) {
        Log.e("catch. . .", "catch. . .");
        e.printStackTrace();
    }
    /*
     * MapFunctionality mf = new MapFunctionality(); mf.currentLoc();
     * mf.addMarker();
     */
}

/**
 * function to load map. If map is not created it will create it for you
 * */
private void initilizeMap() {
    // MapFunctionality mf = new MapFunctionality();
    Log.e("initializing map. . . ", "initializing map. . ");
    if (googleMap == null) {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();

        // check if map is created successfully or not
        if (googleMap != null) {
        	googleMap.setOnMapClickListener(new OnMapClickListener() {
        		System.out.println("test");
        		System.out.println("test2");
        	    @Override
        	    public void onMapClick(LatLng point) {
        	          googleMap.addMarker(new MarkerOptions().position(point));
        	          // you can get latitude and longitude also from 'point'
        	          // and using Geocoder, the address
        	         }
        	});
        }
        // mf.currentLoc();
        // mf.addMarker();
    }
}

//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.activity_main, menu);
//    return true;
//}

@Override
protected void onResume() {
    super.onResume();
    initilizeMap();
}
}
package com.teamme;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    // Google Map
    public GoogleMap googleMap;
    
    //Code to use popup- perhaps useful for first time users
    public void clicked(View view) {
  	HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap the map to select a location!");
  	helpPopup.show(view);
      }
   	
    
    
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
        if (googleMap != null){
        	
        }
        // check if map is created successfully or not
        if (googleMap != null) {
        	googleMap.setOnMapClickListener(new OnMapClickListener(){

				@Override
				public void onMapClick(LatLng point) {
					// TODO Auto-generated method stub
				    googleMap.addMarker(new MarkerOptions().position(point));        	     
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
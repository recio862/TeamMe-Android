package com.teamme;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	// Google Map
	public GoogleMap googleMap;
	public Button myButton;



	//Quickfix to move maps zoom buttons to the top left so it doesnt interfere with the
	//layout of the buttons
	public void fixZoom(){


		// Find map fragment

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

		// Find ZoomControl view
		View zoomControls = mapFragment.getView().findViewById(0x1);

		if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

			// Update margins, set to 10dp
			final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
					getResources().getDisplayMetrics());
			params.setMargins(margin, margin, margin, margin);
		}
	}
	
	
	public void clickedCreate(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap a location first!");
		helpPopup.show(view);

	}

	public void clickedView(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap an existing activity first!");
		helpPopup.show(view);

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fixZoom();
		myButton = (Button)findViewById(R.id.Button01);
		try {
			Log.e("loading map. . . .", "loading map. . . ");
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			Log.e("catch. . .", "catch. . .");
			e.printStackTrace();
		}

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

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}
}
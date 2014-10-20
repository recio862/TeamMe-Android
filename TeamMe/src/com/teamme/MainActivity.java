package com.teamme;

import android.app.Activity;
import android.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	// Google Map
	public GoogleMap googleMap;
	public Button createButton;
	public Button viewButton;
	public Marker myMarker;
	public LatLng myLocation;

	private boolean createEnabled = false;
	private boolean viewEnabled = false;

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
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			// Update margins, set to 10dp
			final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
					getResources().getDisplayMetrics());
			params.setMargins(margin, margin, margin, margin);
		}
	}


	public void clickedCreate(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap a location first!");
		if (!createEnabled)
			helpPopup.show(view);
		else 
		{
			DialogFragment dialog = new CreateGameDialog();
			dialog.show(getFragmentManager(), "CreateGameDialog");
			
			
			
		}
	}

	public void clickedView(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap an existing activity first!");
		if (!viewEnabled)
			helpPopup.show(view);

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		fixZoom();
		createButton = (Button)findViewById(R.id.Button01);

		viewButton = (Button)findViewById(R.id.Button02);
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
			
			// check if map is created successfully or not
			if (googleMap != null) {
//				
//				Location location = googleMap.getMyLocation();
//
//				    if (location != null) {
//				    	myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//				        myLocation = new LatLng(location.getLatitude(),
//				                location.getLongitude());
//				    }
//				    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
//				           13));
				googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){

					@Override
					public boolean onMarkerClick(Marker arg0) {
						
						return true;
					}
					
				});
				googleMap.setOnMapClickListener(new OnMapClickListener(){

					@Override
					public void onMapClick(LatLng point) {
						// TODO Auto-generated method stub
						if (myMarker != null)
							myMarker.remove();
						// create marker
						MarkerOptions markerOptions = new MarkerOptions().position(point).title("Create Game Here!");
					
						// Changing marker icon
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
					
						createButton.setAlpha((float) 1.0);
						createEnabled = true;
						
						myMarker = googleMap.addMarker(markerOptions); 

						
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
package com.teamme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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
	public AlertDialog dialog;
	private boolean createEnabled = false;
	private boolean viewEnabled = false;

	//Quickfix to move maps zoom buttons to the top left so it doesnt interfere with the
	//layout of the buttons
	//http://stackoverflow.com/questions/14071230/android-maps-library-v2-zoom-controls-custom-position
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

	protected Dialog onCreateDialog(int id) {

		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = getLayoutInflater();
		// 2. Chain together various setter methods to set the dialog characteristics

		builder.setView(inflater.inflate(R.layout.create_dialog, null)).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
			}
		})
		.setNegativeButton("Create Game", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast gameCreated = Toast.makeText(getApplicationContext(), "Your Game Was Created!", Toast.LENGTH_SHORT);
				gameCreated.setGravity(Gravity.CENTER, 0, 0);
				gameCreated.show();
				
			}
		});

		// 3. Get the AlertDialog from create()
		dialog = builder.create();


		return dialog;
	}





	public void clickedCreate(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap a location first!");
		if (!createEnabled)
			helpPopup.show(view);
		else 
		{


			showDialog(0);
			LayoutParams params = dialog.getWindow().getAttributes();
			params.width = LayoutParams.FILL_PARENT;
			dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

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


				googleMap.setMyLocationEnabled(true);
				//Location location = googleMap.getMyLocation();
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				String provider = locationManager.getBestProvider(criteria, false);
				Location location = locationManager.getLastKnownLocation(provider);
				if (location != null) {
					myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					myLocation = new LatLng(location.getLatitude(),
							location.getLongitude());
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
							14));
				}
				else 
				{
					LatLng coordinate = new LatLng(30.2861, -97.7394);
					CameraUpdate utAustin = CameraUpdateFactory.newLatLngZoom(coordinate, 14);
					googleMap.animateCamera(utAustin);
				}


				googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){

					@Override
					public boolean onMarkerClick(Marker arg0) {

						return true;
					}

				});
				googleMap.getUiSettings().setCompassEnabled(false);

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

						createButton.setAlpha((float) 0.70);

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
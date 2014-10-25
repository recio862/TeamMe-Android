package com.teamme;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
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
import com.teamme.Networking.AsyncResponse;
import com.teamme.Networking.CoordParameters;

public class MainActivity extends Activity implements AsyncResponse {
	// Google Map
	public GoogleMap googleMap;
	public Button createButton;
	public Button viewButton;
	public Marker myMarker;
	public LatLng myLocation;
	private MarkerOptions markerOptions;
	public AlertDialog dialog;
	public Networking messagePasser;
	public Networking.DownloadMarkersTask finalPasser;
	public static String jsonDownloadedMarkersString;
	
	private boolean createEnabled = false;
	private boolean viewEnabled = false;
	
	//passes data from the async networking thread to ui thread after results are returned
    public void gotMarkers(String jsonDownloadedMarkersString){
			try{
				final JSONArray geodata = new JSONArray(jsonDownloadedMarkersString);
				Toast.makeText(getApplicationContext(), "22222" , Toast.LENGTH_LONG).show();
//				Log.d("LAT", "meow" + geodata.toString());
	
				//Toast.makeText(getApplicationContext(), geodata.toString() , Toast.LENGTH_LONG).show();

				final int n = geodata.length();
				for (int i = 0; i < n; i++) {
					Log.d("LAT", "meow" + geodata.getJSONObject(i).getDouble("lat"));
					Log.d("LONG", "meow" + geodata.getJSONObject(i).getDouble("lng"));
					markerOptions = new MarkerOptions().position(new LatLng(geodata.getJSONObject(i).getDouble("lat"), geodata.getJSONObject(i).getDouble("lng")));
					googleMap.addMarker(markerOptions); 
				}
			}catch(JSONException e) {
				throw new RuntimeException(e);
			}
	}
	//Quickfix to move maps zoom buttons to the top left so it doesn't interfere with the
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

		
		if (id == 0){
			builder.setView(inflater.inflate(R.layout.create_dialog, null)).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
			}
		})
		.setNegativeButton("Create Game", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast gameCreated = Toast.makeText(getApplicationContext(), "Your Game Was Created!", Toast.LENGTH_SHORT);
				gameCreated.setGravity(Gravity.CENTER, 0, 0);
				gameCreated.show();
				createButton.setAlpha((float)0.15);
				markerOptions.alpha((float)(1.0));
				myMarker = googleMap.addMarker(markerOptions); 
				
				markerOptions = null;
				myMarker = null;
				createEnabled=  false;
				
			}
		});

		}
		else if (id == 1)
			builder.setView(inflater.inflate(R.layout.settings_dialog, null));
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
		messagePasser = new Networking(MainActivity.this);
		viewButton = (Button)findViewById(R.id.Button02);
		try {
			Log.e("loading map. . . .", "loading map. . . ");
			// Loading map
			initializeMap();

		} catch (Exception e) {
			Log.e("catch. . .", "catch. . .");
			e.printStackTrace();
		}

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initializeMap() {
		// MapFunctionality mf = new MapFunctionality();
		Log.e("initializing map. . . ", "initializing map. . ");
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap != null) {

				finalPasser = messagePasser.new DownloadMarkersTask();
				finalPasser.responder = this;
				finalPasser.execute("http://72.182.49.84:80/android/project/grabMarkers.php?id=1"); 
				googleMap.setMyLocationEnabled(true);
				//Location location = googleMap.getMyLocation();
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				String provider = locationManager.getBestProvider(criteria, false);
				Location location = locationManager.getLastKnownLocation(provider);
				if (location != null) {
					myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
						if (myMarker != null){
							myMarker.remove();
						}
						// create marker
						markerOptions = new MarkerOptions().position(point).title("Create Game Here!");
						
						// Changing marker icon
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
						markerOptions.alpha((float)0.15);

						
						createButton.setAlpha((float) 0.70);

						createEnabled = true;
						CoordParameters params = new CoordParameters("http://72.182.49.84:80/android/project/updateMarkers.php", point, "0");
						messagePasser.new SendCoordsTask().execute(params); 
						myMarker = googleMap.addMarker(markerOptions); 


					}

				});

			}

		}
	}

	
	public void settingsDialog(MenuItem item){
		showDialog(1);
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
		initializeMap();
	}
}

package com.teamme;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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

	private HashMap<String, MarkerInfo> mapMarkers;
	private boolean mSoundOn;
	private SoundPool mSounds;
	private HashMap<Integer, Integer> mSoundIDMap;
	private SharedPreferences mPrefs;
	public Marker myMarker;
	public int mGameNumber;
	public int index;
	public LatLng myLocation;
	public Marker selectedMarker;
	private Switch switchButton;
	private String markerID;
	public String usedIp;
	private MarkerOptions markerOptions;
	public AlertDialog dialog;
	public Networking messagePasser;
	public Networking.GetRequest getPasser;
	public CoordParameters params;
	//this is the point the user clicks on on the map that will be passed to server when creating game
	public LatLng paramPoint;

	//to use the Deleter, do something like

	//params = new GameDeletionParameters("http://72.182.49.84:80/android/project/deleteMarkers.php", Integer gameNum);
	//messagePasser.new DeleteMarkersTask().execute(params);

	private boolean createEnabled = false;
	private boolean viewEnabled = false;

	//passes data from the async networking thread to ui thread after results are returned
	public void getResponse(String jsonDownloadedMarkersString){
		if (jsonDownloadedMarkersString.equals("0") || jsonDownloadedMarkersString.equals(null) 
				|| jsonDownloadedMarkersString.equals("") || jsonDownloadedMarkersString.equals("[]")){
			Log.e("PROFILE RESPONSE STRING CAUGHT", jsonDownloadedMarkersString);
			return;
		}
		else{
			try{
				final JSONArray geodata = new JSONArray(jsonDownloadedMarkersString);
				Toast.makeText(getApplicationContext(), "Loading Games" , Toast.LENGTH_LONG).show();
				final int n = geodata.length();
				index = n;
				for (int i = 0; i < n; i++) {
					Log.d("LAT", "meow" + geodata.getJSONObject(i).getDouble("lat"));
					Log.d("LONG", "meow" + geodata.getJSONObject(i).getDouble("lng"));
					mGameNumber = (geodata.getJSONObject(i).getInt("markerId"));
					markerOptions = new MarkerOptions().position(new LatLng(geodata.getJSONObject(i).getDouble("lat"), geodata.getJSONObject(i).getDouble("lng")));
					markerOptions.icon(getIconFromActivityNum(Integer.parseInt(geodata.getJSONObject(i).getString("activityNum")), false));
					markerOptions.title(""+i);
					googleMap.addMarker(markerOptions);
					mapMarkers.put(""+i, new MarkerInfo(geodata.getJSONObject(i)));
				}
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
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

	public void profile(MenuItem item){
		Intent intent = new Intent(getApplicationContext(), Profile.class);
		startActivity(intent);



	}

	public void teams(MenuItem item){
		Intent intent = new Intent(getApplicationContext(), Teams.class);
		startActivity(intent);


	}

	public void logout(MenuItem item){
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		SharedPreferences.Editor ed = mPrefs.edit();


		ed.putBoolean("loggedOut", true);

		ed.apply();
		Intent intent = new Intent(getApplicationContext(), Login.class);
		startActivity(intent);
		finish();
	}
	private BitmapDescriptor getIconFromActivityNum(int activityNum, boolean selected){

		BitmapDescriptor icon = (BitmapDescriptorFactory.fromResource(R.drawable.marker));
		switch (activityNum){
		case 0:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.customgame));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.customgameselected));
			break;
		case 1:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.soccer));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.soccerselected));
			break;
		case 2:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.football));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.footballselected));
			break;
		case 3:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.frisbee));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.footballselected));
			break;
		case 4:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.tennis));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.tennisselected));
			break;
		case 5:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bike));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bikeselected));
			break;
		case 6:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bowling));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bowlingselected));
			break;
		case 7:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.climbing));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.climbingselected));
			break;
		case 8:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.volleyball));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.volleyballselected));
			break;
		}

		return icon;



	}
	protected Dialog onCreateDialog(int id) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		final View dialogContent = inflater.inflate(R.layout.create_dialog, null);
		final View viewDialogContent = inflater.inflate(R.layout.view_dialog, null);
		// 2. Chain together various setter methods to set the dialog characteristics
		if (id == 0){
			builder.setView(dialogContent).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					playSound(R.raw.cancel);
					resetFields(dialogContent);
				}
			})
			.setNegativeButton("Create Game", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					//setContentView( R.layout.create_dialog );
					EditText edTxtTeamName = (EditText) dialogContent.findViewById(R.id.teamName);
					EditText edTxtCustomActivity = (EditText) dialogContent.findViewById(R.id.username5);
					Spinner spinnerActivity = (Spinner) dialogContent.findViewById(R.id.spinner1);
					RadioButton rb = (RadioButton) dialogContent.findViewById(R.id.radio_pirates);
					EditText edTxtPlayersActive = (EditText) dialogContent.findViewById(R.id.num_players);
					EditText edTxtPlayersNeeded = (EditText) dialogContent.findViewById(R.id.num_players2);
					TimePicker pickFinishTime = (TimePicker) dialogContent.findViewById(R.id.timePicker1);

					//				if (spinnerActivity.getSelectedItem() == null)
					//					activity = "";
					//				else
					String activity = spinnerActivity.getSelectedItem().toString();
					Integer activityNum = 0;
					//see spinner_items in res/values/arrays.xml
					playSound(R.raw.whistle);
					if (rb.isChecked()){
						switch (activity){

						case "Soccer":
							activityNum = 1;
							break;
						case "Football":
							activityNum = 2;
							break;
						case "Disc Golf":
							activityNum = 3;
							break;
						case "Tennis":
							activityNum = 4;
							break;
						case "Biking":
							activityNum = 5;
							break;
						case "Bowling":
							activityNum = 6;
							break;
						case "Rock Climbing":
							activityNum = 7;
							break;
						case "Volleyball":
							activityNum = 8;
							break;
						}
					}
					String teamName = edTxtTeamName.getText().toString();
					String customActivity = edTxtCustomActivity.getText().toString();
					String activePlayers = edTxtPlayersActive.getText().toString();
					String neededPlayers = edTxtPlayersNeeded.getText().toString();
					String finishTimeHour = String.valueOf(pickFinishTime.getCurrentHour());
					String finishTimeMinute = String.valueOf(pickFinishTime.getCurrentMinute());


					params = new CoordParameters("http://" + messagePasser.usedIp + ":80/android/project/updateMarkers.php", paramPoint, "0", 
							finishTimeHour, finishTimeMinute, activePlayers, neededPlayers, customActivity, teamName, activityNum);
					messagePasser.new SendCoordsTask().execute(params);
					Toast gameCreated = Toast.makeText(getApplicationContext(), "Your Game Was Created!", Toast.LENGTH_SHORT);
					gameCreated.setGravity(Gravity.CENTER, 0, 0);
					gameCreated.show();
					createButton.setAlpha((float)0.15);
					myMarker.setIcon(getIconFromActivityNum(activityNum, false));
					MarkerInfo mi = new MarkerInfo(null);
					mi.setActivityNum(activityNum);
					mi.setActivePlayers(activePlayers);
					mi.setNeededPlayers(neededPlayers);
					mi.setFinishHour(finishTimeHour);
					mi.setFinishMinute(finishTimeMinute);
					mi.setCustomActivity(customActivity);
					mi.setTeamName(teamName);
					mapMarkers.put(""+index,mi );
					myMarker.setTitle(""+index);
					index++;
					markerOptions = null;
					myMarker = null;
					createEnabled=  false;
					resetFields(dialogContent);

				}
			});

		}
		else if (id == 1){
			builder.setView(inflater.inflate(R.layout.settings_dialog, null));  

			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});



		}
		else if (id==2){
			builder.setView(inflater.inflate(R.layout.about_dialog, null));
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});
		}

		else if (id==3){
			builder.setView(viewDialogContent);
			String title = selectedMarker.getTitle();
			Log.d("title is: ", title);
			MarkerInfo markerInfo = mapMarkers.get(title);

			RadioButton rb1 = (RadioButton)viewDialogContent.findViewById(R.id.radio_pirates55);
			RadioButton rb2 = (RadioButton)viewDialogContent.findViewById(R.id.radio_ninjas55);

			//TO-DO: error checking
			//			if (mapMarkers.containsKey(title)){
			//				markerInfo = mapMarkers.get(title);
			//			}
			EditText edTxtTeamName = (EditText) viewDialogContent.findViewById(R.id.teamName55);
			if (markerInfo != null)edTxtTeamName.setText(markerInfo.getTeamName());
			EditText edTxtCustomActivity = (EditText) viewDialogContent.findViewById(R.id.username555);

			Spinner spinnerActivity = (Spinner) viewDialogContent.findViewById(R.id.spinner155);
			if (markerInfo != null)
				if (markerInfo.getActivityNum()-1 != -1){
					spinnerActivity.setSelection(markerInfo.getActivityNum()-1);
					rb1.setChecked(true);
					rb2.setChecked(false);
					rb1.setVisibility(View.GONE);
					rb2.setVisibility(View.GONE);
					edTxtCustomActivity.setVisibility(View.GONE);
				}
				else {
					edTxtCustomActivity.setText(markerInfo.getCustomActivity());
					rb2.setChecked(true);
					rb1.setChecked(false);
					rb1.setVisibility(View.GONE);
					rb2.setVisibility(View.GONE);
					spinnerActivity.setVisibility(View.GONE);
				}
			
			spinnerActivity.setEnabled(false);
			
			EditText edTxtPlayersActive = (EditText) viewDialogContent.findViewById(R.id.num_players55);
			if (markerInfo != null)edTxtPlayersActive.setText(markerInfo.getActivePlayers());
			EditText edTxtPlayersNeeded = (EditText) viewDialogContent.findViewById(R.id.num_players255);
			if (markerInfo != null)edTxtPlayersNeeded.setText(markerInfo.getNeededPlayers());
			TimePicker pickFinishTime = (TimePicker) viewDialogContent.findViewById(R.id.timePicker155);
			pickFinishTime.setEnabled(false);

			
			
			
			
			
			
			
			builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					if (selectedMarker != null){
						viewEnabled = false;
					viewButton.setAlpha((float)0.15);
					
					selectedMarker.setIcon(getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
					selectedMarker = null;
					playSound(R.raw.cancel);
					
					resetFields(dialogContent);
					}
				}
			})
			.setNegativeButton("Join Game", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {



				}
			});

		}
		else if (id == 4){
			builder.setView(inflater.inflate(R.layout.map_filter, null));
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});
		}
		// 3. Get the AlertDialog from create()
		dialog = builder.create();
		dialog.show();
		

		return super.onCreateDialog(id);
	}

	protected void resetFields(View dialogContent) {
		EditText edTxtTeamName = (EditText) dialogContent.findViewById(R.id.teamName);
		edTxtTeamName.setText(null);
		EditText edTxtCustomActivity = (EditText) dialogContent.findViewById(R.id.username5);
		edTxtCustomActivity.setText(null);
		Spinner spinnerActivity = (Spinner) dialogContent.findViewById(R.id.spinner1);
		spinnerActivity.setSelection(0);
		EditText edTxtPlayersActive = (EditText) dialogContent.findViewById(R.id.num_players);
		edTxtPlayersActive.setText(null);
		EditText edTxtPlayersNeeded = (EditText) dialogContent.findViewById(R.id.num_players2);
		edTxtPlayersNeeded.setText(null);
		TimePicker pickFinishTime = (TimePicker) dialogContent.findViewById(R.id.timePicker1);
		Calendar c = Calendar.getInstance();
		pickFinishTime.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		pickFinishTime.setCurrentMinute(c.get(Calendar.MINUTE));



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
		else{
			showDialog(3);

		}
	}

	public void featureUnavailable(MenuItem item){
		String alphaRelease = "This feature is not yet available. Stay tuned for the beta release.";
		showFeatureUnavailableToast(alphaRelease);
	}

	public void showFeatureUnavailableToast(String alphaRelease){

		Toast gameCreated = Toast.makeText(getApplicationContext(), alphaRelease, Toast.LENGTH_SHORT);
		gameCreated.setGravity(Gravity.CENTER, 0, 0);
		gameCreated.show();
	}


	@Override
	protected void onPause() {
		super.onPause();
		dialog.dismiss();
		googleMap.clear();
		selectedMarker = null;
		mapMarkers = new HashMap<String, MarkerInfo>();
		viewEnabled = false;
		createEnabled = false;
		viewButton.setAlpha((float) 0.15);
		createButton.setAlpha((float)0.15);

		if(mSounds != null) {
			mSounds.release();
			mSounds = null;
		}		
	}




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
		if (mPrefs.getBoolean("loggedOut", true)){
			Intent intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			finish();
			return;
		}
		setContentView(R.layout.activity_main);
		createSoundPool();
		fixZoom();
		mSoundOn = mPrefs.getBoolean("sound", true);
		createButton = (Button)findViewById(R.id.Button01);
		messagePasser = new Networking(MainActivity.this);
		usedIp = messagePasser.amazonServerIp;
		if (mapMarkers == null)
			mapMarkers = new HashMap<String, MarkerInfo>();
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

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		// check if map is created successfully or not
		if (googleMap != null) {

			getPasser = messagePasser.new GetRequest();
			getPasser.responder = this;


			getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabMarkers.php?id=1"); 

			googleMap.setMyLocationEnabled(true);

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
				public boolean onMarkerClick(Marker myMarker) {
					//String showUnavailable = "View Game Feature is Unavailable. In the future release, you can click this icon to see game info!";
					//showFeatureUnavailableToast(showUnavailable);
					if (myMarker.getTitle().equals("-1")){
						Log.d("tag","test");
						return true;

					}
					viewEnabled = true;
					viewButton.setAlpha((float) 0.70);

					if (selectedMarker != null)
						selectedMarker.setIcon(getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));

					selectedMarker = myMarker;
					selectedMarker.setIcon(getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), true));

					//selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
					return true;
				}

			});
			googleMap.getUiSettings().setCompassEnabled(false);

			googleMap.setOnMapClickListener(new OnMapClickListener(){

				@Override
				public void onMapClick(LatLng point) {
					if (myMarker != null){
						myMarker.remove();
					}
					// create marker
					markerOptions = new MarkerOptions().position(point).title("Create Game Here!");

					// Changing marker icon
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
					markerOptions.title("-1");
					createButton.setAlpha((float) 0.70);
					paramPoint = point;
					createEnabled = true; 
					myMarker = googleMap.addMarker(markerOptions); 
					playSound(R.raw.placemarker);
				}

			});

		}

	}


	public void settingsDialog(MenuItem item){
		showDialog(1);
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		Switch s = (Switch)dialog.findViewById(R.id.sound_switch2);
		if (s!=null)
			s.setChecked(mSoundOn);
		
		/*Switch s2 = (Switch)dialog.findViewById(R.id.anim_switch);
		if (s2!=null)
			s2.setChecked(mPrefs.getBoolean("animationOn", true));
			*/

	}

	public void animClick(View view){
		boolean on = ((Switch) view).isChecked();
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		SharedPreferences.Editor ed = mPrefs.edit();

		if (on)
		ed.putBoolean("animationOn", true);
		else
			ed.putBoolean("animationOn", false);

		ed.apply();

	}
	public void soundClick(View view){

		boolean on = ((Switch) view).isChecked();


		if (on)
			mSoundOn = true;
		else
			mSoundOn = false;


		// Save the current scores
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		SharedPreferences.Editor ed = mPrefs.edit();


		ed.putBoolean("sound", mSoundOn);

		ed.apply();



	}

	public void filterDialog(MenuItem item){
		showDialog(4);
	}
	public void aboutDialog(MenuItem item){
		showDialog(2);
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
		fixZoom();
		createSoundPool();
		mSoundOn = mPrefs.getBoolean("sound", true);


	}

	private void playSound(int id){
		if (mSoundOn){
			mSounds.play(mSoundIDMap.get(id), 1, 1, 1, 0, 1);
		}

	}

	private void createSoundPool() {
		int[] soundIds = {R.raw.whistle, R.raw.cancel, R.raw.placemarker};
		mSoundIDMap = new HashMap<Integer, Integer>();
		mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		for(int id : soundIds) 
			mSoundIDMap.put(id, mSounds.load(this, id, 1));
	}
}

package com.teamme;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
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
import com.parse.Parse;
import com.teamme.Networking.AsyncResponse;
import com.teamme.Networking.CoordParameters;
import com.teamme.Networking.GameJoinParameters;
import com.teamme.Networking.GameUpdateParameters;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends Activity implements AsyncResponse {


	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Global variables
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	public GoogleMap googleMap;
	private boolean restrictMarkerLoading;
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
	public boolean initialLoad = false;
	private Toast loadingToast;
	public LatLng myLocation;
	public Marker selectedMarker;
	public String usedIp;
	private MarkerOptions markerOptions;
	public AlertDialog dialog;
	public Networking messagePasser;
	public Networking.GetRequest getPasser;
	public CoordParameters params;
	public GameJoinParameters gameJoinParams;
	public GameUpdateParameters gameUpdateParams;
	public LatLng paramPoint;
	private boolean createEnabled = false;
	private boolean viewEnabled = false;
	public int newActivePlayers;
	public int newNeededPlayers;
	ParseUser user;
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////





	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Dialogs (these should be separate classes)
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	protected Dialog onCreateDialog(int id) {
		if (loadingToast != null)
			loadingToast.cancel();
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		final View dialogContent = inflater.inflate(R.layout.create_dialog, null);
		final View viewDialogContent = inflater.inflate(R.layout.view_dialog, null);
		final View editDialogContent = inflater.inflate(R.layout.edit_dialog, null);
		final View filterDialogContent = inflater.inflate(R.layout.map_filter, null);

		// 2. Chain together various setter methods to set the dialog characteristics

		//CREATE GAME DIALOG
		if (id == 0){
			TimePicker mTimePicker = (TimePicker) dialogContent.findViewById(R.id.timePicker1);

			mTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
			builder.setView(dialogContent).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//					createEnabled = false;
					//					createButton.setAlpha((float)0.15);
					//					myMarker.remove();
					playSound(R.raw.cancel);
					TeamMeUtils.resetFields(dialogContent);
					Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton("Create Game", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {


				}
			});

		}
		//SETTINGS DIALOG
		else if (id == 1){
			builder.setView(inflater.inflate(R.layout.settings_dialog, null));  
			builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog2, int id) {


				}
			});

		}
		//ABOUT DIALOG
		else if (id==2){
			builder.setView(inflater.inflate(R.layout.about_dialog, null));
			builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
		}

		//JOIN GAME DIALOG
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

					Drawable img = TeamMeUtils.getDrawableFromActivityNum(getApplicationContext(), markerInfo.getActivityNum() -1 );
					spinnerActivity.setVisibility(View.GONE);
					String text = TeamMeUtils.getActivityName(markerInfo.getActivityNum());
					rb1.setChecked(true);
					rb2.setChecked(false);
					rb1.setText(text);
					rb1.setButtonDrawable(img);
					rb2.setVisibility(View.GONE);
					edTxtCustomActivity.setVisibility(View.GONE);
				}
				else {
					String text = markerInfo.getCustomActivity();
					Drawable img = getResources().getDrawable( R.drawable.customgame);
					edTxtCustomActivity.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
					rb2.setChecked(true);
					rb2.setText(text);
					rb1.setChecked(false);
					rb2.setButtonDrawable(img);
					rb1.setVisibility(View.GONE);
					spinnerActivity.setVisibility(View.GONE);
					edTxtCustomActivity.setVisibility(View.GONE);
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

						selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
						selectedMarker = null;
						playSound(R.raw.cancel);
					}
				}
			})
			.setNegativeButton("Join Game", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Toast gameCreated = Toast.makeText(getApplicationContext(), "Game Joined!", Toast.LENGTH_SHORT);
					gameCreated.setGravity(Gravity.CENTER, 0, 0);
					gameCreated.show();
					if (selectedMarker != null){
						
						viewEnabled = false;
						viewButton.setAlpha((float)0.15);

						newActivePlayers = 
								Integer.parseInt(mapMarkers.get(selectedMarker.getTitle()).getActivePlayers()) + 1;
						newNeededPlayers = 
								Integer.parseInt(mapMarkers.get(selectedMarker.getTitle()).getNeededPlayers()) - 1;
						int markerid = mapMarkers.get(selectedMarker.getTitle()).getMarkerId();

						gameJoinParams = new GameJoinParameters("http://" + messagePasser.usedIp + ":80/android/project/joinGame.php", 
								mapMarkers.get(selectedMarker.getTitle()).getMarkerId(),
								newActivePlayers,
								newNeededPlayers);
						messagePasser.new GameJoinTask().execute(gameJoinParams);

						//mapMarkers.get(selectedMarker.getTitle()).getActivePlayers()

						selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
						selectedMarker = null;
						
						//Parse code
						ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
						query.whereEqualTo("markerId", markerid);
						query.findInBackground(new FindCallback<ParseObject>() {
							
							@Override
							public void done(List<ParseObject> objects, ParseException e) {
								// TODO Auto-generated method stub
								if(objects.size() > 0){
									Game game = (Game) objects.get(0);
									game.addMember(user);
									game.setNeededPlayers(Integer.toString(newNeededPlayers));
									game.setActivePlayers(Integer.toString(newActivePlayers));
									game.saveEventually();
									Toast.makeText(getApplicationContext(), game.getTeamName(), Toast.LENGTH_LONG).show();
								}
							}
						});
						
						refreshMap();
					}
				}
			});

		}
		//MAP FILTER DIALOG
		else if (id == 4){
			builder.setView(inflater.inflate(R.layout.map_filter, null));

			builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {



				}
			});

		}
		//EDIT GAME DIALOG
		else if (id == 6){
			builder.setView(editDialogContent);
			String title = selectedMarker.getTitle();
			Log.d("title is: ", title);
			MarkerInfo markerInfo = mapMarkers.get(title);

			RadioButton rb1 = (RadioButton)editDialogContent.findViewById(R.id.radio_pirates);
			RadioButton rb2 = (RadioButton)editDialogContent.findViewById(R.id.radio_ninjas);


			EditText edTxtTeamName = (EditText) editDialogContent.findViewById(R.id.teamName);
			if (markerInfo != null)edTxtTeamName.setText(markerInfo.getTeamName());
			EditText edTxtCustomActivity = (EditText) editDialogContent.findViewById(R.id.username5);

			Spinner spinnerActivity = (Spinner) editDialogContent.findViewById(R.id.spinner1);
			if (markerInfo != null)
				if (markerInfo.getActivityNum()-1 != -1){
					spinnerActivity.setSelection(markerInfo.getActivityNum()-1);
					rb1.setChecked(true);
					rb2.setChecked(false);
				}
				else {
					edTxtCustomActivity.setText(markerInfo.getCustomActivity());
					rb2.setChecked(true);
					rb1.setChecked(false);
				}

			EditText edTxtPlayersActive = (EditText) editDialogContent.findViewById(R.id.num_players);
			if (markerInfo != null)edTxtPlayersActive.setText(markerInfo.getActivePlayers());
			EditText edTxtPlayersNeeded = (EditText) editDialogContent.findViewById(R.id.num_players2);
			if (markerInfo != null)edTxtPlayersNeeded.setText(markerInfo.getNeededPlayers());
			TimePicker pickFinishTime = (TimePicker) editDialogContent.findViewById(R.id.timePicker1);


			builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					if (selectedMarker != null){
						viewEnabled = false;
						viewButton.setAlpha((float)0.15);

						selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
						selectedMarker = null;
						playSound(R.raw.cancel);

						TeamMeUtils.resetFields(dialogContent);
					}
				}
			})
			.setNegativeButton("Edit Game", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mapMarkers.get(selectedMarker.getTitle()).getNeededPlayers();
					gameUpdateParams = new GameUpdateParameters("http://" + messagePasser.usedIp + ":80/android/project/updateGame.php",
							paramPoint,
							mapMarkers.get(selectedMarker.getTitle()).getUserId(), 
							mapMarkers.get(selectedMarker.getTitle()).getFinishHour(),
							mapMarkers.get(selectedMarker.getTitle()).getFinishMinute(),
							mapMarkers.get(selectedMarker.getTitle()).getActivePlayers(), 
							mapMarkers.get(selectedMarker.getTitle()).getNeededPlayers(),
							mapMarkers.get(selectedMarker.getTitle()).getCustomActivity(),
							mapMarkers.get(selectedMarker.getTitle()).getTeamName(),
							mapMarkers.get(selectedMarker.getTitle()).getActivityNum(),
							mapMarkers.get(selectedMarker.getTitle()).getMarkerId());
					messagePasser.new GameUpdateTask().execute(gameUpdateParams);
					//					gameJoinParams = new GameJoinParameters("http://" + messagePasser.usedIp + ":80/android/project/joinGame.php", 
					//							mapMarkers.get(selectedMarker.getTitle()).getMarkerId(),
					//							newActivePlayers,
					//							newNeededPlayers);
					//					messagePasser.new GameJoinTask().execute(gameJoinParams);
				}
			});

		}
		// 3. Get the AlertDialog from create()
		dialog = builder.create();
		dialog.show();


		//Add a listener to create game button so that we only create when the form is complete (no empty team name)
		if (id == 0){
			dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
			{            
				@Override
				public void onClick(View v)
				{
					//setContentView( R.layout.create_dialog );
					EditText edTxtTeamName = (EditText) dialogContent.findViewById(R.id.teamName);
					EditText edTxtCustomActivity = (EditText) dialogContent.findViewById(R.id.username5);
					Spinner spinnerActivity = (Spinner) dialogContent.findViewById(R.id.spinner1);
					RadioButton rb = (RadioButton) dialogContent.findViewById(R.id.radio_pirates);
					EditText edTxtPlayersActive = (EditText) dialogContent.findViewById(R.id.num_players);
					EditText edTxtPlayersNeeded = (EditText) dialogContent.findViewById(R.id.num_players2);
					TimePicker pickFinishTime = (TimePicker) dialogContent.findViewById(R.id.timePicker1);

					if ((!rb.isChecked() && (edTxtCustomActivity.getText().toString().equals("") || edTxtCustomActivity.getText().toString() == null) 
							) || (edTxtTeamName.getText().toString().equals("") || edTxtTeamName.getText().toString() == null)){
						Toast gameCreated = Toast.makeText(getApplicationContext(), "Form Incomplete! Be sure to select team name and activity!", Toast.LENGTH_SHORT);
						gameCreated.setGravity(Gravity.CENTER, 0, 0);
						gameCreated.show();
						return;
					}

					String activity = spinnerActivity.getSelectedItem().toString();
					Integer activityNum = 0;

					playSound(R.raw.whistle);
					if (rb.isChecked()){

						activityNum = TeamMeUtils.getActivityNumber(activity);
					}

					SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
					String userId = mPrefs.getString("userId", "noUserNameFound!");
					String teamName = edTxtTeamName.getText().toString();
					String customActivity = edTxtCustomActivity.getText().toString();
					String activePlayers = edTxtPlayersActive.getText().toString();
					String neededPlayers = edTxtPlayersNeeded.getText().toString();
					String finishTimeHour = String.valueOf(pickFinishTime.getCurrentHour());
					String finishTimeMinute = String.valueOf(pickFinishTime.getCurrentMinute());


					params = new CoordParameters("http://" + messagePasser.usedIp + ":80/android/project/updateMarkers.php", paramPoint, userId, 
							finishTimeHour, finishTimeMinute, activePlayers, neededPlayers, customActivity, teamName, activityNum);
					messagePasser.new SendCoordsTask().execute(params);
					Toast gameCreated = Toast.makeText(getApplicationContext(), "Your Game Was Created!", Toast.LENGTH_SHORT);
					gameCreated.setGravity(Gravity.CENTER, 0, 0);
					gameCreated.show();

					createButton.setAlpha((float)0.15);
					myMarker.setIcon(TeamMeUtils.getIconFromActivityNum(activityNum, false));

					MarkerInfo mi = new MarkerInfo(null);
					mi.setAllFields(activityNum,userId, activePlayers, neededPlayers,finishTimeHour, finishTimeMinute, customActivity, teamName,0);


					mapMarkers.put(""+index,mi );
					myMarker.setTitle(""+index);
					index++;
					markerOptions = null;
					myMarker = null;
					createEnabled = false;
					//though not computationally efficient, this is where we will get the markerId from.
					//the title is reset in the getResponse method to the right value.
					getPasser = messagePasser.new GetRequest();
					getPasser.responder = MainActivity.this;
					getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabMarkers.php?id=1"); 
					TeamMeUtils.resetFields(dialogContent);
					refreshMap();

					dialog.dismiss();
					//else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
				}
			});




		}
		Button positive_button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

		if (positive_button != null){
			positive_button.setBackgroundColor(Color.parseColor("#000000"));
			positive_button.setTextColor(Color.parseColor("#FC8F00"));  
			positive_button.setTypeface(null, Typeface.BOLD);
			positive_button.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					Button b = (Button) arg0;
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						b.setBackgroundColor(Color.parseColor("#383838"));
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						b.setBackgroundColor(Color.parseColor("#000000"));
					}
					return false;
				}
			});
		}
		Button negative_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		if (negative_button != null){
			negative_button.setBackgroundColor(Color.parseColor("#000000"));
			negative_button.setTextColor(Color.parseColor("#FC8F00"));  
			negative_button.setTypeface(null, Typeface.BOLD);
			negative_button.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					Button b = (Button) arg0;
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						b.setBackgroundColor(Color.parseColor("#383838"));
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						b.setBackgroundColor(Color.parseColor("#000000"));
					}
					return false;
				}
			});
		}

		return super.onCreateDialog(id);
	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Standard Overridden Methods
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null)
			dialog.dismiss();
		if (googleMap != null)
			googleMap.clear();
		if (loadingToast != null)
			loadingToast.cancel();
		selectedMarker = null;
		mapMarkers = new HashMap<String, MarkerInfo>();
		viewEnabled = false;
		createEnabled = false;
		if (viewButton != null)
			viewButton.setAlpha((float) 0.15);
		if (createButton != null)
			createButton.setAlpha((float)0.15);

		if(mSounds != null) {
			mSounds.release();
			mSounds = null;
		}		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Parse Code
		Parse.initialize(this, "ybMbNsW5K7M3tWC0hq5d2JJyiDDJfDW65eGRcYRc", "ny76yoFFCO2ACumEzDDzOqHs40udxmyaJkjHG5eo");
		ParseObject.registerSubclass(Game.class);
		
		
		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
		if (mPrefs.getBoolean("loggedOut", true)){
			Intent intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			finish();
			return;
		}
		user = ParseUser.getCurrentUser();
		setContentView(R.layout.activity_main);
		createSoundPool();
		fixZoom();
		mSoundOn = mPrefs.getBoolean("sound", true);

		viewButton = (Button)findViewById(R.id.Button02);
		viewButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				Button b = (Button) arg0;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundColor(Color.parseColor("#E38100"));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundColor(Color.parseColor("#FC8F00"));
				}
				return false;
			}
		});
		createButton = (Button)findViewById(R.id.Button01);
		createButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				Button b = (Button) arg0;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundColor(Color.parseColor("#E38100"));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundColor(Color.parseColor("#FC8F00"));
				}
				return false;
			}
		});
		messagePasser = new Networking(MainActivity.this);
		usedIp = messagePasser.amazonServerIp;
		if (mapMarkers == null)
			mapMarkers = new HashMap<String, MarkerInfo>();
		try {
			Log.e("loading map. . . .", "loading map. . . ");
			// Loading map
			initializeMap();

		} catch (Exception e) {
			Log.e("catch. . .", "catch. . .");
			e.printStackTrace();
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
		initializeMap();
		fixZoom();
		createSoundPool();
		mSoundOn = mPrefs.getBoolean("sound", true);


	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////All the options from the overflow menu can be found here//
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
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
		user.logOut();
		finish();
	}
	public void settingsDialog(MenuItem item){
		showDialog(1);
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		Switch s = (Switch)dialog.findViewById(R.id.sound_switch2);
		if (s!=null)
			s.setChecked(mSoundOn);
		Switch s2 = (Switch)dialog.findViewById(R.id.anim_switch);
		if (s2!=null)
			s2.setChecked(mPrefs.getBoolean("animationOn", true));
	}
	public void filterDialog(MenuItem item){
		showDialog(4);

		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		Log.d("filterint:", ""+mPrefs.getInt("filter", -5)); 
		RadioButton[] radiobuttons = new RadioButton[4];
		radiobuttons[0] = (RadioButton) dialog.findViewById(R.id.nofilterbutton);
		radiobuttons[1] = (RadioButton) dialog.findViewById(R.id.activitybutton);
		radiobuttons[2] = (RadioButton) dialog.findViewById(R.id.customactivitybutton);

		EditText t = (EditText) dialog.findViewById(R.id.customactivity);

		t.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				customactivitytext();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		}); 
		String customActivity = mPrefs.getString("customActivity", "");

		Spinner s = (Spinner) dialog.findViewById(R.id.activity);
		s.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				RadioButton b = (RadioButton)dialog.findViewById(R.id.activitybutton);
				if (!b.isChecked())
					restrictMarkerLoading = true;
				filteractivityspinner();
				restrictMarkerLoading = false;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				RadioButton b = (RadioButton)dialog.findViewById(R.id.activitybutton);
				if (!b.isChecked())
					restrictMarkerLoading = true;
				filteractivityspinner();
				restrictMarkerLoading = false;

			}
		});
		String spinnerActivity = s.getSelectedItem().toString();
		int activityNumber = TeamMeUtils.getActivityNumber(spinnerActivity);

		int filter = mPrefs.getInt("filter", -1);
		restrictMarkerLoading = true;
		if (filter == -1){

			radiobuttons[0].setChecked(true);
		}
		else if (filter == 0){
			radiobuttons[2].setChecked(true);
			t.setText(customActivity);
		}
		else {
			radiobuttons[1].setChecked(true);
			s.setSelection(filter-1);
		}
		restrictMarkerLoading = false;
	}
	public void aboutDialog(MenuItem item){
		showDialog(2);

	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Settings options can be found here 
	////////Right now, this includes: ANIMATION and SOUND 
	////////////////////////////////////////////////////////////////
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
	//Clicking Animation from settings page directs you here
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
	//Clicking Sound from settings page directs you here
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
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Filter options can be found here 
	////////////////////////////////////////////////////////////////
	public void setFilterSettings(int type){
		SharedPreferences mPrefs =  getSharedPreferences("ttt_prefs", MODE_PRIVATE);

		RadioButton b = (RadioButton) dialog.findViewById(R.id.activitybutton);
		if (!b.isChecked() && type == 4)
			return;

		RadioButton b2 = (RadioButton) dialog.findViewById(R.id.customactivitybutton);
		if (!b2.isChecked() && type == 3)
			return;

		EditText t = (EditText) dialog.findViewById(R.id.customactivity);
		String customActivity = t.getText().toString();

		Spinner s = (Spinner) dialog.findViewById(R.id.activity);
		String spinnerActivity = s.getSelectedItem().toString();
		int activityNumber = TeamMeUtils.getActivityNumber(spinnerActivity);


		SharedPreferences.Editor ed = mPrefs.edit();

		if (type == 0)
			ed.putInt("filter", -1);
		else if (type==1 || (b.isChecked() && type == 4)){
			ed.putInt("filter", activityNumber);
			Log.d("Storing....", ""+activityNumber);
		}
		else if (type == 2 || b2.isChecked() && type == 3){
			ed.putInt("filter", 0);
			ed.putString("customActivity", customActivity);
		}

		ed.apply();
	}
	public void noFilter(View view){
		setFilterSettings(0);
		refreshMap();
	}
	public void activity(View view){
		setFilterSettings(1);
		refreshMap();

	}
	public void customactivity(View view){
		setFilterSettings(2);
		refreshMap();

	}
	public void filteractivityspinner(){
		setFilterSettings(4);
		refreshMap();
	}
	public void customactivitytext(){
		setFilterSettings(3);
		refreshMap();
	}

	private boolean passesFilter(MarkerInfo marker) {
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		int filterType = mPrefs.getInt("filter", -1);
		String customActivity = mPrefs.getString("customActivity", "");
		if (filterType == 0 && (marker.getActivityNum()== 0 ) && marker.getCustomActivity().equalsIgnoreCase(customActivity))
			return true;

		else if (filterType != 0 && (filterType == marker.getActivityNum() || filterType == -1) )
			return true;

		return false;
	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Map Initializing, Map/Marker Loading, Everything Map 
	/////////Related
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////

	private void refreshMap(){
		if (restrictMarkerLoading == true)
			return;
		if (googleMap != null)
		googleMap.clear();
		selectedMarker = null;
		mapMarkers = new HashMap<String, MarkerInfo>();
		viewEnabled = false;
		createEnabled = false;
		if (viewButton != null)
			viewButton.setAlpha((float) 0.15);
		if (createButton != null)
			createButton.setAlpha((float)0.15);

		getPasser = messagePasser.new GetRequest();
		getPasser.responder = this;

		getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabMarkers.php?id=1"); 


	}
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
				public boolean onMarkerClick(Marker marker) {

					if (marker.getTitle().equals("-1")){
						return true;
					}
					viewEnabled = true;
					viewButton.setAlpha((float) 0.70);

					if (selectedMarker != null){
						if (passesFilter(mapMarkers.get(selectedMarker.getTitle()))){
					
						selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(
								mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
							}
					}
					paramPoint = marker.getPosition();
					selectedMarker = marker;
					createEnabled = false;
					if (myMarker != null)
						myMarker.remove();
					if (createButton != null)
						createButton.setAlpha((float)0.15);
					if (selectedMarker != null){
						
						if (passesFilter(mapMarkers.get(selectedMarker.getTitle()))){
					selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(
							mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), true));
						}
					}
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
					if (selectedMarker != null){
						selectedMarker.setIcon(TeamMeUtils.getIconFromActivityNum(
								mapMarkers.get(selectedMarker.getTitle()).getActivityNum(), false));
					
					selectedMarker = null;
					viewEnabled = false;
					}
					if (viewButton != null)
						viewButton.setAlpha((float)0.15);
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

	//http://stackoverflow.com/questions/14071230/android-maps-library-v2-zoom-controls-custom-position
	public void fixZoom(){
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		View zoomControls = mapFragment.getView().findViewById(0x1);
		if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
					getResources().getDisplayMetrics());
			params.setMargins(margin, margin, margin, margin);
		}
	}

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

				loadingToast = Toast.makeText(getApplicationContext(), "Loading Games" , Toast.LENGTH_LONG);
				if (initialLoad == false)loadingToast.show();
				initialLoad = true;
				final int n = geodata.length();
				index = n;
				int uniqueId = 0;
				for (int i = 0; i < n; i++) {
					Log.d("Number of markers", "" + n);
					//Log.d("LAT", "meow" + geodata.getJSONObject(i).getDouble("lat"));
					//Log.d("LONG", "meow" + geodata.getJSONObject(i).getDouble("lng"));
					uniqueId  = (geodata.getJSONObject(i).getInt("markerId"));

					markerOptions = new MarkerOptions().position(new LatLng(geodata.getJSONObject(i).getDouble("lat"), geodata.getJSONObject(i).getDouble("lng")));
					markerOptions.icon(TeamMeUtils.getIconFromActivityNum(Integer.parseInt(geodata.getJSONObject(i).getString("activityNum")), false));
					markerOptions.title(""+ i);
					
					MarkerInfo marker = new MarkerInfo(geodata.getJSONObject(i));

					if (passesFilter(marker) == true){
						googleMap.addMarker(markerOptions);
					mapMarkers.put(""+i, marker);
					
					}
					Log.d("title", ""+i);
				}
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/////////Create and View Buttons go through here ///////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////

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
			SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
			String userId = mPrefs.getString("userId", "null");
			if (selectedMarker != null){
				if (mapMarkers.containsKey(selectedMarker.getTitle())){
					String getUserIdFromMarker = mapMarkers.get(selectedMarker.getTitle()).getUserId();
					Log.d("UID:", getUserIdFromMarker);
					if (getUserIdFromMarker.equals(userId)){
						showDialog(6);
						return;
					}
				}
			}
			showDialog(3);

		}
	}
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////



	/*
	GameAdmin gameadmin = new GameAdmin();
	getPasser = messagePasser.new GetRequest();
	getPasser.responder = gameadmin;
	getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabUserProfile.php?email=" + mPrefs.getString("email",""));
	 */
}

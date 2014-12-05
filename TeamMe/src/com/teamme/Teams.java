package com.teamme;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Teams extends Activity {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	private RadioButton current;
	private RadioButton saved;
	private Button viewTeam;
	ParseUser user;
	private TextView text;
	private Toast currentToast;
	private Spinner s;
	private SharedPreferences mPrefs;
	@Override 
	public void onPause(){
		super.onPause();
		if (currentToast != null)
			currentToast.cancel();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.teams);
		user = ParseUser.getCurrentUser();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		current = (RadioButton) findViewById(R.id.current_teams);
		saved = (RadioButton) findViewById(R.id.saved_teams);
		viewTeam = (Button) findViewById(R.id.view_team_button);
		s = (Spinner) findViewById(R.id.teams);
		setTeams();
		
		viewTeam.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(current.isChecked()){
					ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
					query.whereEqualTo("admin", user.getObjectId().toString());
					query.addDescendingOrder("markerId");
					
					query.findInBackground(new FindCallback<ParseObject>() {
						
						@Override
						public void done(List<ParseObject> objects, ParseException e) {
							if(e == null && objects.size() > 0){
								ParseObject game = objects.get(0);
								
								Intent intent = new Intent(getApplicationContext(), ViewTeam.class);
								String marker = game.getNumber("markerId").toString();
								intent.putExtra("team_id", marker);
								String marker2 = intent.getStringExtra("team_id");
//								Toast.makeText(getApplicationContext(), marker2, Toast.LENGTH_LONG).show();
								startActivity(intent);
								finish();
							}
							else if(objects.size() == 0){
								//runSecondQuery();
								if (currentToast != null)
									currentToast.cancel();
								currentToast = Toast.makeText(getApplicationContext(), "You have not joined any games yet.", Toast.LENGTH_LONG);
								currentToast.show();
								
							}
						}
					});
					
					
				}
				else if (saved.isChecked()){
					String savedteam = mPrefs.getString("savedteams", "");
					if (savedteam.equals("")){
						if (currentToast != null)
							currentToast.cancel();
						currentToast = Toast.makeText(getApplicationContext(), "You have not saved any games yet.", Toast.LENGTH_LONG);
						currentToast.show();
						return;
						
					}
					Intent intent = new Intent(getApplicationContext(), ViewTeam.class);
					
					intent.putExtra("savedteam", true);
					intent.putExtra("savedteamname", s.getItemAtPosition(0).toString());
					startActivity(intent);
					finish();
					
				}
			}
		});
	
	}
	private void setTeams() {
		String savedteam = mPrefs.getString("savedteams", "");
		if (savedteam.equals(""))
			return;
		
		String[] savedteams = savedteam.split(",");
		
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			    this, android.R.layout.simple_spinner_item, savedteams);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			s.setAdapter(adapter);
		
	}
	public void runSecondQuery(){
		ParseQuery<ParseObject> q = ParseQuery.getQuery("game");
//		q.addAscendingOrder("markerId");
		q.orderByAscending("markerId");
		q.findInBackground(new FindCallback<ParseObject>() {
		

			@Override
			public void done(List<ParseObject> objs, ParseException e){
				ParseObject g = null;
				for(ParseObject game : objs){
					JSONArray members = game.getJSONArray("members");
						if (members != null)
						g = game;
						break;
					
					}
				
				if(g != null){
					Intent intent = new Intent(getApplicationContext(), ViewTeam.class);
					String marker = g.getNumber("markerId").toString();
					intent.putExtra("team_id", marker);
					startActivity(intent);
					finish();
				}
				else{
					
					if (currentToast != null)
						currentToast.cancel();
					currentToast = Toast.makeText(getApplicationContext(), "You have not joined any games yet.", Toast.LENGTH_LONG);
					currentToast.show();
				}
				}
		});
	}
	
	//USE THIS TO CALL THE VIEW TEAM PAGE!
	/*
	Intent intent = new Intent(getApplicationContext(), ViewTeam.class);
	intent.putExtra("team_id", team_id); //adds identifier for team ID , goes to savedInstanceState, used savedInstanceState.get(team_id) to get the team id while you are in ViewTeam.java
	startActivity(intent);*/
	

}

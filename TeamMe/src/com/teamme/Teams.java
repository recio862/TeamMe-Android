package com.teamme;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class Teams extends Activity {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.teams);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	
	}
	
	
	//USE THIS TO CALL THE VIEW TEAM PAGE!
	/*
	Intent intent = new Intent(getApplicationContext(), ViewTeam.class);
	intent.putExtra("team_id", team_id); //adds identifier for team ID , goes to savedInstanceState, used savedInstanceState.get(team_id) to get the team id while you are in ViewTeam.java
	startActivity(intent);*/
	

}

package com.teamme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
	Intent intent = new Intent(getApplicationContext(), ViewProfile.class);
	intent.putExtra("team_id", team_id); //adds identifier for team ID , goes to savedInstanceState, used savedInstanceState.get(team_id) to get the team id while you are in ViewTeam.java
	startActivity(intent);*/
	

}

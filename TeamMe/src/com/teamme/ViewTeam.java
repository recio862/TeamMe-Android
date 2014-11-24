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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewTeam extends Activity {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_team);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.scv);
		
		//set number of profiles in this team
		int numberOfProfilesToLoad = 0;
		
		for (int i = 0 ; i < numberOfProfilesToLoad; i++){
		Button profile = new Button(this);
		profile.setText("User Name Goes Here"); //This will be based on the profile info 
		profile.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
		                                    LayoutParams.WRAP_CONTENT));
		ll.addView(profile);
		profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	int profile_id = 0; //Get profile id from server 
            	Intent intent = new Intent(getApplicationContext(), ViewProfile.class);
            	intent.putExtra("profile_id", profile_id);
        		startActivity(intent);
        	
                
            }
         });
		
		}
		
	}
	
	

}
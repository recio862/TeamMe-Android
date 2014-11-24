package com.teamme;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewProfile extends Activity {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	public Networking messagePasser;
	public Networking.GetRequest getPasser;
	public Networking.GetRequest profilePasser;
	public String usedIp;
	EditText username;
	EditText email;
	EditText phone; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_profile);
		
	}
	


}

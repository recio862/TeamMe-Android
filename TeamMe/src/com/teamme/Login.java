package com.teamme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity{

	EditText email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		
		login.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				Button b = (Button) arg0;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundColor(Color.parseColor("#E38100"));
					login(arg0);
			        } else if (event.getAction() == MotionEvent.ACTION_UP) {
			          b.setBackgroundColor(Color.parseColor("#FC8F00"));
			     
			        }
				return false;
			}
			
		});
		
		register.setOnTouchListener(new OnTouchListener() {
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
		
	}
	
	public void login(View view){
	
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		SharedPreferences.Editor ed = mPrefs.edit();

		email = (EditText) findViewById(R.id.email);
		ed.putBoolean("loggedOut", false);
		ed.putString("email",email.getText().toString());
		ed.apply();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			finish();
			
	}
	
	public void register(View view){
		//TO-DO create dialog with all fields necessary to create a profile
		//TO-DO connect data from dialog to server
		
	}
}

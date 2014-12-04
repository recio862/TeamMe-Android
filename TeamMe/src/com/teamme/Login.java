package com.teamme;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Login extends Activity{

	EditText email;
	EditText password;
	String emailtxt;
	String passwordtxt;
	private Toast invalidLoginToast;
	private SharedPreferences mPrefs;
	private Toast currentToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		 mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		
		email.setText(mPrefs.getString("email", ""));
		login.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				Button b = (Button) arg0;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundColor(Color.parseColor("#E38100"));
					if (mPrefs.getBoolean("loggedOut", true))
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
					register(arg0);
				
			        } else if (event.getAction() == MotionEvent.ACTION_UP) {
			          b.setBackgroundColor(Color.parseColor("#FC8F00"));
			        }
				return false;
			}
		});
		
	}
	
	public void login(View view){
	 
		final SharedPreferences.Editor ed = mPrefs.edit();
		
		emailtxt = email.getText().toString();
		passwordtxt = password.getText().toString();
		
		//Send data to parse
		ParseUser.logInInBackground(emailtxt, passwordtxt,
				new LogInCallback(){
					public void done(ParseUser user, ParseException e){
						if(user != null){
							//if user exists and is authenticated
							if (mPrefs.getBoolean("loggedOut", true)){
							Intent intent = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(intent);
							}
							ed.putBoolean("loggedOut", false);
							ed.putString("email", emailtxt);
							ed.apply();
							Log.d("test", "test");
							finish();
						}
						else{
							if (currentToast != null)
								currentToast.cancel();
							currentToast = Toast.makeText(getApplicationContext(), "Invalid Login Credentials!" , Toast.LENGTH_SHORT);
							currentToast.show();
						}
							
					}
		});

//		email = (EditText) findViewById(R.id.email);
//		ed.putBoolean("loggedOut", false);
//		ed.putString("email",email.getText().toString());
//		ed.apply();
//			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//			startActivity(intent);
//			finish();
			
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
	
		if (currentToast != null)
			currentToast.cancel();
	}
	
	public void register(View view){
		//TO-DO create dialog with all fields necessary to create a profile
		//TO-DO connect data from dialog to server
		
		emailtxt = email.getText().toString();
		passwordtxt = password.getText().toString();
		
		//force user to fill up the form
		if(emailtxt.equals("") || passwordtxt.equals("")){
			currentToast = Toast.makeText(getApplicationContext(), "Please fill in email and password fields", Toast.LENGTH_LONG);
			currentToast.show();
		} else{
			// Save new user data into parse
			ParseUser user = new ParseUser();
			user.setUsername(emailtxt);
			user.setEmail(emailtxt);
			user.setPassword(passwordtxt);
			user.put("phoneNumber", "");
			user.signUpInBackground(new SignUpCallback(){
				public void done(ParseException e){
					if(e == null){
						if (currentToast != null)
							currentToast.cancel();
						currentToast = Toast.makeText(getApplicationContext(), "You successfully registered with TeamMe! \nPlease Log in." , Toast.LENGTH_LONG);
								currentToast.show();
								return;
					}
					else{
						if (currentToast != null)
							currentToast.cancel();
						currentToast = Toast.makeText(getApplicationContext(), "An error occurred. Please try registering again.", Toast.LENGTH_LONG);
						currentToast.show();
					}
				}
			});
		}
		
	}
}

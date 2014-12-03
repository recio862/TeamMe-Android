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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		
		
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
					register(arg0);
				
			        } else if (event.getAction() == MotionEvent.ACTION_UP) {
			          b.setBackgroundColor(Color.parseColor("#FC8F00"));
			        }
				return false;
			}
		});
		
	}
	
	public void login(View view){
	
		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		final SharedPreferences.Editor ed = mPrefs.edit();
		
		emailtxt = email.getText().toString();
		passwordtxt = password.getText().toString();
		
		//Send data to parse
		ParseUser.logInInBackground(emailtxt, passwordtxt,
				new LogInCallback(){
					public void done(ParseUser user, ParseException e){
						if(user != null){
							//if user exists and is authenticated
							ed.putBoolean("loggedOut", false);
							ed.putString("email", emailtxt);
							ed.apply();
							Intent intent = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(intent);
							finish();
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
	
	public void register(View view){
		//TO-DO create dialog with all fields necessary to create a profile
		//TO-DO connect data from dialog to server
		
		emailtxt = email.getText().toString();
		passwordtxt = password.getText().toString();
		
		//force user to fill up the form
		if(emailtxt.equals("") || passwordtxt.equals("")){
			Toast.makeText(getApplicationContext(), "Please fill in email and password fields", Toast.LENGTH_LONG).show();
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
						Toast.makeText(getApplicationContext(), "You successfully registered with TeamMe!, please Log In" , Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(getApplicationContext(), "An error occurred. Please try registering again.", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		
	}
}

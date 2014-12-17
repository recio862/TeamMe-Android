package com.teamme;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class Login extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

	EditText email;
	EditText password;
	String emailtxt;
	String passwordtxt;
	private Toast invalidLoginToast;
	private SharedPreferences mPrefs;
	private Toast currentToast;
	private boolean loggedin;

  /* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;
	  /* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	private boolean mIntentInProgress;
	  /* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;
	
	//for gmail login
	public void onClick(View view) {
		  if (view.getId() == R.id.sign_in_button
		    && !mGoogleApiClient.isConnecting()) {
		    mSignInClicked = true;
		    resolveSignInError();
		  }
	}
	@Override
	protected void onStart() {
		    super.onStart();
		    mGoogleApiClient.connect();
	}
	
	 @Override
	 protected void onStop() {
		    super.onStop();
		    if (mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.disconnect();
		    }
	  }
	 public void onConnectionFailed(ConnectionResult result) {
		  if (!mIntentInProgress) {
		    // Store the ConnectionResult so that we can use it later when the user clicks
		    // 'sign-in'.
		    mConnectionResult = result;

		    if (mSignInClicked) {
		      // The user has already clicked 'sign-in' so we attempt to resolve all
		      // errors until the user is signed in, or they cancel.
		      resolveSignInError();
		    }
		  }
	 }
	@Override
	public void onConnected(Bundle connectionHint) {
	   mSignInClicked = false;
	   Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
	}
	 
	@Override
	public void onConnectionSuspended(int cause) {
		  mGoogleApiClient.connect();
	}
		
	 @Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		  if (requestCode == RC_SIGN_IN) {
		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnecting()) {
		      mGoogleApiClient.connect();
		    }
		}
        }
	 
	
		/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
	  if (mConnectionResult.hasResolution()) {
	    try {
	      mIntentInProgress = true;
		startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
	          RC_SIGN_IN, null, 0, 0, 0);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      mIntentInProgress = false;
	      mGoogleApiClient.connect();
	    }
	  }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//this is for gmail login.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(Plus.API)
	        .addScope(Plus.SCOPE_PLUS_LOGIN)
	        .build();
	        
	        findViewById(R.id.sign_in_button).setOnClickListener(this);

	        
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		loggedin= false;
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
		/*
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
		*/

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
	
	public void register(final View view){
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
	/*		ParseUser user = new ParseUser();
			user.setUsername(emailtxt);
			user.setEmail(emailtxt);
			user.setPassword(passwordtxt);
			user.put("phoneNumber", "");
			user.signUpInBackground(new SignUpCallback(){
				public void done(ParseException e){
					if(e == null){
						if (currentToast != null)
							currentToast.cancel();
						currentToast = Toast.makeText(getApplicationContext(), "You successfully registered with TeamMe! \nLogging in." , Toast.LENGTH_LONG);
								currentToast.show();
								loggedin = true;
								login(view);
								return;
					}
					else{
						if (currentToast != null)
							currentToast.cancel();
						if (loggedin == false){
						currentToast = Toast.makeText(getApplicationContext(), "An error occurred. Please try registering again.", Toast.LENGTH_LONG);
						currentToast.show();
						}
					}
				}
			});*/
		}
		
	}
}

package com.teamme;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.teamme.Networking.AsyncResponse;

public class Login extends Activity implements AsyncResponse{
    public int userId;
	EditText email;
	private Networking.GetRequest getPasser;
	private Networking messagePasser;
	public String usedIp;
	private String password;

	
	public void getResponse(String jsonDownloadedMarkersString){
		if (jsonDownloadedMarkersString.equals("0") || jsonDownloadedMarkersString.equals(null) 
				|| jsonDownloadedMarkersString.equals("") || jsonDownloadedMarkersString.equals("[]")){
			Log.e("PROFILE RESPONSE STRING CAUGHT", jsonDownloadedMarkersString);
			return;
		}
		else{
			try{
				//final JSONArray userData = new JSONArray(jsonDownloadedMarkersString);
				final JSONObject userData = new JSONObject(jsonDownloadedMarkersString);
				
				SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
				SharedPreferences.Editor ed = mPrefs.edit();
				ed.putBoolean("loggedOut", userData.getBoolean("loggedOut"));
				ed.putString("email",email.getText().toString());
				ed.putString("userId", userData.getString("userId"));
				ed.putString("username",userData.getString("userName"));
				ed.putString("password",userData.getString("password"));
				ed.putString("phone",userData.getString("phone"));
				ed.apply();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
//				loadingToast = Toast.makeText(getApplicationContext(), "Loading Games" , Toast.LENGTH_LONG);
//				if (initialLoad == false)loadingToast.show();
//				initialLoad = true;
				//final int n = geodata.length();
//				index = n;
//				int uniqueId = 0;
//				for (int i = 0; i < n; i++) {
//					Log.d("Number of markers", ""+ n);
//					//Log.d("LAT", "meow" + geodata.getJSONObject(i).getDouble("lat"));
//					//Log.d("LONG", "meow" + geodata.getJSONObject(i).getDouble("lng"));
//					uniqueId  = (geodata.getJSONObject(i).getInt("markerId"));
//
//					markerOptions = new MarkerOptions().position(new LatLng(geodata.getJSONObject(i).getDouble("lat"), geodata.getJSONObject(i).getDouble("lng")));
//					markerOptions.icon(TeamMeUtils.getIconFromActivityNum(Integer.parseInt(geodata.getJSONObject(i).getString("activityNum")), false));
//					markerOptions.title(""+ i);
//					
//					MarkerInfo marker = new MarkerInfo(geodata.getJSONObject(i));
//
//					if (passesFilter(marker) == true){
//						googleMap.addMarker(markerOptions);
//					mapMarkers.put(""+i, marker);
//					
//					}
					//Log.d("title", ""+i);
				//}
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		
		messagePasser = new Networking(this);
		usedIp = messagePasser.amazonServerIp;
		
		getPasser = messagePasser.new GetRequest();
		getPasser.responder = this;
	
		login.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {

				Button b = (Button) arg0;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundColor(Color.parseColor("#E38100"));
					login(arg0);
					try{
						email = (EditText) findViewById(R.id.email);
						password = ((EditText) findViewById(R.id.password)).getText().toString();
						getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/checkLogin.php?email=" + email.getText().toString()
							+ "&password=" + password); 
						}
						catch(Exception ex){
							throw new IllegalStateException();
						}
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
					try{
						email = (EditText) findViewById(R.id.email);
						password = ((EditText) findViewById(R.id.password)).getText().toString();
						getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/checkLogin.php?email=" + email.getText().toString()
							+ "&password=" + password); 
						}
						catch(Exception ex){
							throw new IllegalStateException();
						}
			        } else if (event.getAction() == MotionEvent.ACTION_UP) {
			          b.setBackgroundColor(Color.parseColor("#FC8F00"));
			        }
				return false;
			}
		});
		
	}
	
	public void login(View view){

	}
	
	public void register(View view){

	}
}

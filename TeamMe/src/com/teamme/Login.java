package com.teamme;

import java.util.Random;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Login extends Activity{

	EditText email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		
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

package com.teamme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class Login extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Button login = (Button) findViewById(R.id.login_button);
		Button register = (Button) findViewById(R.id.register_button);
		
	}
	
	public void login(){
		//TO-DO create dialog with username/password fields to handle login
		//TO-DO connect data from fields to server and authenticate
	}
	
	public void register(){
		//TO-DO create dialog with all fields necessary to create a profile
		//TO-DO connect data from dialog to server
		
	}
}

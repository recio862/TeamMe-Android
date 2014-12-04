package com.teamme;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("game")
public class GameAdmin extends ParseObject{
	public GameAdmin(){
		
	}
	
	
	
	public void setUserId(String userid){
		put("userId", userid);
	}



	public void setUser(String email) {
		put("user",email);
		
	}
	
	
}










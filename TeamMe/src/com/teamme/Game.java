package com.teamme;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("game")
public class Game extends ParseObject{
	public Game(){
		
	}
	
	public String getAdminId(){
		ParseUser admin = (ParseUser) get("admin");
		return admin.getObjectId().toString();
	}
	
	public String getAdminName(){
		ParseUser admin = (ParseUser) get("admin");
		return admin.getUsername().toString();
	}
	
	public String getTeamName(){
		return getString("teamName");
	}
	
	
	public void setTeamName(String team){
		put("teamName", team);
	}
	
	public void setAdmin(ParseUser currentUser){
		put("admin", currentUser.getObjectId());
	}
	
	public void setMarkerId(int id){
		put("markerId", id);
	}
	
	public void setHour(String hour){
		put("finishHour", hour);
	}
	
	public void setMinute(String minute){
		put("finishMinute", minute);
	}
	
	public void setActivePlayers(String active){
		put("activePlayers", Integer.parseInt(active));
	}
	
	public void setNeededPlayers(String needed){
		put("neededPlayers", Integer.parseInt(needed));
	}
	
	public void setActivity(String act){
		put("Activity", act);
	}
	
	public void addMember(ParseUser user){
		add("members", user.getUsername());
	}
}










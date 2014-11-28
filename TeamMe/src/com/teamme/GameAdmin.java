package com.teamme;

import org.json.JSONArray;

import android.util.Log;

import com.teamme.Networking.AsyncResponse;

public class GameAdmin implements AsyncResponse  {

	private String mUserId;
	public GameAdmin(){
		
	}
	@Override
	public void getResponse(String jsonResponseString) {
		// TODO Auto-generated method stub
		if (jsonResponseString.equals("0") || jsonResponseString.equals(null) || 
				jsonResponseString.equals("") || jsonResponseString.equals("[]")){
			Log.e("PROFILE RESPONSE STRING CAUGHT", jsonResponseString);
			return;
		}
		else{
			try{
				Log.e("PROFILE RESPONSE STRING", jsonResponseString);
				final JSONArray geodata = new JSONArray(jsonResponseString);

				final int n = geodata.length();
				
				for (int i = 0 ; i < n ; i++){
					int id = geodata.getJSONObject(i).getInt("userId");

					
					setUserId(id);
					}
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	
	private void setUserId(int id) {
		this.mUserId = ""+id;
		
	}
	public String getUserId(){
		return mUserId;
	}

}

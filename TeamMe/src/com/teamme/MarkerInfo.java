package com.teamme;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerInfo {

	private MarkerOptions mMarker;
	private JSONObject mObject;
	private String userId;
	private String finishHour; 
	private String finishMinute;
	private String activePlayers;
	private String neededPlayers; 
	private String customActivity;
	private String teamName;
	private Integer activityNum;


	public MarkerInfo(MarkerOptions markerOptions, JSONObject jsonObject) {
		setmMarker(markerOptions);
		setmObject(jsonObject);
		saveObjectFields();
	}
	private void saveObjectFields() {
		JSONObject jsonObject = getmObject();
		if (getmMarker() == null || jsonObject == null){
			setUserId("");
			setFinishHour("");
			setFinishMinute("");
			setActivePlayers("");
			setNeededPlayers("");
			setCustomActivity("");
			setTeamName("");
			setActivityNum(0);
			return;
		}


		try {
			setUserId(jsonObject.getString("userId"));
			setFinishHour(jsonObject.getString("finishHour"));
			setFinishMinute(jsonObject.getString("finishMinute"));
			setActivePlayers(jsonObject.getString("activePlayers"));
			setNeededPlayers(jsonObject.getString("neededPlayers"));
			setCustomActivity(jsonObject.getString("customActivity"));
			setTeamName(jsonObject.getString("teamName"));
			setActivityNum(jsonObject.getInt("activityNum"));
		} catch (JSONException e) {
			e.printStackTrace();
		}



	}
	public MarkerOptions getmMarker() {
		return mMarker;
	}
	public void setmMarker(MarkerOptions mMarker) {
		this.mMarker = mMarker;
	}
	public JSONObject getmObject() {
		return mObject;
	}
	public void setmObject(JSONObject mObject) {
		this.mObject = mObject;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFinishHour() {
		return finishHour;
	}
	public void setFinishHour(String finishHour) {
		this.finishHour = finishHour;
	}
	public String getFinishMinute() {
		return finishMinute;
	}
	public void setFinishMinute(String finishMinute) {
		this.finishMinute = finishMinute;
	}
	public String getActivePlayers() {
		return activePlayers;
	}
	public void setActivePlayers(String activePlayers) {
		this.activePlayers = activePlayers;
	}
	public String getNeededPlayers() {
		return neededPlayers;
	}
	public void setNeededPlayers(String neededPlayers) {
		this.neededPlayers = neededPlayers;
	}
	public String getCustomActivity() {
		return customActivity;
	}
	public void setCustomActivity(String customActivity) {
		this.customActivity = customActivity;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public Integer getActivityNum() {
		return activityNum;
	}
	public void setActivityNum(Integer activityNum) {
		this.activityNum = activityNum;
	}

}

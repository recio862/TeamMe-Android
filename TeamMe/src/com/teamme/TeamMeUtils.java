package com.teamme;

import java.util.Calendar;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class TeamMeUtils {

	
	protected static void resetFields(View dialogContent) {
		EditText edTxtTeamName = (EditText) dialogContent.findViewById(R.id.teamName);
		edTxtTeamName.setText(null);
		EditText edTxtCustomActivity = (EditText) dialogContent.findViewById(R.id.username5);
		edTxtCustomActivity.setText(null);
		Spinner spinnerActivity = (Spinner) dialogContent.findViewById(R.id.spinner1);
		spinnerActivity.setSelection(0);
		EditText edTxtPlayersActive = (EditText) dialogContent.findViewById(R.id.num_players);
		edTxtPlayersActive.setText(null);
		EditText edTxtPlayersNeeded = (EditText) dialogContent.findViewById(R.id.num_players2);
		edTxtPlayersNeeded.setText(null);
		TimePicker pickFinishTime = (TimePicker) dialogContent.findViewById(R.id.timePicker1);
		Calendar c = Calendar.getInstance();
		pickFinishTime.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		pickFinishTime.setCurrentMinute(c.get(Calendar.MINUTE));



	}

	
	
	public static Integer getActivityNumber(String activity) {
		int activityNum = 0;
		switch (activity){

		case "Soccer":
			activityNum = 1;
			break;
		case "Football":
			activityNum = 2;
			break;
		case "Disc Golf":
			activityNum = 3;
			break;
		case "Tennis":
			activityNum = 4;
			break;
		case "Biking":
			activityNum = 5;
			break;
		case "Bowling":
			activityNum = 6;
			break;
		case "Rock Climbing":
			activityNum = 7;
			break;
		case "Volleyball":
			activityNum = 8;
			break;
		}
		return activityNum;
		
	}
	
	public static BitmapDescriptor getIconFromActivityNum(int activityNum, boolean selected){

		BitmapDescriptor icon = (BitmapDescriptorFactory.fromResource(R.drawable.marker));
		switch (activityNum){
		case 0:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.customgame));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.customgameselected));
			break;
		case 1:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.soccer));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.soccerselected));
			break;
		case 2:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.football));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.footballselected));
			break;
		case 3:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.frisbee));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.frisbeeselected));
			break;
		case 4:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.tennis));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.tennisselected));
			break;
		case 5:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bike));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bikeselected));
			break;
		case 6:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bowling));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.bowlingselected));
			break;
		case 7:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.climbing));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.climbingselected));
			break;
		case 8:
			if (!selected)
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.volleyball));
			else
				icon =(BitmapDescriptorFactory.fromResource(R.drawable.volleyballselected));
			break;
		}

		return icon;



	}
}

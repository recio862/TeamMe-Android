package com.teamme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.model.Marker;

public class CreateGame {

	
	public static void newCreateGameDialog(Marker myMarker, Activity myActivity){
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage("testing")
		       .setTitle("Testing");
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	           }
	       });
	builder.setNegativeButton("nah", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	           }
	       });

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}

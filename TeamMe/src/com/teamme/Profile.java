package com.teamme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamme.Networking.AsyncResponse;

public class Profile extends Activity implements AsyncResponse {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	public Networking messagePasser;
	public Networking.GetRequest getPasser;
	public Networking.GetRequest profilePasser;
	public String usedIp;
	EditText username;
	EditText email;
	EditText phone; 
	
    // here we populate the user profile fields from data pulled from the server which relies
	//on the user having already signed in, to query the server for that email address.
	public void getResponse(String jsonResponseString){

		if (jsonResponseString.equals("0") || jsonResponseString.equals(null) || 
				jsonResponseString.equals("") || jsonResponseString.equals("[]")){
			Log.e("PROFILE RESPONSE STRING CAUGHT", jsonResponseString);
			return;
		}
		else{
			try{
				Log.e("PROFILE RESPONSE STRING", jsonResponseString);
				final JSONArray jsonProfile = new JSONArray(jsonResponseString);
				Toast.makeText(getApplicationContext(), "Loading user profile" , Toast.LENGTH_LONG).show();
				//final int n = geodata.length();
				
				username.setText(jsonProfile.getJSONObject(0).getString("userName"));
				phone.setText(jsonProfile.getJSONObject(0).getString("phone"));

				//for (int i = 0; i < n; i++) {
//					Log.d("LAT", "meow" + geodata.getJSONObject(i).getDouble("lat"));
//					Log.d("LONG", "meow" + geodata.getJSONObject(i).getDouble("lng"));
					//username, email, phone
					
//					mGameNumber = (geodata.getJSONObject(i).getInt("markerId"));
//					markerOptions = new MarkerOptions().position(new LatLng(geodata.getJSONObject(i).getDouble("lat"), geodata.getJSONObject(i).getDouble("lng")));
//					markerOptions.icon(getIconFromActivityNum(Integer.parseInt(geodata.getJSONObject(i).getString("activityNum")), false));
//					markerOptions.title(""+i);
//					googleMap.addMarker(markerOptions);
//					mapMarkers.put(""+i, new MarkerInfo(geodata.getJSONObject(i)));
				//}
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		username = (EditText) findViewById(R.id.edit_profile_name);
		email = (EditText) findViewById(R.id.edit_email);
		phone = (EditText) findViewById(R.id.edit_phone_number);
		Log.e("pref email", mPrefs.getString("email"," "));

		email.setText(mPrefs.getString("email"," "));
		
		messagePasser = new Networking(Profile.this);
		usedIp = messagePasser.amazonServerIp;
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//prof_name = (String) savedInstanceState.get("profile_name");
		
	/*	if(prof_name != null){
			TextView name = (TextView) findViewById(R.id.profile_name);
			name.setText(prof_name);
		}*/
		Button confirmChanges = (Button) findViewById(R.id.view_team_button);
		getPasser = messagePasser.new GetRequest();
		getPasser.responder = this;
		getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabUserProfile.php?email=" + mPrefs.getString("email","")); 
		//username, email and phone number should be set from information retrieved from server now
		
		confirmChanges.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				username = (EditText) findViewById(R.id.edit_profile_name);
				email = (EditText) findViewById(R.id.edit_email);
				phone = (EditText) findViewById(R.id.edit_phone_number);
				SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
				SharedPreferences.Editor ed = mPrefs.edit();
				ed.putString("email",email.getText().toString());
				ed.apply();

				
				profilePasser = messagePasser.new GetRequest();
				profilePasser.responder = Profile.this;
				profilePasser.execute("http://" + messagePasser.usedIp + 
						":80/android/project/updateProfile.php?email=" +
						email.getText().toString() + "&phone=" + phone.getText().toString() + "&username=" + username.getText().toString()); 

			}
		});
		
		profile = (ImageView) findViewById(R.id.profile_pic);
		profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1){
			File f = new File(Environment.getExternalStorageDirectory().toString());
			for(File temp: f.listFiles()){
				if(temp.getName().equals("temp.jpg")){
					f = temp;
					break;
				}
			}
			try{
				Bitmap bitmap;
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
				profile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 150, 150, false));
				
				String path = android.os.Environment.getExternalStorageDirectory() +File.separator + "Phoenix" + File.separator + "default";
				f.delete();
				OutputStream outFile = null;
				File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
				try{
					outFile = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 85, outFile);
					outFile.flush();
					outFile.close();
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(requestCode == 2){
			Uri selectedImage = data.getData();
			String[] filePath = {MediaStore.Images.Media.DATA};
			Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePath[0]);
			String picturePath = c.getString(columnIndex);
			c.close();
			Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
			Log.w("Path of image from gallery: ", picturePath + "");
			profile.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 150, 150, false));
		}
	}
	
	private void selectPicture(){
		final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if(options[item].equals("Take Photo"))
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				}
				else if(options[item].equals("Choose from Gallery"))
				{
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);
				}
				else if(options[item].equals("Cancel"))
					dialog.dismiss();
				
			}
		});
		builder.show();
	}

}

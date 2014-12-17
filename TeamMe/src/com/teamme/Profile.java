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
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.teamme.Networking.AsyncResponse;
import com.teamme.Networking.UpdateProfileParameters;

public class Profile extends Activity implements AsyncResponse {
	
	public String prof_name;
	public ImageView profile;
	public String jsonEncodedImage;
	public String profilePicPath = " ";
	private Toast profileToast;
	private Bitmap pic = null;
	public Networking messagePasser;
	public Networking.GetRequest getPasser;
	public Networking.UpdateProfileTask profilePasser;
	public String usedIp;
	public UpdateProfileParameters params;
	EditText username;
	EditText email;
	EditText phone; 
	EditText password; 
	//ParseUser user;
	boolean editedFields;
	private TextWatcher userwatcher;
	private TextWatcher emailwatcher;
	private PhoneNumberFormattingTextWatcher phonewatcher;
	private TextWatcher passwatcher;
	private Toast currentToast;
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
				
					if (currentToast != null)
						currentToast.cancel();
				currentToast = Toast.makeText(getApplicationContext(), "Loading user profile" , Toast.LENGTH_LONG);
				currentToast.show();
				//final int n = geodata.length();
				
				//need these for the instance where the user is logging into a different phone.
				username.setText(jsonProfile.getJSONObject(0).getString("userName"));
				email.setText(jsonProfile.getJSONObject(0).getString("email"));
				phone.setText(jsonProfile.getJSONObject(0).getString("phone"));
				jsonEncodedImage = jsonProfile.getJSONObject(0).getString("userImage");
				Log.e("JSONECNODEDIMAGE", jsonEncodedImage);

				if (! (null == jsonEncodedImage) && (jsonEncodedImage.equals(null))){
				  byte[] decodedString = Base64.decode(jsonEncodedImage, Base64.DEFAULT);
				  pic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				  profile.setImageBitmap(Bitmap.createScaledBitmap(pic, 150, 150, false));
				}
				//profile.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 150, 150, false));
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
				Log.e("exception in profile getResponse", "yah dude");
				throw new RuntimeException(e);
			}
		}
	}
	
	protected void initializeWatchers(){
		userwatcher = new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
//				user.setUsername(s.toString());
				editedFields = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		emailwatcher = new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				user.setEmail(s.toString());
				editedFields = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		phonewatcher = new PhoneNumberFormattingTextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
	//			user.put("phoneNumber", s.toString());
				editedFields = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		passwatcher = new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
		//		user.setPassword(s.toString());
				editedFields = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile);
/*		user = ParseUser.getCurrentUser();
		try {
			user.fetch();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		initializeWatchers();
		
//		Toast.makeText(getApplicationContext(), user.getUsername().toString(), Toast.LENGTH_LONG).show();

		SharedPreferences mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);  
		username = (EditText) findViewById(R.id.edit_profile_name);
		email = (EditText) findViewById(R.id.edit_email);
		phone = (EditText) findViewById(R.id.edit_phone_number);
		password = (EditText) findViewById(R.id.edit_new_password);		
		
		Log.e("right herety here", "HERE");

		/*username.setText(user.getUsername().toString());
		email.setText(user.getEmail().toString());
		phone.setText(user.get("phoneNumber").toString());*/
		password.setText("");

		
//		messagePasser = new Networking(Profile.this);
//		usedIp = messagePasser.amazonServerIp;
		editedFields = false;
		username.addTextChangedListener(userwatcher);
		email.addTextChangedListener(emailwatcher);
		phone.addTextChangedListener(phonewatcher);
		password.addTextChangedListener(passwatcher);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//prof_name = (String) savedInstanceState.get("profile_name");
		
	/*	if(prof_name != null){
			TextView name = (TextView) findViewById(R.id.profile_name);
			name.setText(prof_name);
		}*/
		Button confirmChanges = (Button) findViewById(R.id.view_team_button);
//		getPasser = messagePasser.new GetRequest();
//		getPasser.responder = this;
//		getPasser.execute("http://" + messagePasser.usedIp + ":80/android/project/grabUserProfile.php?email=" + user.getEmail()); 
		//username, email and phone number should be set from information retrieved from server now
		//Log.e("passed get", user.getEmail());

		confirmChanges.setOnClickListener(new OnClickListener(){
			

			@Override
			public void onClick(View v) {
				if(editedFields){
					
					if (currentToast != null)
						currentToast.cancel();
					currentToast = Toast.makeText(getApplicationContext(), "Updating Profile", Toast.LENGTH_SHORT);
					currentToast.show();
		/*			user.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (currentToast != null)
								currentToast.cancel();
							currentToast = Toast.makeText(getApplicationContext(), "Profile Updated Successfully!", Toast.LENGTH_LONG);
							currentToast.show();
						}
					});*/
					editedFields = false;
				}
				else
				{
					if (currentToast != null)
						currentToast.cancel();
					currentToast = Toast.makeText(getApplicationContext(), "No changes to save", Toast.LENGTH_LONG);
					currentToast.show();
					
				}
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
				profilePicPath = file.getAbsolutePath();
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
			profilePicPath = picturePath;
			c.close();
			Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
			Log.w("Path of image from gallery: ", picturePath + "");
			profile.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 150, 150, false));
		}
	}
	
	
	@Override 
	public void onPause(){
		super.onPause();
		if (currentToast != null)
			currentToast.cancel();
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
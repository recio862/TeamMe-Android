package com.teamme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Activity {
	
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile);
		//prof_name = (String) savedInstanceState.get("profile_name");
		
	/*	if(prof_name != null){
			TextView name = (TextView) findViewById(R.id.profile_name);
			name.setText(prof_name);
		}*/
		
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

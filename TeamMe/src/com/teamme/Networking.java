package com.teamme;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Networking {
	public final String amazonServerIp = "54.148.102.210";
	public final String privateServerIp = "72.182.49.84";
	public final String usedIp = amazonServerIp;
	public interface AsyncResponse {
		void getResponse(String output);
	}
	protected Context mContext;

	public Networking(Context context) {
		mContext = context;
	}

	//from the Android cookbook by Wei Meng lee, GET

	public static InputStream OpenHttpGETConnection(String url) {
		InputStream inputStream = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
			inputStream = httpResponse.getEntity().getContent();
		} catch (Exception e){
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return inputStream;
	}
	public class GetRequest extends AsyncTask<String, Void, String> {
		public AsyncResponse responder = null;
		protected String doInBackground(String... urls){
			return retrieveResponse(urls[0]);			
		}

		private String retrieveResponse(String URL) {
			int BUFFER_SIZE = 100000;
			InputStream in = null;
			try {
				in = OpenHttpGETConnection(URL);
			} catch(Exception e) {
				Log.e("Download Markers", e.getLocalizedMessage());
				return " ";
			}
			Log.e("in get", "hi there");

			InputStreamReader isr = new InputStreamReader(in);
			int charRead;
			String str = "";
			char[] inputBuffer = new char[BUFFER_SIZE];
			try {
				while ((charRead = isr.read(inputBuffer)) > 0) {
					///convert chars to string
					String readString = String.copyValueOf(inputBuffer, 0, charRead);
					str += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				in.close();
			} catch(IOException e) {
				Log.d("DownloadText", e.getLocalizedMessage());
				return "";
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result){
			Log.d("ProfileTask", result);
			Log.e("GET RESPONSE: ", result);
			responder.getResponse(result);
		}
	}

	
	//inspired by http://stackoverflow.com/questions/12069669/how-can-you-pass-multiple-primitive-parameters-to-asynctask
	public static class CoordParameters {
		String url;
		LatLng position;
		String userId;
		String finishHour;
		String finishMinute;
		String activePlayers;
		String neededPlayers;
		String customActivity;
		String teamName;
		Integer activityNum;

		CoordParameters(String url1, LatLng pos, String userId1, String finishHour1, String finishMinute1, String activePlayers1,
				String neededPlayers1, String customActivity1, String teamName1, Integer activityNum1){
			this.position = pos;
			this.url = url1;
			this.userId = userId1;
			this.finishHour = finishHour1;
			this.finishMinute = finishMinute1;
			this.activePlayers = activePlayers1;
			this.neededPlayers = neededPlayers1;
			this.customActivity = customActivity1;
			this.teamName = teamName1;
			this.activityNum = activityNum1;
		}
	}


	//from the Android cookbook by Wei Meng lee, POST
	public InputStream OpenHttpPOSTConnection(String url, LatLng pos, String userId, String finishHour, String finishMinute, 
			String activePlayers, String neededPlayers, String customActivity, String teamName, Integer activityNum) {
		InputStream inputStream = null;
		try{
			//HttpParams httpParameters = new BasicHttpParams();
			//HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			//HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			//httpPost.setParams(httpParameters);

			httpPost.addHeader("Host",usedIp);
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");	
			List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
			nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(pos.latitude)));
			nameValuePairs.add(new BasicNameValuePair("lng", String.valueOf(pos.longitude)));
			nameValuePairs.add(new BasicNameValuePair("userId", userId));
			nameValuePairs.add(new BasicNameValuePair("finishHour", finishHour));
			nameValuePairs.add(new BasicNameValuePair("finishMinute", finishMinute));
			nameValuePairs.add(new BasicNameValuePair("activePlayers", activePlayers));
			nameValuePairs.add(new BasicNameValuePair("neededPlayers", neededPlayers));
			nameValuePairs.add(new BasicNameValuePair("customActivity", customActivity));
			nameValuePairs.add(new BasicNameValuePair("teamName", teamName));
			nameValuePairs.add(new BasicNameValuePair("activityNum", String.valueOf(activityNum)));


			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
		} catch(Exception e) {
			Log.d("OpenHttpPOSTConnection", e.getLocalizedMessage());
		}
		return inputStream;
	}

	public class SendCoordsTask extends AsyncTask<CoordParameters, Void, String>{
		protected String doInBackground(CoordParameters... params){

			String url = params[0].url;
			LatLng point = params[0].position;
			String userId = params[0].userId;
			String finishHour = params[0].finishHour;
			String finishMinute = params[0].finishMinute;
			String activePlayers = params[0].activePlayers;
			String neededPlayers = params[0].neededPlayers;
			String customActivity = params[0].customActivity;
			String teamName = params[0].teamName;
			Integer activityNum = params[0].activityNum;
			return sendCoords(url,point,userId, finishHour, finishMinute, activePlayers,
					neededPlayers, customActivity, teamName, activityNum);
		}

		private String sendCoords(String url, LatLng pos, String userId, String finishHour, String finishMinute, 
				String activePlayers, String neededPlayers, String customActivity, String teamName, Integer activityNum) {
			int BUFFER_SIZE = 2000;
			InputStream in = null;
			try {
				in = OpenHttpPOSTConnection(url, pos, userId, finishHour, finishMinute, activePlayers,
						neededPlayers, customActivity, teamName, activityNum);
				if (in == null){
					Log.d("IN", "error null returned by post request");
					//throw NoHttpResponseException;
					return "";
				}				

			} catch(Exception e) {
				Log.d("Networking", e.getLocalizedMessage());
				return "";
			}
			//Log.d("IN", "meow1");

			InputStreamReader isr = new InputStreamReader(in);
			int charRead;
			String str = " ";
			char [] inputBuffer = new char[BUFFER_SIZE];
			try {
				while ((charRead = isr.read(inputBuffer)) > 0) {
					//convert chars to a String
					String readString = String.copyValueOf(inputBuffer, 0, charRead);
					str += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				in.close();
			} catch (IOException e) {
				Log.d("SendCoords", e.getLocalizedMessage());
				return "";
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_LONG).show();
			Log.d("SendCoordsTask", result);
		}
	}
	
	//used in deleting stuff
	public static class GameDeletionParameters {
		Integer gameNum;
		String url;
		GameDeletionParameters(String url1, Integer gameNum1){
			this.url = url1;
			this.gameNum = gameNum1;
		}
	}

	public InputStream OpenHttpPOSTConnectionForDeletion(String url, Integer activityNum) {
		InputStream inputStream = null;
		try{
			//HttpParams httpParameters = new BasicHttpParams();
			//HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			//HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			//httpPost.setParams(httpParameters);

			httpPost.addHeader("Host", usedIp);
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");	
			List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("activityNum", String.valueOf(activityNum)));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
		} catch(Exception e) {
			Log.d("OpenHttpPOSTConnectionForDeletion", e.getLocalizedMessage());
		}
		return inputStream;
	}

	public class DeleteMarkersTask extends AsyncTask<GameDeletionParameters, Void, String>{
		protected String doInBackground(GameDeletionParameters... params){
			String url = params[0].url;
			Integer gameNum = params[0].gameNum;

			return deleteCoords(url, gameNum);
		}

		private String deleteCoords(String url, Integer gameNum) {
			int BUFFER_SIZE = 2000;
			InputStream in = null;
			try {
				in = OpenHttpPOSTConnectionForDeletion(url, gameNum);
				if (in == null){
					Log.d("IN", "error null returned by post request");
					//throw NoHttpResponseException;
					return "";
				}				

			} catch(Exception e) {
				Log.d("Networking", e.getLocalizedMessage());
				return "";
			}
			//Log.d("IN", "meow1");

			InputStreamReader isr = new InputStreamReader(in);
			int charRead;
			String str = " ";
			char [] inputBuffer = new char[BUFFER_SIZE];
			try {
				while ((charRead = isr.read(inputBuffer)) > 0) {
					//convert chars to a String
					String readString = String.copyValueOf(inputBuffer, 0, charRead);
					str += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				in.close();
			} catch (IOException e) {
				Log.d("DeleteCoords", e.getLocalizedMessage());
				return "";
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_LONG).show();
			Log.d("DeleteCoordsTask", result);
		}
	}
	
	
	//used in updating the profile
	public static class UpdateProfileParameters {
		String url;
		String username;
		String email;
		String phone;
		String password;
		String image;
		String oldemail;
		//image;
		UpdateProfileParameters(String url1,String username, String email, String oldemail, String phone,String password, String image){
			this.url = url1;
			this.username = username;
			this.email = email;
			this.phone = phone;
			this.password = password;
			this.image=image;
			this.oldemail=oldemail;
		}
	}

	
	
	public InputStream OpenHttpPOSTConnectionForUpdateProfile(String url,String username,String email,String oldemail,
			String phone,String password, String image) {
		 InputStream inputStream = null;
		 HttpClient httpClient = new DefaultHttpClient();
		    HttpContext localContext = new BasicHttpContext();
		    HttpPost httpPost = new HttpPost(url);
		    
			List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("oldemail", oldemail));
			nameValuePairs.add(new BasicNameValuePair("phone", phone));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("image", image));
			
		    try {
		    	//HttpEntity entity = MultipartEntityBuilder.create();
		    			
		        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		    		
		        for(int index=0; index < nameValuePairs.size(); index++) {
		            if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
		                // If the key equals to "image", we use FileBody to transfer the data
		            	//entity.addBinaryBody("image", new File (nameValuePairs.get(index).getValue())), 
		            	 // ContentType.create("image/jpeg"),file.getName());
		                entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
		            } else {
		                // Normal string data
		                entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
		            }
		        }
		        httpPost.setEntity(entity);
		        HttpResponse response = httpClient.execute(httpPost, localContext);
				inputStream = response.getEntity().getContent();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			return inputStream;

//			
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//
//			httpPost.addHeader("Host", usedIp);
//			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");	
//			List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//			nameValuePairs.add(new BasicNameValuePair("activityNum", String.valueOf(activityNum)));
//
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//
//			HttpResponse httpResponse = httpclient.execute(httpPost);
//			inputStream = httpResponse.getEntity().getContent();
//		} catch(Exception e) {
//			Log.d("OpenHttpPOSTConnectionForUpdateProfile", e.getLocalizedMessage());
//		}
//		return inputStream;
	}

	public class UpdateProfileTask extends AsyncTask<UpdateProfileParameters, Void, String>{
		protected String doInBackground(UpdateProfileParameters... params){
			String url = params[0].url;
			String username = params[0].username;
			String email = params[0].email;
			String oldemail = params[0].oldemail;
			String phone = params[0].phone;
			String password = params[0].password;
			String image = params[0].image;
			return UpdateProfile(url, username, email, oldemail, phone, password, image);
		}

		private String UpdateProfile(String url,String username,String email,String oldemail,String phone,String password, String image) {
			int BUFFER_SIZE = 2000;
			InputStream in = null;
			try {
				in = OpenHttpPOSTConnectionForUpdateProfile(url, username, email, oldemail, phone, password, image);
				if (in == null){
					Log.d("IN", "error null returned by post request");
					//throw NoHttpResponseException;
					return "";
				}				

			} catch(Exception e) {
				Log.d("Networking", e.getLocalizedMessage());
				return "";
			}
			//Log.d("IN", "meow1");

			InputStreamReader isr = new InputStreamReader(in);
			int charRead;
			String str = " ";
			char [] inputBuffer = new char[BUFFER_SIZE];
			try {
				while ((charRead = isr.read(inputBuffer)) > 0) {
					//convert chars to a String
					String readString = String.copyValueOf(inputBuffer, 0, charRead);
					str += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				in.close();
			} catch (IOException e) {
				Log.d("UpdateProfile", e.getLocalizedMessage());
				return "";
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_LONG).show();
			Log.d("UpdateProfileTask", result);
		}
	}
}

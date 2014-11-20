package com.teamme;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Networking {
	public final String amazonServerIp = "54.148.102.210";
	public final String privateServerIp = "72.182.49.84";
	public final String usedIp = privateServerIp;
	public interface AsyncResponse {
		void gotMarkers(String output);
	}
	protected Context mContext;

	public Networking(Context context) {
		mContext = context;
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
	public class DownloadMarkersTask extends AsyncTask<String, Void, String> {
		public AsyncResponse responder = null;
		protected String doInBackground(String... urls){
			return DownloadMarkers(urls[0]);			
		}

		private String DownloadMarkers(String URL) {
			int BUFFER_SIZE = 2000;
			InputStream in = null;
			try {
				in = OpenHttpGETConnection(URL);
			} catch(Exception e) {
				Log.d("Download Markers", e.getLocalizedMessage());
				return " ";
			}

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
			Log.d("DownloadMarkersTask", result);
			responder.gotMarkers(result);
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
}

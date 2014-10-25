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
			CoordParameters(String url1, LatLng pos, String userId1){
				this.position = pos;
				this.url = url1;
				this.userId = userId1;
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
					//Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_LONG).show();
					//new AlertDialog.Builder(mContext.getApplicationContext())
				    //.setTitle("Delete entry")
				    //.setMessage("Are you sure you want to delete this entry?")
				    //.show();
					Log.d("DownloadMarkersTask", result);
					responder.gotMarkers(result);
				}
			}
			
		//from the Android cookbook by Wei Meng lee, POST
			public InputStream OpenHttpPOSTConnection(String url, LatLng point, String userId) {
				InputStream inputStream = null;
				try{
					//HttpParams httpParameters = new BasicHttpParams();
					//HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
					//HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000);
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					//httpPost.setParams(httpParameters);
					httpPost.addHeader("Host", "72.182.49.84");
					httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");	
					List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(point.latitude)));
					nameValuePairs.add(new BasicNameValuePair("lng",String.valueOf(point.longitude)));
					nameValuePairs.add(new BasicNameValuePair("user_id",userId));
					
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
					 //HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), false);
					 //HttpProtocolParams.setVersion(httpclient.getParams(), HttpVersion.HTTP_1_1);
					 //HttpConnectionParams.setStaleCheckingEnabled(httpclient.getParams(), true);
					/*
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
					inputStream = httpResponse.getEntity().getContent();*/

					HttpResponse httpResponse = httpclient.execute(httpPost);
					inputStream = httpResponse.getEntity().getContent();
					//HttpEntity entity = httpResponse.getEntity();
			        //String logResponse = EntityUtils.toString(entity);
			        //final String TAG = "server response";
			        //Log.v(TAG,logResponse);
				} catch(Exception e) {
					Log.d("OpenHttpPOSTConnection", e.getLocalizedMessage());
				}
				return inputStream;
			}

			public class SendCoordsTask extends AsyncTask<CoordParameters, Void, String>{
				protected String doInBackground(CoordParameters... params){
					String url1 = params[0].url;
					Log.d("URL", url1);
					LatLng point = params[0].position;
					Log.d("Position", point.toString());
					String user1 = params[0].userId;
					Log.d("User", user1);
					return sendCoords(url1,point,user1);
				}
				
				private String sendCoords(String URL, LatLng point, String userId) {
					int BUFFER_SIZE = 2000;
					InputStream in = null;
					try {
						in = OpenHttpPOSTConnection(URL, point, userId);
						if (in == null){
							Log.d("IN", "meow1");
							//throw NoHttpResponseException;
							return "";
						}
						//Scanner scanner = new Scanner(in).useDelimiter("\\A");
						//String stringinput = scanner.hasNext() ? scanner.next() : null;
						//scanner.close();
						//Log.d("IN", stringinput);

					} catch(Exception e) {
						Log.d("Networking", e.getLocalizedMessage());
						//Log.d("IN", "meow");

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
					Toast.makeText(mContext.getApplicationContext(), result, Toast.LENGTH_LONG).show();
					Log.d("SendCoordsTask", result);
				}
			}
	}

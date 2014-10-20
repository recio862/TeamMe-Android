package com.teamme;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {
    // Google Map
    public GoogleMap googleMap;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
        Log.e("loading map. . . .", "loading map. . . ");
        // Loading map
        initilizeMap();

    } catch (Exception e) {
        Log.e("catch. . .", "catch. . .");
        e.printStackTrace();
    }
    /*
     * MapFunctionality mf = new MapFunctionality(); mf.currentLoc();
     * mf.addMarker();
     */
}

/**
 * function to load map. If map is not created it will create it for you
 * */
private void initilizeMap() {
    // MapFunctionality mf = new MapFunctionality();
    Log.e("initializing map. . . ", "initializing map. . ");
    if (googleMap == null) {
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();

        // check if map is created successfully or not
        if (googleMap != null) {
        	googleMap.setOnMapClickListener(new OnMapClickListener() {
        		System.out.println("test");
        		System.out.println("test2");
        	    @Override
        	    public void onMapClick(LatLng point) {
        	          googleMap.addMarker(new MarkerOptions().position(point));
        	          // you can get latitude and longitude also from 'point'
        	          // and using Geocoder, the address
        	          CoordParameters params = new CoordParameters("http://72.182.49.84:80/android/project/updateMarkers.php", point, "0");
        	          new SendCoordsTask().execute(params);
        	          //new DownloadMarkersTask().execute("http://72.182.49.84:80/android/project/updateMarkers.php?id=1");
        	         }
        	});
        }
        // mf.currentLoc();
        // mf.addMarker();
    }
}

//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.activity_main, menu);
//    return true;
//}

@Override
protected void onResume() {
    super.onResume();
    initilizeMap();
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
	private class DownloadMarkersTask extends AsyncTask<String, Void, String> {
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
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
			Log.d("DownloadMarkersTask", result);
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

	private class SendCoordsTask extends AsyncTask<CoordParameters, Void, String>{
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
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
			Log.d("SendCoordsTask", result);
		}
	}
}
}
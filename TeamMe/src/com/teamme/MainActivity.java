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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	// Google Map
	public GoogleMap googleMap;
	public Button createButton;
	public Button viewButton;
	public Marker myMarker;
	public LatLng myLocation;
	public AlertDialog dialog;
	private boolean createEnabled = false;
	private boolean viewEnabled = false;

	//Quickfix to move maps zoom buttons to the top left so it doesnt interfere with the
	//layout of the buttons
	//http://stackoverflow.com/questions/14071230/android-maps-library-v2-zoom-controls-custom-position
	public void fixZoom(){
		// Find map fragment
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

		// Find ZoomControl view
		View zoomControls = mapFragment.getView().findViewById(0x1);

		if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			// ZoomControl is inside of RelativeLayout
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

			// Align it to - parent top|left
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

			// Update margins, set to 10dp
			final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
					getResources().getDisplayMetrics());
			params.setMargins(margin, margin, margin, margin);
		}
	}

	protected Dialog onCreateDialog(int id) {

		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = getLayoutInflater();
		// 2. Chain together various setter methods to set the dialog characteristics

		builder.setView(inflater.inflate(R.layout.create_dialog, null)).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
			}
		})
		.setNegativeButton("Create Game", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast gameCreated = Toast.makeText(getApplicationContext(), "Your Game Was Created!", Toast.LENGTH_SHORT);
				gameCreated.setGravity(Gravity.CENTER, 0, 0);
				gameCreated.show();
				
			}
		});

		// 3. Get the AlertDialog from create()
		dialog = builder.create();


		return dialog;
	}





	public void clickedCreate(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap a location first!");
		if (!createEnabled)
			helpPopup.show(view);
		else 
		{


			showDialog(0);
			LayoutParams params = dialog.getWindow().getAttributes();
			params.width = LayoutParams.FILL_PARENT;
			dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		}
	}

	public void clickedView(View view) {
		HelpPopup helpPopup = new HelpPopup(MainActivity.this,"Tap an existing activity first!");
		if (!viewEnabled)
			helpPopup.show(view);

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fixZoom();
		createButton = (Button)findViewById(R.id.Button01);

		viewButton = (Button)findViewById(R.id.Button02);
		try {
			Log.e("loading map. . . .", "loading map. . . ");
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			Log.e("catch. . .", "catch. . .");
			e.printStackTrace();
		}

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


				googleMap.setMyLocationEnabled(true);
				//Location location = googleMap.getMyLocation();
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				String provider = locationManager.getBestProvider(criteria, false);
				Location location = locationManager.getLastKnownLocation(provider);
				if (location != null) {
					myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					myLocation = new LatLng(location.getLatitude(),
							location.getLongitude());
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
							14));
				}
				else 
				{
					LatLng coordinate = new LatLng(30.2861, -97.7394);
					CameraUpdate utAustin = CameraUpdateFactory.newLatLngZoom(coordinate, 14);
					googleMap.animateCamera(utAustin);
				}


				googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){

					@Override
					public boolean onMarkerClick(Marker arg0) {

						return true;
					}

				});
				googleMap.getUiSettings().setCompassEnabled(false);

				googleMap.setOnMapClickListener(new OnMapClickListener(){

					@Override
					public void onMapClick(LatLng point) {
						// TODO Auto-generated method stub
						if (myMarker != null)
							myMarker.remove();
						// create marker
						MarkerOptions markerOptions = new MarkerOptions().position(point).title("Create Game Here!");

						// Changing marker icon
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

						createButton.setAlpha((float) 0.70);

						createEnabled = true;

						myMarker = googleMap.addMarker(markerOptions); 



					}

				});

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

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

package com.teamme;
<<<<<<< HEAD
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
=======

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
<<<<<<< HEAD
=======

>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
public class ViewTeam extends Activity {
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	private TextView teamName;
	private ListView members;
	private ArrayAdapter<String> listAdapter;
	String marker;
	ArrayList<String> list;
<<<<<<< HEAD
=======
	
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_team);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
<<<<<<< HEAD
=======
		
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
		teamName = (TextView) findViewById(R.id.team_name);
		members = (ListView) findViewById(R.id.member);
		if(savedInstanceState == null){
			Bundle extras = getIntent().getExtras();
			if(extras != null)
				marker = extras.getString("team_id");
			else
				marker = "You haven't joined any teams.";
<<<<<<< HEAD
			// Toast.makeText(getApplicationContext(), marker, Toast.LENGTH_LONG).show();
=======
//			Toast.makeText(getApplicationContext(), marker, Toast.LENGTH_LONG).show();
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
		}
		else{
			marker = savedInstanceState.getString("team_id");
		}
		if(marker.compareTo("You haven't joined any teams.") != 0){
			ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
			query.whereEqualTo("markerId", Integer.parseInt(marker));
			try {
				query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Couldn't find a game", Toast.LENGTH_LONG).show();
			}
<<<<<<< HEAD
=======
			
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
			ParseObject game = null;
			try {
				game = query.getFirst();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
<<<<<<< HEAD
=======
			
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
			if(game == null)
				return;
			String name = "Team " + game.getString("teamName");
			name = name + ":";
			teamName.setText(name);
<<<<<<< HEAD
=======
			
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
			list = new ArrayList<String>();
			JSONArray jArray = game.getJSONArray("members");
			if(jArray != null){
				for (int i = 0; i < jArray.length(); i++){
					try {
						list.add((String) jArray.get(i));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
<<<<<<< HEAD
			// for(int i = 0; i < list.size(); i++)
			// Toast.makeText(getApplicationContext(), list.get(i), Toast.LENGTH_LONG).show();
=======
			
//			for(int i = 0; i < list.size(); i++)
//				Toast.makeText(getApplicationContext(), list.get(i), Toast.LENGTH_LONG).show();
>>>>>>> 20e10363b43e15d005d13e5e52f3072e375cb0be
			listAdapter = new ArrayAdapter<String>(this, R.layout.profile_list, list);
			members.setAdapter(listAdapter);
			members.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View v, int pos, long id){
					Intent i = new Intent(getApplicationContext(), ViewProfile.class);
					i.putExtra("username", list.get(pos));
				}
			});
		}
		else
			teamName.setText(marker);
	}
}
package com.teamme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ViewTeam extends Activity {
	public String prof_name;
	public ImageView profile;
	private Bitmap pic = null;
	private TextView teamName;
	private ListView members;
	private ArrayAdapter<String> listAdapter;
	String marker;
	List<String> list;
	private Toast currentToast;
	private SharedPreferences mPrefs;
	private Button savedteambutton;
	public void saveTeam(View view){
		if (currentToast != null)
			currentToast.cancel();
		SharedPreferences.Editor ed = mPrefs.edit();
		currentToast = Toast.makeText(getApplicationContext(), "Team Saved!", Toast.LENGTH_LONG);
		String s = mPrefs.getString("savedteams", "");
		boolean alreadySaved = false;
		if (s.equals(""))
			ed.putString("savedteams", teamName.getText().toString().substring(0, teamName.getText().toString().length()-1));
		else if (!s.contains(teamName.getText().toString().substring(0, teamName.getText().toString().length()-1)))
			ed.putString("savedteams", s+","+teamName.getText().toString().substring(0, teamName.getText().toString().length()-1));
		else 
			alreadySaved = true;
			
		String members = "";	
		for (int i= 0 ; i < list.size(); i++){
			members += list.get(i);
			members+=",";
		}
		if (alreadySaved == true)
			currentToast = Toast.makeText(getApplicationContext(), "Team Already Saved!", Toast.LENGTH_LONG);
		else
			ed.putString(teamName.getText().toString().substring(0, teamName.getText().toString().length()-1), members);
		ed.apply();
		currentToast.show();
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_team);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		teamName = (TextView) findViewById(R.id.team_name);
		members = (ListView) findViewById(R.id.member);
		savedteambutton = (Button) findViewById(R.id.save_team_button);
		if(savedInstanceState == null){
			Bundle extras = getIntent().getExtras();
			if (extras != null){
				
				if (extras.getBoolean("savedteam") == true){
					
					
					savedteambutton.setVisibility(View.GONE);
					String savedteamname = extras.getString("savedteamname");
					teamName.setText(savedteamname);
					String member = mPrefs.getString(savedteamname, "");
					if (member.equals(""))
						return;
					String[] allMembers = member.split(",");
					list = new ArrayList<String>();
					for (int i = 0 ; i < allMembers.length; i++){
						list.add(allMembers[i]);
					}
					Set<String> set = new HashSet<String>(list);

					String[] arraySet = set.toArray(new String[set.size()]);
					list = Arrays.asList(arraySet);  
					listAdapter = new ArrayAdapter<String>(this, R.layout.profile_list, list);
					members.setAdapter(listAdapter);
					members.setOnItemClickListener(new OnItemClickListener(){
						public void onItemClick(AdapterView<?> parent, View v, int pos, long id){
							Intent i = new Intent(getApplicationContext(), ViewProfile.class);
							i.putExtra("username", list.get(pos));
						}
					});
					return;
					
				}
				
			}
			if(extras != null)
				marker = extras.getString("team_id");
			else
				marker = "You haven't joined any teams.";

			// Toast.makeText(getApplicationContext(), marker, Toast.LENGTH_LONG).show();

//			Toast.makeText(getApplicationContext(), marker, Toast.LENGTH_LONG).show();

		}
		else if (savedInstanceState.getBoolean("savedteam")==(true))
		{
			savedteambutton.setVisibility(View.GONE);
			String savedteamname = savedInstanceState.getString("savedteamname");
			String member = mPrefs.getString(savedteamname, "");
			if (member.equals(""))
				return;
			String[] allMembers = member.split(",");
			list = new ArrayList<String>();
			for (int i = 0 ; i < allMembers.length; i++){
				list.add(allMembers[i]);
			}
			Set<String> set = new HashSet<String>(list);

			String[] arraySet = set.toArray(new String[set.size()]);
			list = Arrays.asList(arraySet);  
			listAdapter = new ArrayAdapter<String>(this, R.layout.profile_list, list);
			members.setAdapter(listAdapter);
			members.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View v, int pos, long id){
					Intent i = new Intent(getApplicationContext(), ViewProfile.class);
					i.putExtra("username", list.get(pos));
				}
			});
			return;
		}
		else{
			marker = savedInstanceState.getString("team_id");
		}
		if(marker.compareTo("You haven't joined any teams.") != 0){
/*			ParseQuery<ParseObject> query = ParseQuery.getQuery("game");
			query.whereEqualTo("markerId", Integer.parseInt(marker));
			try {
				query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (currentToast != null)
					currentToast.cancel();
				currentToast = Toast.makeText(getApplicationContext(), "Couldn't find a game", Toast.LENGTH_LONG);
				currentToast.show();
			}

			ParseObject game = null;
			try {
				game = query.getFirst();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(game == null)
				return;
			String name = "Team " + game.getString("teamName");
			name = name + ":";
			teamName.setText(name);

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
			}*/
			Set<String> set = new HashSet<String>(list);

			String[] arraySet = set.toArray(new String[set.size()]);
			// for(int i = 0; i < list.size(); i++)
			// Toast.makeText(getApplicationContext(), list.get(i), Toast.LENGTH_LONG).show();

			
//			for(int i = 0; i < list.size(); i++)
//				Toast.makeText(getApplicationContext(), list.get(i), Toast.LENGTH_LONG).show();
			list = Arrays.asList(arraySet);  
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
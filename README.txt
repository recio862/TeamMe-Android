AndroidDevProject
=================
TEAM MEMBERS:
Brian Zachary Abel
bza57
Renato Recio 
rjr862


HOW TO USE THE APP:
1. Click on the app icon (If you don't have internet access, the app will crash)
2. A Map with a number of markers will load; these are other people's events.
Eventually you'll be able to click on them to get the event information, but not today, Jack,
today you'll just have to show up and see what the event is for yourself. It's a surprise.
These are actual markers that other people have placed though, and they're active.
3. If you want to create your own event, click on a map location. A marker will appear. 
Then click the create button, and fill out the form according to your whims, or don't bother,
and click the create game button inside the create game dialog. Either way it will work, but your
game might be deleted by the server before you want it to be if you don't specify a time range.
4. Bam, your marker is on the map.

FEATURES/USE CASES COMPLETED:
II. Map of activities screen
V. Create a Game screen
VI. Action Bar Menu
XI. Kind've a settings screen (we have sound)
XII. About

FEATURES/USE CASES NOT COMPLETED:
Profile Page

I.Login Screen
III. Map Filter
IV. View Games Screen
VII. My Profile 
VIII. My Teams
IX. Individual Team screen
X. Public Profile Page

ADDED FEATURES THAT WEREN'T IN PROTOTYPE:
nothing.

CLASSES WE COPIED WITH CODE SOURCES:
//inspired by http://stackoverflow.com/questions/12069669/how-can-you-pass-multiple-primitive-parameters-to-asynctask
		public static class CoordParameters {
			CoordParameters(String url1, LatLng pos, String userId1, String finishHour1, String finishMinute1, String activePlayers1,
					String neededPlayers1, String customActivity1, String teamName1, Integer activityNum1){

			}
		}
//from the Android cookbook by Wei Meng lee with some modifications, GET
public static InputStream OpenHttpGETConnection(String url) {

			}
			public class DownloadMarkersTask extends AsyncTask<String, Void, String> {
	
				}
				
				private String DownloadMarkers(String URL) {
						}
					return str;
				}
				
				@Override
				protected void onPostExecute(String result){
				}
			}
//from the Android cookbook by Wei Meng lee with some modifications, POST
			public InputStream OpenHttpPOSTConnection(String url, LatLng pos, String userId, String finishHour, String finishMinute, 
					String activePlayers, String neededPlayers, String customActivity, String teamName, Integer activityNum) {

			}

			public class SendCoordsTask extends AsyncTask<CoordParameters, Void, String>{
				protected String doInBackground(CoordParameters... params){

				}
				

				private String sendCoords(String url, LatLng pos, String userId, String finishHour, String finishMinute, 
						String activePlayers, String neededPlayers, String customActivity, String teamName, Integer activityNum) {

				}
				
				@Override
				protected void onPostExecute(String result) {

				}
			}



CODE WE GENERATED ALMOST ENTIRELY FROM OUR GENIUS BRAINS (and maybe some reading):
    public void gotMarkers(String jsonDownloadedMarkersString){
	}
	public class Networking {
	public interface AsyncResponse {
	    void gotMarkers(String output);
	}
	protected Context mContext;

	public Networking(Context context) {
		mContext = context;
	}

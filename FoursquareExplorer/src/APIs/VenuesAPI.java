package APIs;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import Constants.HostData;
import Listeners.OnTaskCompleted;
import Tokens.TokenStore;
import VenueObjects.Location;
import VenueObjects.Photo;
import VenueObjects.Venue;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class VenuesAPI extends AsyncTask<String, Void, ArrayList<Venue>> 
{
	private OnTaskCompleted listener;
	private ProgressDialog dialogProgress;
	ArrayList<Venue> allVenues = new ArrayList<Venue>();

    public VenuesAPI(OnTaskCompleted listener,ProgressDialog dialog)
    {
        this.listener=listener;
        dialogProgress = dialog;
    }
    
    protected void onPreExecute() 
    {
    	dialogProgress.setMessage("Loading...");

    	dialogProgress.setCancelable(false);

    	dialogProgress.show();
    };

	HttpResponse response;
	@Override
	protected ArrayList<Venue> doInBackground(String... arg0) 
	{
		String result="";
		try
		{
			String url = HostData.venuesURL+"ll="+arg0[0]+","+arg0[1]+"&oauth_token="+TokenStore.get().getToken()+"&v=20150108"+"&venuePhotos=1";
			Log.i("URL", url);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
            
            
			String authorizationString = "Basic " + Base64.encodeToString(
                    ("tester" + ":" + "tm-sdktest").getBytes(),
                    Base64.NO_WRAP); 
            request.setHeader("Authorization", authorizationString);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			
			JSONObject holeResult = new JSONObject(result);
			JSONObject metaJson = holeResult.getJSONObject("meta");
			String code = metaJson.getString("code");
			
			if(code.equals("200"))
			{
				JSONObject responseJson = holeResult.getJSONObject("response");
				JSONArray groups = responseJson.getJSONArray("groups");
				
				for(int counter1=0 ; counter1<groups.length() ; counter1++)
				{
					JSONObject oneGroup = groups.getJSONObject(counter1);
					JSONArray items = oneGroup.getJSONArray("items");
					Log.i("Items Legth", String.valueOf(items.length()));
					for(int counter2=0 ; counter2<items.length() ; counter2++)
					{
						Venue venueObj;
						Location locationObj;
						Photo photoObj;
						
						try
						{
							JSONObject oneVenue = items.getJSONObject(counter2).getJSONObject("venue");
						
							String venueID = oneVenue.getString("id");
							String venueName = oneVenue.getString("name");
						
							String address="",crossStreet="",lat="",lng="",distance="";
							try
							{
								JSONObject locationJson = oneVenue.getJSONObject("location");
								lat = locationJson.getString("lat");
								lng = locationJson.getString("lng");
								address = locationJson.getString("address");
								distance = locationJson.getString("distance");
								crossStreet = locationJson.getString("crossStreet");
								
								locationObj = new Location(address, crossStreet, lat, lng, distance);
							}
							catch (Exception e)
							{
								locationObj = new Location(address, crossStreet, lat, lng, distance);
								Log.e("Exception", e.getMessage());
							}
							catch (Error e)
							{
								locationObj = new Location(address, crossStreet, lat, lng, distance);
								Log.e("Exception", e.getMessage());
							}
						
							try
							{
								JSONObject photoJson = oneVenue.getJSONObject("photos");
								String prefix = photoJson.getJSONArray("groups").getJSONObject(0).getJSONArray("items").getJSONObject(0).getString("prefix");
								String suffix = photoJson.getJSONArray("groups").getJSONObject(0).getJSONArray("items").getJSONObject(0).getString("suffix");
								photoObj = new Photo(prefix, suffix);
							}
							catch (Exception e)
							{
								photoObj = new Photo("", "");
								Log.e("Exception", e.getMessage());
							}
							catch (Error e)
							{
								photoObj = new Photo("", "");
								Log.e("Exception", e.getMessage());
							}
							
							venueObj = new Venue(venueID, venueName, "", locationObj, photoObj);
						}
						catch(Exception e)
						{
							Log.e("Exception", e.getMessage());
							continue;
						}
						catch (Error e) 
						{
							Log.e("Exception", e.getMessage());
							continue;
						}
						allVenues.add(venueObj);
					}
				}
			}
			
			Log.i("Venues Legth", String.valueOf(allVenues.size()));
			return allVenues;
		}
		catch(Exception e)
		{
			Log.d("VenuesAPI", "Exception");
			Log.d("Message", e.getMessage());
		}
		return allVenues;
	}
	
	@Override
	protected void onPostExecute(ArrayList<Venue> result) 
	{
		dialogProgress.dismiss();
		listener.onTaskCompleted();
		
		super.onPostExecute(result);
	}
	
}

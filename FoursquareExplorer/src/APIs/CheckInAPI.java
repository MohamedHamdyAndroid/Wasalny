package APIs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.android.gms.internal.ar;

import Constants.HostData;
import Listeners.OnCheckCompleted;
import Listeners.OnTaskCompleted;
import Tokens.TokenStore;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class CheckInAPI extends AsyncTask<String, Void, String> {
	
	private OnCheckCompleted listener;
	private ProgressDialog registerProgress;

    public CheckInAPI(OnCheckCompleted listener,ProgressDialog dialog)
    {
        this.listener=listener;
        registerProgress = dialog;
    }
    protected void onPreExecute() 
    {
    	registerProgress.setMessage("Loading...");
    	registerProgress.setCancelable(false);
    	registerProgress.show();
    };
    
		HttpResponse response;
		@Override
		protected String doInBackground(String... arg0) 
		{
			String result="";
	        JSONObject json = new JSONObject();
			try
			{
				HttpClient client = new DefaultHttpClient();
				String usedUrl = HostData.checkInURL+"?venueId="+arg0[0]+"&oauth_token="+TokenStore.get().getToken()+"&v=20150110";
				HttpPost request = new HttpPost(usedUrl);
				
				//json.put("venueId", arg0[0]);
				//json.put("oauth_token", TokenStore.get().getToken());

	            StringEntity se = new StringEntity( json.toString());  
	            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	            request.setEntity(se);
	            String authorizationString = "Basic " + Base64.encodeToString(
	                    ("tester" + ":" + "tm-sdktest").getBytes(),
	                    Base64.NO_WRAP); 
	            request.setHeader("Authorization", authorizationString);
				response = client.execute(request);
				
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				
				String code = new JSONObject(result).getJSONObject("meta").getString("code");
				Log.d("Result", result);
				return code;
				
			}
			catch(Exception e)
			{
				Log.d("CheckInPage", "Exception");
				Log.d("Message", e.getMessage());
			}
			return "Failed";
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			registerProgress.dismiss();
			listener.onCheckCompleted();
			super.onPostExecute(result);
		}

}

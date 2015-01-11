package Helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class InternetMethods 
{
	public static boolean checkInternetConnection(Activity act) 
    {
        final ConnectivityManager conMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            Log.d("APPBasics check connection", "in if in checkInternetConnection");
            return true;
        } else {
            Log.d("APPBasics check connection", "Internet Connection Not Present");
            return false;
        }
    }
}

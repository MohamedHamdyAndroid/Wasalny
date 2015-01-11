package com.foursquareexplorer.Activities;
import com.foursquareexplorer.R;
import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import Helpers.InternetMethods;
import Tokens.TokenStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Splash extends Activity 
{
	
	private static final int REQUEST_CODE_FSQ_CONNECT = 200;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;
    private static final String CLIENT_ID = "1QC0STPUTZUIMIR1W0FI20PZAQHM11OYUAAT1VRDJITU3KUW";
    private static final String CLIENT_SECRET = "4OQXT5W2YS3ABWYKVOTZNACNL2M5KLGSIBLKVPV4P1KA4JAC";

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        
        try
        {
        	SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            String accessToken = p.getString("AcessToken", "");
            if(accessToken.equals("") && InternetMethods.checkInternetConnection(this))
            {
            	ensureUi();
            }
            else if(!(accessToken.equals("")))
            {
            	TokenStore.get().setToken(accessToken);
            	Intent goHome = new Intent( Splash.this , Home.class );
                startActivity(goHome);
            }
            else
            {
            	Toast.makeText(this, "You must turn on the Internet connection to get the Acess token", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
		{
        	toastMessage(this, e.getMessage());
			Log.d("Exception", e.getMessage());
		}
		catch (Error e) 
		{
			toastMessage(this, e.getMessage());
			Log.d("Exception", e.getMessage());
		}
    }
    
    private void ensureUi() 
    {
    	Intent intent = FoursquareOAuth.getConnectIntent(Splash.this, CLIENT_ID);
        
        if (FoursquareOAuth.isPlayStoreIntent(intent)) 
        {
            toastMessage(Splash.this, "The Application of foursqaure is not installed");
            startActivity(intent);
        } 
        else 
        {
            startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                onCompleteConnect(resultCode, data);
                break;
                
            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                onCompleteTokenExchange(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    
    
    private void onCompleteConnect(int resultCode, Intent data) 
    {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();
        
        if (exception == null) 
        {
            // Success.
            String code = codeResponse.getCode();
            performTokenExchange(code);

        } 
        else 
        {
            if (exception instanceof FoursquareCancelException) 
            {
                // Cancel.
                toastMessage(this, "Canceled");

            } 
            else if (exception instanceof FoursquareDenyException) 
            {
                // Deny.
            	toastMessage(this, "Denied");
                
            } 
            else if (exception instanceof FoursquareOAuthException) 
            {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");
                
            } 
            else if (exception instanceof FoursquareUnsupportedVersionException) 
            {
                // Unsupported Fourquare app version on the device.
            	toastError(this, exception);
                
            } 
            else if (exception instanceof FoursquareInvalidRequestException) 
            {
                // Invalid request.
            	toastError(this, exception);
                
            } 
            else 
            {
                // Error.
            	toastError(this, exception);
            }
        }
    }
    
    private void onCompleteTokenExchange(int resultCode, Intent data) 
    {
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();
        
        if (exception == null) 
        {
            String accessToken = tokenResponse.getAccessToken();
            // Success.
            toastMessage(this, "Access token: " + accessToken);
            
            // Persist the token for later use. In this example, we save
            // it to shared prefs.
            TokenStore.get().setToken(accessToken);
            
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			p.edit().putString("AcessToken", accessToken).commit();
            
            Log.d("Acess token : ", accessToken);
            Intent goHome = new Intent( Splash.this , Home.class );
            startActivity(goHome);
        } 
        else 
        {
            if (exception instanceof FoursquareOAuthException) 
            {
                // OAuth error.
                String errorMessage = ((FoursquareOAuthException) exception).getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");
                
            } 
            else 
            {
                // Other exception type.
            	toastError(this, exception);
            }
        }
    }
    
    
    private void performTokenExchange(String code) 
    {
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID, CLIENT_SECRET, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }
    public static void toastMessage(Context context, String message) 
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t) 
    {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
    
}

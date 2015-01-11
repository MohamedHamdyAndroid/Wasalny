package Handlers;

import com.foursquareexplorer.Activities.Splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GeneralExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Activity context;

    public GeneralExceptionHandler(Activity ctx) 
    {
        this.context = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) 
    {
        Intent out = new Intent(context, Splash.class);
        out.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(out);

        //DO NOT USE context.Finish(). It will not work
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}

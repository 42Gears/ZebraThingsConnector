package com.gears42.zebrademo1;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Android Application class
 */
public class MainApplication extends Application {

    /**
     * Saving context in static variable is discouraged
     */
    private static Context context = null;

    @NonNull
    public static Context getMainApplicationContext() {
        return context.getApplicationContext();
    }

    /**
     * This is the first point of application entry
     * Start the service here
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        final Intent serverServiceIntent = new Intent(getApplicationContext(), ZebraThingsService.class);
        startService(serverServiceIntent);
    }
}

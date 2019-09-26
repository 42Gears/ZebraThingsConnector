package com.gears42.zebrademo1;

import android.Manifest;

public class AppConstants {
    public static final String TAG = "ZebraThings";

    /**
     * Should be localhost unless the things server is not running on the same host
     **/
    public static final String CLIENT_IP = "127.0.0.1";

    /**
     * Credentials to access Things server
     * These credentials will be saved in plain text file
     **/
    public static final int PORT = 8888;
    public static final String USER = "things@example.com";
    public static final String PASSWORD = "ZebraTechnologies";
    public static final String KEY = "ZebraTechnologiesCorporationZebraTechnologiesCorporationZebraTechnologiesCorporationZebraTechnologiesCorporationZebraTechnologiesCorporation";

    /**
     * Channel Id for foreground service notification
     **/
    public static final String CHANNEL_ID = "com.gears42.suremdm.zebrademo1.server";

    /**
     * Used for generating things URL
     **/
    public static final String WS_BASE = String.format("%s://%s:%d/", "ws", AppConstants.CLIENT_IP, AppConstants.PORT);

    /**
     * List of runtime permissions required
     **/
    public static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
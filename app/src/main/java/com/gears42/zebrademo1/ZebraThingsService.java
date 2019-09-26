package com.gears42.zebrademo1;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.Scanner;

import org.mozilla.iot.webthing.CredentialsIx;
import org.mozilla.iot.webthing.ServerManager;
import org.mozilla.iot.webthing.Thing;

import java.util.HashMap;

/**
 * Main Android Service
 * Runs as a foreground service
 * This service should always be running for Things service to work
 **/
public class ZebraThingsService extends Service {

    private static final int FOREGROUND_ID = 200;
    private static final HashMap<String, Thing> things = new HashMap<>();
    private BroadcastReceiver batteryChangeReceiver;

    public static void addThing(final ZebraBatteryThing thing, final String id) {
        if (id != null && !things.containsKey(id)) {
            final Thing oldThing = things.put(id, thing);
            if (oldThing == null) {
                Log.d(AppConstants.TAG, "Added new Battery Thing dynamically");
            } else {
                Log.d(AppConstants.TAG, "Replaced New Thing");
            }
            BatteryChangeReceiver.lastBatteryThing = thing;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Initialize the components of Things service
     **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (ZebraScannerManager.getScanner() == null) {
            /* Initialize EMDK Manager and Listener */
            EMDKResults results = EMDKManager.getEMDKManager(MainApplication.getMainApplicationContext(), new ZebraScannerManager());
            Log.d(AppConstants.TAG, "EMDKResults = " + results);
        }

        startForegroundZebraThingsService();
        startZebraThingsService();

        return START_STICKY;
    }

    /**
     * Start Things server
     **/
    private void startZebraThingsService() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            final ServerManager serverManager = new ServerManager();
            final String rootPath = Environment.getExternalStorageDirectory().toString();

            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        final CredentialsIx credentials = new Credentials(AppConstants.USER, AppConstants.PASSWORD, AppConstants.KEY);
                        Log.d(AppConstants.TAG, "Starting Server");
                        serverManager.startServer(things, "List of Things", AppConstants.PORT, AppConstants.CLIENT_IP, credentials, rootPath);
                        Log.d(AppConstants.TAG, "Server Started");
                        populateThings();
                    } catch (Exception ex) {
                        Log.e(AppConstants.TAG, "Error in starting Things Service", ex);
                    }
                }
            };
            t.start();

        } else {
            Log.w(AppConstants.TAG, "Cannot start server. Runtime permission is missing.");
        }
    }

    /**
     * Create Battery Thing and Scanner Thing
     **/
    private void populateThings() {
        populateBatteryThing();

        populateScannerThing();

        Log.w(AppConstants.TAG, "Populated Things");
    }

    /**
     * Create Scanner Thing
     **/
    private void populateScannerThing() {
        final Scanner scanner = ZebraScannerManager.getScanner();
        if (scanner != null) {
            final ZebraScannerThing scannerThing = new ZebraScannerThing("Zebra InbuiltScanner", scanner);
            scannerThing.setHrefPrefix("/" + scannerThing.getDeviceId());
            scannerThing.setWsHref(AppConstants.WS_BASE + scannerThing.getDeviceId());
            things.put(scannerThing.getDeviceId(), scannerThing);

            Log.d(AppConstants.TAG, "Registered ScannerThing: " + scannerThing.getDeviceId());
        } else {
            Log.w(AppConstants.TAG, "Cannot initialize scanner thing - scanner object is null");
        }
    }

    /**
     * Create Battery Thing
     **/
    private void populateBatteryThing() {
        final IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                context.unregisterReceiver(this);
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    final Bundle batteryInfo;
                    batteryInfo = intent.getExtras();
                    if (batteryInfo != null) {
                        final ZebraBatteryThing batteryThing = new ZebraBatteryThing("Zebra SmartBattery", batteryInfo);
                        batteryThing.setHrefPrefix("/" + batteryThing.getDeviceId());
                        batteryThing.setWsHref(AppConstants.WS_BASE + batteryThing.getDeviceId());
                        addThing(batteryThing, batteryThing.getDeviceId());
                        batteryChangeReceiver = new BatteryChangeReceiver();
                        registerReceiver(batteryChangeReceiver, intentFilter);
                    }
                }
            }
        }, intentFilter);
    }

    private void startForegroundZebraThingsService() {
        final Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        final Notification notification = createNotification(pendingIntent);
        startForeground(FOREGROUND_ID, notification);
    }

    private Notification createNotification(PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return createNotificationOreo(pendingIntent);
        } else {
            return createNotificationClassic(pendingIntent);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private Notification createNotificationOreo(final PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
                .setContentTitle("Zebra Things Connector Service")
                .setContentText("Running as a background service")
                .setSmallIcon(R.drawable.gears42_logo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @SuppressWarnings("deprecation")
    private Notification createNotificationClassic(final PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this)
                .setContentTitle("Zebra Things Connector Service")
                .setContentText("Running as a background service")
                .setSmallIcon(R.drawable.gears42_logo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onDestroy() {
        if (batteryChangeReceiver != null) {
            unregisterReceiver(batteryChangeReceiver);
        }
        super.onDestroy();
    }
}
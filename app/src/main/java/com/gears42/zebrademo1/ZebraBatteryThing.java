package com.gears42.zebrademo1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import org.mozilla.iot.webthing.Thing;
import org.mozilla.iot.webthing.Value;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Zebra Battery Thing class
 * Zebra Battery consists of several read only properties
 * This class uses some hand-picked properties provided by Zebra SDK
 **/
public class ZebraBatteryThing extends Thing {

    public final Value<String> serialNumber;
    public final Value<String> partNumber;
    public final Value<String> manufacturedDate;
    public final Value<String> lastConnectedDate;
    public final Value<String> secondsSinceFirstUse;
    public final Value<String> timeRemaining;
    public final Value<Integer> batteryCapacity;
    public final Value<Integer> batteryCharge;
    public final Value<Integer> healthPercentage;
    public final Value<Boolean> isConnected;

    /**
     * Initialize all the properties in the constructor
     **/
    public ZebraBatteryThing(final String name, final Bundle batteryInfo) {
        super(name, Arrays.asList(name), name, batteryInfo.getString("serialnumber"));
        serialNumber = Utility.getStringProperty(this, "serialNumber", "Serial Number", true, batteryInfo.getString("serialnumber"));
        partNumber = Utility.getStringProperty(this, "partNumber", "Part Number", true, batteryInfo.getString("partnumber"));
        manufacturedDate = Utility.getStringProperty(this, "manufacturedDate", "Manufactured Date", true, batteryInfo.getString("mfd"));
        lastConnectedDate = Utility.getStringProperty(this, "lastConnectedDate", "Last Connected Date", true, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
        secondsSinceFirstUse = Utility.getStringProperty(this, "secondsSinceFirstUse", "Time Since First Use", true, Utility.formatSeconds(batteryInfo.getInt("seconds_since_first_use", -1)));
        timeRemaining = Utility.getStringProperty(this, "timeRemaining", "Time Remaining", true, Utility.formatSeconds(batteryInfo.getInt("timeremaining", -1)));
        batteryCapacity = Utility.getIntegerProperty(this, "batteryCapacity", "Battery Capacity (mAh)", true, batteryInfo.getInt("present_capacity", -1));
        batteryCharge = Utility.getIntegerProperty(this, "batteryCharge", "Battery Charge (mAh)", true, batteryInfo.getInt("present_charge", -1));
        healthPercentage = Utility.getIntegerProperty(this, "healthPercentage", "Health Percentage", true, batteryInfo.getInt("health_percentage", -1));
        isConnected = Utility.getBooleanProperty(this, "isConnected", "Is Connected", true, true);
    }

    /**
     * This is called when SureMDM server requests for updated battery information
     */
    @Override
    public void update() {
        Log.d(AppConstants.TAG, "Update requested from SureMDM Server");

        final String batteryId = this.getThing().getDeviceId();
        final String currentId = BatteryChangeReceiver.lastBatteryThing.serialNumber.get();
        isConnected.notifyOfExternalUpdate(Utility.equals(batteryId, currentId));

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        MainApplication.getMainApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                context.unregisterReceiver(this);
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    final Bundle batteryInfo = intent.getExtras();
                    BatteryChangeReceiver.updateBatteryThing(batteryInfo);
                }
            }
        }, intentFilter);
    }
}
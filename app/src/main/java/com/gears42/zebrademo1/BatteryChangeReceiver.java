package com.gears42.zebrademo1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.mozilla.iot.webthing.Value;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Called when ever any battery information changes
 */
public class BatteryChangeReceiver extends BroadcastReceiver {
    public static ZebraBatteryThing lastBatteryThing;

    /**
     * Checks if the value has notifies the things server
     **/
    private static boolean notifyOfExternalUpdate(final Value<String> originalValue, final String newValue) {
        if (originalValue != null && !Utility.equals(originalValue.get(), newValue)) {
            originalValue.notifyOfExternalUpdate(newValue);
            return true;
        }
        return false;
    }

    /**
     * Checks if the value has notifies the things server
     **/
    private static boolean notifyOfExternalUpdate(final Value<Integer> originalValue, final int newValue) {
        if (originalValue != null && originalValue.get() != newValue) {
            originalValue.notifyOfExternalUpdate(Integer.valueOf(newValue));
            return true;
        }
        return false;
    }

    /**
     * Checks if the value has notifies the things server
     **/
    private static boolean notifyOfExternalUpdate(final Value<Boolean> originalValue, final boolean newValue) {
        if (originalValue != null) {
            originalValue.notifyOfExternalUpdate(Boolean.valueOf(newValue));
            return true;
        }
        return false;
    }

    /**
     * Checks each battery parameter and notifies things server if changed
     **/
    public static void updateBatteryThing(final Bundle batteryInfo) {
        final ZebraBatteryThing batteryThing = lastBatteryThing;
        final String originalSerialNo = batteryThing.getDeviceId();
        final String currentSerialNo = batteryInfo.getString("serialnumber");
        Log.d(AppConstants.TAG, "OriginalSerialNo: " + originalSerialNo + " CurrentSerialNo:" + currentSerialNo);
        if (Utility.equals(originalSerialNo, currentSerialNo)) {
            notifyOfExternalUpdate(batteryThing.partNumber, batteryInfo.getString("partnumber"));
            notifyOfExternalUpdate(batteryThing.manufacturedDate, batteryInfo.getString("mfd"));

            notifyOfExternalUpdate(batteryThing.secondsSinceFirstUse, Utility.formatSeconds(batteryInfo.getInt("seconds_since_first_use", -1)));
            notifyOfExternalUpdate(batteryThing.timeRemaining, Utility.formatSeconds(batteryInfo.getInt("timeremaining", -1)));
            notifyOfExternalUpdate(batteryThing.batteryCapacity, batteryInfo.getInt("present_capacity", -1));
            notifyOfExternalUpdate(batteryThing.batteryCharge, batteryInfo.getInt("present_charge", -1));
            notifyOfExternalUpdate(batteryThing.healthPercentage, batteryInfo.getInt("health_percentage", -1));

            notifyOfExternalUpdate(batteryThing.lastConnectedDate, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
            notifyOfExternalUpdate(batteryThing.isConnected, true);
        } else {
            Log.d(AppConstants.TAG, "Marking battery as disconnected");
            notifyOfExternalUpdate(batteryThing.isConnected, false);


            // Add New Battery Thing
            final ZebraBatteryThing newBatteryThing = new ZebraBatteryThing("Zebra SmartBattery", batteryInfo);
            if (newBatteryThing != null) {
                newBatteryThing.setHrefPrefix("/" + newBatteryThing.getDeviceId());
                newBatteryThing.setWsHref(AppConstants.WS_BASE + newBatteryThing.getDeviceId());
                ZebraThingsService.addThing(newBatteryThing, newBatteryThing.getDeviceId());
                notifyOfExternalUpdate(newBatteryThing.isConnected, true);
            }
        }
    }

    /**
     * Broadcast Receiver
     **/
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            final Bundle batteryInfo = intent.getExtras();
            if (batteryInfo != null) {
                updateBatteryThing(batteryInfo);
            }
        }
    }
}

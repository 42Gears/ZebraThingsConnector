package com.gears42.zebrademo1;

import android.provider.Settings;
import android.util.Log;

import com.google.gson.JsonObject;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;

import org.mozilla.iot.webthing.Action;
import org.mozilla.iot.webthing.Thing;
import org.mozilla.iot.webthing.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zebra Scanner Thing class
 * Zebra Scanner consists of few read only properties, one read-write property and one action
 **/
public class ZebraScannerThing extends Thing {
    public final Value<String> connectionType;
    public final Value<String> decoderType;
    public final Value<String> deviceIdentifier;
    public final Value<String> deviceType;
    public final Value<String> friendlyName;
    public final Value<Boolean> isConnected;
    public final Value<Boolean> isDefaultScanner;
    public final Value<Boolean> decodeHapticFeedback;

    /**
     * Initialize all the properties in the constructor
     **/
    public ZebraScannerThing(final String name, final Scanner scanner) {
        super(name, Arrays.asList(name), name, Settings.Secure.getString(MainApplication.getMainApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        final ScannerInfo info = scanner.getScannerInfo();
        connectionType = Utility.getStringProperty(this, "connectionType", "Connection Type", true, info.getConnectionType().toString());
        decoderType = Utility.getStringProperty(this, "decoderType", "Decoder Type", true, info.getDecoderType().toString());
        deviceIdentifier = Utility.getStringProperty(this, "deviceIdentifier", "Device Identifier", true, info.getDeviceIdentifier().toString());
        deviceType = Utility.getStringProperty(this, "deviceType", "deviceType", true, info.getDeviceType().toString());
        friendlyName = Utility.getStringProperty(this, "friendlyName", "Friendly Name", true, info.getFriendlyName());
        isConnected = Utility.getBooleanProperty(this, "isConnected", "Is Connected", true, info.isConnected());
        isDefaultScanner = Utility.getBooleanProperty(this, "IsDefaultScanner", "Is Default Scanner", true, info.isDefaultScanner());
        decodeHapticFeedback = Utility.getBooleanProperty(this, "DecodeHapticFeedback", "Decode Haptic Feedback", false, getDecodeHapticFeedback(scanner), f -> setDecodeHapticFeedback(f));


        final Map<String, Object> actionStatus = new HashMap<String, Object>();
        final Map<String, Object> actionProperties = new HashMap<String, Object>();
        final Map<String, Object> actionInput = new HashMap<String, Object>();
        final Map<String, Object> actionMetadata = new HashMap<String, Object>();

        actionStatus.put("type", "null");
        actionProperties.put("reset", actionStatus);

        actionInput.put("type", "object");
        actionInput.put("required", Arrays.asList(new String[]{"reset"}));
        actionInput.put("properties", actionProperties);

        actionMetadata.put("label", "ResetConfig");
        actionMetadata.put("description", "Reset Scanner Config to Defaults");
        actionMetadata.put("input", actionInput);

        addAvailableAction("ResetConfig", actionMetadata, ResetToDefaultsAction.class);
    }

    /**
     * Writes the new value of the property DecodeHapticFeedback
     * This is a callback method
     **/
    private void setDecodeHapticFeedback(final boolean newValue) {
        Log.d(AppConstants.TAG, "Requested value change - setDecodeHapticFeedback: " + newValue);
        final Scanner scanner = ZebraScannerManager.getScanner();
        if (scanner != null && scanner.isEnabled()) {
            try {
                final ScannerConfig config = scanner.getConfig();
                if (config != null) {
                    config.scanParams.decodeHapticFeedback = newValue;
                    scanner.setConfig(config);
                    Log.d(AppConstants.TAG, "Successfully changed scannerConfig.scanParams.decodeHapticFeedback");
                    Log.d(AppConstants.TAG, "New Haptic feedback value = " + getDecodeHapticFeedback(scanner));

                }
            } catch (ScannerException e) {
                Log.d(AppConstants.TAG, "Cannot set Decode Haptic Feedback", e);
            }
        }
    }

    /**
     * Get the value of the property DecodeHapticFeedback
     **/
    private boolean getDecodeHapticFeedback(final Scanner scanner) {
        try {
            ScannerConfig scannerConfig = scanner.getConfig();
            boolean value = scannerConfig.scanParams.decodeHapticFeedback;
            Log.d(AppConstants.TAG, "Current Haptic feedback value = " + value);
            return value;
        } catch (ScannerException e) {
            Log.d(AppConstants.TAG, "Cannot get Decode Haptic Feedback", e);
        }
        return false;
    }

    /**
     * This is called when SureMDM server requests for updated scanner information
     */
    @Override
    public void update() {
        Log.d(AppConstants.TAG, "Requested Scanner Properties Update");
        final Scanner scanner = ZebraScannerManager.getScanner();
        if (scanner != null) {
            if (scanner.isEnabled()) {
                final boolean value = getDecodeHapticFeedback(scanner);
                decodeHapticFeedback.notifyOfExternalUpdate(Boolean.valueOf(value));
            } else {
                Log.i(AppConstants.TAG, "update: Scanner is either not enabled");
            }
        } else {
            Log.i(AppConstants.TAG, "update: Scanner is either not initialized");
        }
    }

    /**
     * Class representing Reset Scanner Config to Default
     * This class should always be public static
     */
    public static class ResetToDefaultsAction extends Action {

        public ResetToDefaultsAction(Thing thing, JsonObject input) {
            super(UUID.randomUUID().toString(), thing, "ResetConfig", input);
            Log.d(AppConstants.TAG, "ResetToDefaultsAction constructor " + input);
        }

        /**
         * Callback for performing the reset action
         **/
        @Override
        public void performAction() {
            Log.d(AppConstants.TAG, "Requested ResetToDefaultsAction");

            final Scanner scanner = ZebraScannerManager.getScanner();
            if (scanner != null && scanner.isEnabled()) {
                try {
                    final ScannerConfig config = scanner.getConfig();
                    if (config != null) {
                        config.resetToDefault(scanner);
                        scanner.setConfig(config);
                        Log.d(AppConstants.TAG, "resetToDefault successful");
                    }
                } catch (ScannerException e) {
                    Log.d(AppConstants.TAG, "Cannot resetToDefault", e);
                }
            }
        }
    }
}

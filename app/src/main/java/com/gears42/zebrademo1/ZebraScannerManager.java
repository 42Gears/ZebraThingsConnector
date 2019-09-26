package com.gears42.zebrademo1;

import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;

/**
 * EMDK Manager and Listener implementation
 * see http://techdocs.zebra.com/emdk-for-android/latest/api/
 **/
public class ZebraScannerManager implements EMDKManager.EMDKListener {

    private static Scanner scanner;

    public static Scanner getScanner() {
        return scanner;
    }

    @Override
    public void onOpened(final EMDKManager emdkManager) {
        Log.d(AppConstants.TAG, "EMDKManager.EMDKListener - onOpened");
        if (emdkManager != null) {
            final BarcodeManager barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
            if (barcodeManager != null) {
                final Scanner scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
                if (scanner != null) {
                    ZebraScannerManager.scanner = scanner;
                    try {
                        ZebraScannerManager.scanner.enable();
                    } catch (ScannerException e) {
                        ZebraScannerManager.scanner = null;
                        Log.d(AppConstants.TAG, "EMDKManager.EMDKListener - onOpened", e);
                    }
                }
            }
        }
    }

    @Override
    public void onClosed() {
        try {
            Log.d(AppConstants.TAG, "EMDKManager.EMDKListener - onClosed");
            scanner.disable();
        } catch (ScannerException e) {
            Log.d(AppConstants.TAG, "EMDKManager.EMDKListener - onClosed", e);
        }
        scanner = null;
    }
}
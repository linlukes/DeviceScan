package mg.app.myapplication.utils;

/**
 * @author M. JAMAL
 * @copyright Copyright (c) 2023  M. JAMAL
 * All rights reserved.
 * Created by M. JAMAL on 26/02/2024
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class LocationUtil {
    public static void showLocationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enable Location")
                .setMessage("Location services are required for this app. Please enable location.")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open location settings when positive button is clicked
                        requestLocationEnable(context);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle cancellation, if needed
                        dialog.dismiss();
                    }
                })
                .setCancelable(false); // Prevent the dialog from being dismissed by tapping outside of it

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    // Method to check if location services are enabled
    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Method to prompt the user to enable location services
    public static void requestLocationEnable(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }
}


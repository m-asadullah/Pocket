package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.cubixos.pocket.R;

public class DialogNetworkError {

    public static void networkError(Activity activity, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setIcon(R.drawable.ic_twotone_perm_scan_wifi_24)
                .setTitle("Network Error")
                .setMessage("Internet Connection is not available. Check your Data connection or WiFi")
                .setCancelable(false)
                .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Go to WiFi Setting - programmatically
                        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        final Intent intentWifi = new Intent(Intent.ACTION_MAIN, null);
                        intentWifi.addCategory(Intent.CATEGORY_LAUNCHER);
                        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        intentWifi.setComponent(cn);
                        intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity( intentWifi);
                    }
                })
                //set negative button
                .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(context,"There should be stable internet connection", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

    }
}

package com.cubixos.pocket.accessories.misc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AppPermissionManager extends AppCompatActivity {

    private static final int PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST = 13;

    public static void permissionStorage(Activity activity, Context context) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // to simplify, call requestPermissions again
                // Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_LONG).show();
                Toast.makeText(activity, "Allow permission to save files on device storage", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST);
                }
            } else {
                // No explanation needed; request the permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST);
                }
            }
        }else{
            // permission granted
            // writeImage();
            // Toast.makeText(getApplicationContext(), "shouldShowRequestPermissionRationale", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                // Toast.makeText(getApplicationContext(), "permission was granted, thx:)", Toast.LENGTH_LONG).show();
            } else {
                // permission denied.
                Toast.makeText(this, "Permission denied\nAllow permission to store data in storage.", Toast.LENGTH_SHORT).show();
            }
            return;
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

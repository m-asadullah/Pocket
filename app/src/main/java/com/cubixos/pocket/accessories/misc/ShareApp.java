package com.cubixos.pocket.accessories.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.cubixos.pocket.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ShareApp {

    public static void saveApp(Activity activity, Context context) {
        ApplicationInfo app = context.getApplicationInfo();
        String filePath = app.sourceDir;
        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        // Append file and send Intent
        File originalApk = new File(filePath);
        try {
            //Make new directory in new location
            File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+context.getString(R.string.app_name)+"/Apps");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;//
            String versionName = "1.0";
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + context.getString(app.labelRes).replace(" ","") +"_"+versionName+".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Share App
            //Uri uriFilePathAPK = FileProvider.getUriForFile(activity, "com.cubixos.math" + ".provider", tempFile);
            Uri uriFilePathAPK = FileProvider.getUriForFile(activity, context.getPackageName() + ".provider", tempFile);
            context.startActivity(ShareApp.intentShareApp(uriFilePathAPK, context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Intent intentShareApp(Uri apk, Context context) {
        Intent intEmailTo = new Intent(Intent.ACTION_SEND);
        intEmailTo.setType("text/email");
        intEmailTo.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name)+" App");
        intEmailTo.putExtra(Intent.EXTRA_TEXT, "For further information,\nClick this link to visit website.\nhttps://bit.ly/cubixos-apps");
        intEmailTo.putExtra(Intent.EXTRA_STREAM, apk);
        return Intent.createChooser(intEmailTo, "Share "+context.getString(R.string.app_name)+" App by");
    }
}

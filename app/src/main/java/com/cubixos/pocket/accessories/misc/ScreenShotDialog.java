package com.cubixos.pocket.accessories.misc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.notifications.Notify;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScreenShotDialog {

    public static void takeScreenshot(Activity activity, Context context, Dialog dialog, String dialogType, String title, String description) {
        //TimeStamp
        long timeStamp = System.currentTimeMillis();
        String messageId = Long.toString(timeStamp);
        //Calender - Date Time
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssa");
        String datetime = sdf.format(calendar.getTime());
        //
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+context.getString(R.string.app_name)+"/"+dialogType+"_"+datetime+".jpg"; //"/data/data/com.cubixos.test/test.jpg"; // use your desired path
            // create bitmap screen capture
            View view = dialog.getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageFile = new File(filePath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Notify.isProfileShotSuccess(activity, context, title ,description);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
            Toast.makeText(activity, "Message:\n"+e.getMessage()+"LocalMessage:\n"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

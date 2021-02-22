package com.cubixos.pocket.accessories.notifications;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cubixos.pocket.R;
import com.cubixos.pocket.oldpack.SplashScreenActivity;

import java.security.IdentityScope;

public class NotificationUploadSuccess {

    //Notification - Account Image
    public static void notificationFileUpload(Activity activity, Context context, String title, String description, String filename, String fileSize){
        String id = "id_file_upload" ;
        String stringTitle = title + " Uploaded";
        String stringDescription = filename + " of " + fileSize +" uploaded successfully."+ description;
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to data uploaded." ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, stringTitle, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.canShowBadge();
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(activity, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,id)
                .setContentTitle(stringTitle)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(stringDescription);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);
        managerCompat.notify(8001, builder.build());
    }
}

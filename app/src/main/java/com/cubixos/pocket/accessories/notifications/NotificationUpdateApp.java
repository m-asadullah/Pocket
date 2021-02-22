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

import com.cubixos.pocket.MainActivity;
import com.cubixos.pocket.R;

public class NotificationUpdateApp {

    //Notification - Update Available
    public static void notificationSettingUpdateYes(Activity activity, Context context){
        String id = "id_settings_update_yes" ;
        String title = "Update Available" ;
        String description = "Enhance your experience use latest available version." ;
        String channelID = "Settings" ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = activity.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,id)
                .setContentTitle(title)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setContentText(description);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);
        managerCompat.notify(4001, builder.build());
    }
    //Notification - Update Not
    public static void notificationSettingUpdateNo(Activity activity, Context context){
        String id = "id_settings_update_no" ;
        String title = "No Update Available" ;
        String description = "You are good to go, using latest version." ;
        String channelID = "Settings" ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = activity.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,id)
                .setContentTitle(title)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setContentText(description);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);
        managerCompat.notify(4002, builder.build());
    }


}

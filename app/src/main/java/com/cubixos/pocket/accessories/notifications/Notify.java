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

public class Notify{

    public static void isProfileShotSuccess(Activity activity, Context context, String title, String description) {
        String id = "id_account_profile" ;
        String notificationTitle = "Profile: "+title ;
        String notificationDescription = "Snap save to your storage. "+description+"\nOpen Math folder...";
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to account but error occurred." ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, notificationTitle, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.canShowBadge();
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, id)
                .setContentTitle(notificationTitle)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(notificationDescription);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(activity);
        managerCompat.notify(3002, builder.build());
    }

    public static boolean isProfileShoError(Context context){
        String id = "id_account_profile" ;
        String title = "Profile" ;
        String description = "Snap save to your storage. Open Math folder..." ;
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to account but error occurred." ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.canShowBadge();
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,id)
                .setContentTitle(title)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(description);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(3002, builder.build());

        return false;
    }
}

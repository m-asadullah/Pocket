package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.notifications.NotificationResetApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class DialogResetApp {
    
    public static void showDialogResetApp(Activity activity, Context context){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_reset);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.sound_bell);
        mediaPlayer.start();
        LinearLayout linearLayoutButtons = dialog.findViewById(R.id.linear_layout_buttons_dialog);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar_dialog);
        ImageView imageViewSuccessTick = dialog.findViewById(R.id.image_button_success_dialog);
        linearLayoutButtons.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        imageViewSuccessTick.setVisibility(View.GONE);
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_negative_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_positive_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutButtons.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                imageViewSuccessTick.setVisibility(View.GONE);
                //resetApp();
                try{
                    new Timer().schedule(new TimerTask(){
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    linearLayoutButtons.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    imageViewSuccessTick.setVisibility(View.VISIBLE);
                                    resetApp(activity, context);
                                    deleteCache(context);
                                    try{
                                        new Timer().schedule(new TimerTask(){
                                            public void run() {
                                                activity.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        activity.finish();
                                                        NotificationResetApp.showNotificationResetApp(activity, context);
                                                        //
                                                    }
                                                });
                                            }
                                        }, 2000);
                                    }catch (Exception e){
                                        FirebaseCrashlytics.getInstance().recordException(e);
                                    }
                                }
                            });
                        }
                    }, 8000);
                }catch (Exception e){
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        });
        dialog.show();
    }
    //Reset App Completely
    public static void resetApp(Activity activity, Context context) {
        File cacheDirectory = activity.getCacheDir();//here getActivity added
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }
    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }
        return deletedAll;
    }
    //Delete Cache - clear all cache and cache folder of app
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Toast.makeText(context,"Successfully deleted cache of this app from your phone", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context,"Error in  deleting cache. "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}

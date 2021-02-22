package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.WebViewStart;
import com.cubixos.pocket.settings.InfoActivity;

public class DialogVersionApp {

    public static void showVersionDialog(Activity activity, Context context) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_version);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.sound_bell);
        mediaPlayer.start();
        TextView textViewVersion = dialog.findViewById(R.id.text_view_app_version);
        String version = "1.0";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textViewVersion.setText(version);
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.image_view_course_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewStart.startWebView(activity, context, "http://bit.ly/math--course");
            }
        });
        dialog.findViewById(R.id.image_view_website_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewStart.startWebView(activity, context, "http://bit.ly/cubixos");
            }
        });
        dialog.findViewById(R.id.image_view_info_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInfo = new Intent(activity, InfoActivity.class);
                context.startActivity(intentInfo);
            }
        });
        dialog.show();
    }
}

package com.cubixos.pocket.oldpack;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.cubixos.pocket.R;
import com.cubixos.pocket.onboardingscreen.OnBoardingActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {//if greater than Android 4.4, API-20
                    Intent intentSplash = new Intent(SplashScreenActivity.this, OnBoardingActivity.class);
                    startActivity(intentSplash);
                    finish();
                } else {
                   showNotSupportedDialog();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void showNotSupportedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_not_support);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell_ring);
        mediaPlayer.start();
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            dialog.dismiss();
        }
        });
        dialog.findViewById(R.id.image_view_course_dialog).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAppWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cubixos.com/math"));
                startActivity(intentAppWebsite);
            }
        });
        dialog.findViewById(R.id.image_view_website_dialog).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAppWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bit.ly/cubixos"));
                startActivity(intentAppWebsite);
            }
        });
        dialog.findViewById(R.id.image_view_info_dialog).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAppWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cubixos.com/math-app-info"));
                startActivity(intentAppWebsite);
            }
        });
        dialog.show();
    }
}
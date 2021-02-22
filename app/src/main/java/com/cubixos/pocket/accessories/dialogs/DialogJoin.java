package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cubixos.pocket.R;
import com.cubixos.pocket.oldpack.AuthActivity;

public class DialogJoin {

    public static void showDialogJoin(Activity activity, Context context) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_join);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.sound_bell);
        mediaPlayer.start();
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //onBackPressed();
                activity.finish();
            }
        });
        dialog.findViewById(R.id.button_join_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AuthActivity.class);
                context.startActivity(intent);
                dialog.dismiss();
                //finish();
            }
        });
        dialog.show();
    }
}

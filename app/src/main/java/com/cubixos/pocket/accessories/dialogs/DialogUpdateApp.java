package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.WebViewStart;

public class DialogUpdateApp {

    //Dialog Update
    public static void showUpdateDialog(Activity activity, Context context, String newUrl, String newSize, String newDescription) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.sound_bell);
        mediaPlayer.start();
        TextView textViewSize = dialog.findViewById(R.id.text_view_size_update);
        TextView textViewDescription = dialog.findViewById(R.id.text_view_description_update);
        textViewSize.setText(newSize);
        textViewDescription.setText(newDescription);
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_update_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Url to update app
                if (CheckInternet.isNetwork(activity)){
                    WebViewStart.startWebView(activity, context, newUrl);
                }else {
                    Toast.makeText(activity, "Error: Network is unstable,\nCheck Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }
}

package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.ScreenShotDialog;
import com.cubixos.pocket.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DialogProfileAccount {

    //Profile Dialog
    public static void showDialogProfile(Activity activity, Context context, DocumentReference documentReference) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        //
        ImageView imageViewProfileDialog = dialog.findViewById(R.id.image_view_dialog_profile);
        ImageView imageViewVerification = dialog.findViewById(R.id.image_view_verified_badge);
        imageViewVerification.setVisibility(View.GONE);
        TextView textViewUsernameDialog = dialog .findViewById(R.id.text_view_username_dialog_profile);
        TextView textViewUsernameIdDialog = dialog .findViewById(R.id.text_view_usernameid_dialog_profile);
        TextView textViewEmailDialog = dialog .findViewById(R.id.text_view_email_dialog_profile);
        TextView textViewPhoneDialog = dialog .findViewById(R.id.text_view_phone_dialog_profile);
        TextView textViewStatusDialog = dialog .findViewById(R.id.text_view_status_dialog_profile);
        ImageButton imageButtonClose = dialog.findViewById(R.id.image_button_close_dialog);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    if (user.getProfilePicUrl() != null) {
                        //Picasso.get().load(user.getProfilePicUrl()).into(imageViewProfileDialog);
                        Picasso.get()
                                .load(user.getProfilePicUrl())
                                .placeholder(R.drawable.empty_profile_pic)
                                .error(R.drawable.ic_warning_sign_board)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(imageViewProfileDialog);
                    }
                    if(user.getUserName()!= null){
                        textViewUsernameDialog.setText(user.getUserName());
                    }else{
                        textViewUsernameDialog.setVisibility(View.GONE);
                    }
                    if(user.getUserNameId()!= null){
                        textViewUsernameIdDialog.setText(user.getUserNameId());
                    }else{
                        textViewUsernameIdDialog.setVisibility(View.GONE);
                    }
                    if(user.getStatus()!= null){
                        textViewStatusDialog.setText(user.getStatus());
                    }else{
                        textViewStatusDialog.setVisibility(View.GONE);
                    }
                    if (user.getEmailAddress()!= null){
                        textViewEmailDialog.setText(user.getEmailAddress());
                    }else{
                        textViewEmailDialog.setVisibility(View.GONE);
                    }
                    if (user.getPhoneNumber()!= null){
                        textViewPhoneDialog.setText(user.getPhoneNumber());
                        imageViewVerification.setVisibility(View.VISIBLE);
                    }else{
                        imageViewVerification.setVisibility(View.GONE);
                        textViewPhoneDialog.setVisibility(View.GONE);
                    }
                }
            }
        });
        dialog.findViewById(R.id.image_view_security).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Screen shot
                ScreenShotDialog.takeScreenshot(activity, context, dialog, "Account", textViewUsernameDialog.toString(), "");
            }
        });
        dialog.show();
    }
}

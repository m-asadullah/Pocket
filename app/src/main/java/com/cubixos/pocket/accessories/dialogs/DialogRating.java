package com.cubixos.pocket.accessories.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.cubixos.pocket.R;

public class DialogRating {

    public static void showDialogRating(Activity activity, Context context){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_rating);
        dialog.show();
    }
}

package com.cubixos.pocket.accessories.misc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cubixos.pocket.accessories.dialogs.DialogNetworkError;
import com.cubixos.pocket.view.WebViewActivity;

public class WebViewStart {

    public static void startWebView(Activity activity, Context context, String Url) {
        if (CheckInternet.isNetwork(activity)){
            Intent intent = new Intent(activity, WebViewActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("webUrl", Url);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context,"Something going wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            DialogNetworkError.networkError(activity, context);
        }
    }
}

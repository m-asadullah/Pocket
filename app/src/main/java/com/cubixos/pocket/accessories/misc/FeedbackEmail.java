package com.cubixos.pocket.accessories.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityDiagnosticsManager;
import android.net.Uri;
import android.widget.Toast;

import com.cubixos.pocket.R;

public class FeedbackEmail {

    public static Intent intentFeedbackEmail(Activity activity, Context context){
        Intent intFeedbackEmail = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:cubixos@yahoo.com"));
        intFeedbackEmail.putExtra(Intent.EXTRA_EMAIL, "Feedback about "+context.getResources().getString(R.string.app_name)+" App");
        intFeedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback about "+context.getResources().getString(R.string.app_name)+" App");
        intFeedbackEmail.putExtra(Intent.EXTRA_TEXT, "Feedback:\nHi App Developer,\n");
        Intent intentChooser = Intent.createChooser(intFeedbackEmail, "Contact via");
        if (intFeedbackEmail.resolveActivity(context.getPackageManager()) != null) {
            try {
                //startActivity(intentChooser);
                //startActivity(Intent.createChooser(intentChooser, "Send Feedback by"));
            }catch (Exception e){
                Toast.makeText(activity, "Error: Feedback\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        return Intent.createChooser(intentChooser,"Send Feedback by");
    }
}

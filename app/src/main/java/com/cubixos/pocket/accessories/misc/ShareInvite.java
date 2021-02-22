package com.cubixos.pocket.accessories.misc;

import android.content.Context;
import android.content.Intent;

import com.cubixos.pocket.R;

public class ShareInvite {

    public static Intent intentShareInvite(Context context) {
        Intent intShareInvite = new Intent(Intent.ACTION_SEND);
        intShareInvite.setType("text/plain");
        intShareInvite.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name)+" App");
        intShareInvite.putExtra(Intent.EXTRA_TEXT, "For further information  \nClick this link to visit website \nhttps://bit.ly/cubixos-apps");
        return Intent.createChooser(intShareInvite, "Invite "+context.getResources().getString(R.string.app_name)+" by");
    }
}

package com.cubixos.pocket.accessories.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class ShareFile {

    public static Intent intentShareFile(Activity activity, Context context, String filename, File file) {
        Uri uriFilePathShare = FileProvider.getUriForFile(activity,  context.getPackageName() + ".provider", file);
        Intent intShareFile = new Intent(Intent.ACTION_SEND);
        intShareFile.setType("text/email");
        intShareFile.putExtra(Intent.EXTRA_SUBJECT, filename);
        intShareFile.putExtra(Intent.EXTRA_TEXT, "For further information \nClick this link to visit website\nhttps://bit.ly/cubixos");
        intShareFile.putExtra(Intent.EXTRA_STREAM, uriFilePathShare);
        intShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intShareFile, "Share via");
    }
}

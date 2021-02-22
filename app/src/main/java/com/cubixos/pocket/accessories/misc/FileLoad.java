package com.cubixos.pocket.accessories.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import com.cubixos.pocket.R;

import java.io.File;

public class FileLoad {

    public static void loadLocally(Context context, String fileName, ImageView imageView) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name)+"/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }
}

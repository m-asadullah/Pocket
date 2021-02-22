package com.cubixos.pocket.accessories.misc;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.cubixos.pocket.R;

import java.io.File;

public class FileExist {

    public static boolean fileExists(Context context, String fileName) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Botany" +"/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            //Toast.makeText(context,"File:_"+fileName+"_Not Exist",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}

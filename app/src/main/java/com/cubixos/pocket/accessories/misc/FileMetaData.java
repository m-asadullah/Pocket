package com.cubixos.pocket.accessories.misc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileMetaData {

    //For Filename.ext exact OK
    public static String pickedFileName(Context context, Uri uri) {
        //Cursor cursor = resolver.query(uri, null, null, null, null);//given
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();
        return name;
    }

    //File Folder/Bucket
    public static String pickedFileFolder(Context context, Uri uri) {
        File file = new File(uri.getPath());
        String stringFolder = file.getParent();;
        return stringFolder;
    }

    //Folder as a File
    public static File pickedFolderAsFile(Context context, Uri uri){
        File file = new File(uri.getPath());
        File folderAsFile = file.getParentFile();
        return folderAsFile;
    }

    //File Extension OK
    public static String pickedFileExtension(Context context, Uri uri) {
        //ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String stringFileExt = mimeTypeMap.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        // Returning the file Extension.
        return stringFileExt;
    }

    //File MIME Type OK
    public static String pickedFileMimeType(Context context, Uri uri) {
        String stringMimeType = context.getContentResolver().getType(uri);
        return stringMimeType;
    }

    //For File size exact in bits as long
    public static long pickedFileSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        String size = cursor.getString(sizeIndex);
        //
        long longSize = Long.parseLong(size);
        //
        cursor.close();
        return longSize;
    }

    public static String pickedFileImageResolution(Context context, Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        long longWidth = options.outWidth;
        long longHeight = options.outHeight;

        String stringWidth = String.valueOf(longWidth);
        String stringHeight = String.valueOf(longHeight);

        String stringResolution = stringWidth + "x" + stringHeight;
        return stringResolution;
    }

    //small units get long size return in Size with units i.e: 29.8 KB...
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    //Capital Units
    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public static long fileSizeLong(Uri uri){
        File f = new File(uri.getPath());
        long sizeLong = f.length();
        return sizeLong;
    }

    public static String formatFileSize(long bits) {
        String hrSize = null;

        double b = bits;
        double k = bits/1024.0;
        double m = ((bits/1024.0)/1024.0);
        double g = (((bits/1024.0)/1024.0)/1024.0);
        double t = ((((bits/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }
        return hrSize;
    }

    //PDF File Pages
    public static long pickedFilePDFPages(Uri uri) throws IOException {
        //File pdfFile = new File(uri.getPath());
        File pdfFile = new File(String.valueOf(uri));
        long longTotalPages = 0;
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer pdfRenderer = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            int totalpages = pdfRenderer.getPageCount();
            longTotalPages = pdfRenderer.getPageCount();
        }
        return longTotalPages;
    }
    //PDF File Pages
    public static long pickedFilePDFThumbnailB(Context context, Uri uri, ImageView imageView){
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        ParcelFileDescriptor parcelFileDescriptor = null;
        int pageCount = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // create a new renderer
            PdfRenderer renderer= null;
            try {
                renderer = new PdfRenderer(parcelFileDescriptor);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                // say we render for showing on the screen
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);//For PDF Display
                // let us just render all pages
                // do stuff with the bitmap
                // close the page
                page.close();

                imageView.setImageBitmap(bitmap);
            }
            // close the renderer
            renderer.close();
        }
        return pageCount;
    }

    public static Bitmap generateImageFromPdf(Activity activity, Context context, Uri pdfUri) {
        int pageNumber = 0;
        Bitmap bmp = null;
        PdfiumCore pdfiumCore = new PdfiumCore(activity);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //to do with exception
        }
        return bmp;
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";

    private static void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, "PDF_"+TimeSet.timeStampDate()+".png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //to do with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //to do with exception
            }
        }
    }

    public static Uri imageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    //Media - Duration
    public static long longDurationAudioVideo(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        // retriever.setDataSource(context, Uri.fromFile(videoFile));
        retriever.setDataSource(context, uri);
        String stringTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(stringTime);
        retriever.release();
        return timeInMillisec;
    }

    //Convert timeMilliSeconds to humanReadable i.e: 27hr(s) 30min(s) 15sec(s) 0ms
    public static String formatMillisToString(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - ((hours * 60 * 60) + (minutes * 60));
        millis = millis - ((hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000));
        return String.format("%d hr(s) %d min(s) %d sec(s) %d ms", hours, minutes, seconds, millis);
    }

    //Convert timeMilliSeconds to humanReadable i.e: 1:15:30
    public static String formatMilliSecond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        // return  String.format("%02d Min, %02d Sec",
        // TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        // TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        // TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        // return timer string
        return finalTimerString;
    }

    public void fileExtractZip(String stringFilePathZip, String stringFilePathUnzip) {
        //create target location folder if not exist
        //dirChecker("", "");
        try {
            FileInputStream fin = new FileInputStream(stringFilePathZip);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(stringFilePathUnzip, ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(stringFilePathUnzip + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void dirChecker(String stringFilePathOut, String dir) {
        File f = new File(stringFilePathOut + dir);
        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public static String stringFilePathB(Context context, Uri uri) {
        String[] projection = { MediaStore.Files.FileColumns._ID };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }
}

package com.cubixos.pocket.accessories.misc;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class FileMediaMetaData {

    //Media - Album
    public static String longMediaAlbum(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        retriever.release();
        return stringMedia;
    }
    //Media - AlbumArtist
    public static String longMediaAlbumArtist(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        retriever.release();
        return stringMedia;
    }
    //Media - Artist
    public static String longMediaArtist(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        retriever.release();
        return stringMedia;
    }
    //Media - Author
    public static String longMediaAuthor(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
        retriever.release();
        return stringMedia;
    }
    //Media - Media has Audio
    public static boolean longMediaHasAudio(Context context, Uri uri){
        boolean audioTrack = false;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        retriever.release();

        if(stringMedia!=null && stringMedia.equals("yes")){
            audioTrack=true;
        } else{
            audioTrack=false;
        }
        return audioTrack;
    }
    //Media - Bitrate
    public static long longMediaBitrate(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        retriever.release();
        long longBitrate = Long.parseLong(stringMedia);
        return longBitrate;
    }
    //Media - Author - (API M = 23)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static long longMediaFrameRate(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
        retriever.release();
        long longFrameRate = Long.parseLong(stringMedia);
        return longFrameRate;
    }
    //Media - Author
    public static long longMediaCDTrackNumber(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        retriever.release();
        long longCDTrackNumber = Long.parseLong(stringMedia);
        return longCDTrackNumber;
    }
    //Media - Author - (API R = 30)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static long longMediaColorRange(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE);
        retriever.release();
        long longColorRange = Long.parseLong(stringMedia);
        return longColorRange;
    }
    //Media - Color Standard - (API R = 30)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static long longMediaColorStandard(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_STANDARD);
        retriever.release();
        long longColorStandard = Long.parseLong(stringMedia);
        return longColorStandard;
    }
    //Media - Color Transfer - (API R = 30)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static long longMediaColorTransfer(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_TRANSFER);
        retriever.release();
        long longColorTransfer = Long.parseLong(stringMedia);
        return longColorTransfer;
    }
    //Media - Compilation
    public static String longMediaCompilation(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
        retriever.release();
        return stringMedia;
    }
    //Media - Composer
    public static String stringMediaComposer(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        retriever.release();
        return stringMedia;
    }
    //Media - Color Transfer - (API R = 30)
    public static long longMediaDate(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        retriever.release();
        long longDate = Long.parseLong(stringMedia);
        return longDate;
    }
    //Media - Duration
    public static long longMediaDuration(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long longDuration = Long.parseLong(stringMedia);
        retriever.release();
        return longDuration;
    }
    //Media - Disc Number
    public static long longMediaDiscNumber(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);
        String stringMedia = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
        long longDiscNumber = Long.parseLong(stringMedia);
        retriever.release();
        return longDiscNumber;
    }
}

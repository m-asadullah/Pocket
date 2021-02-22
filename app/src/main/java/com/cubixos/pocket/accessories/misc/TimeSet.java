package com.cubixos.pocket.accessories.misc;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeSet {

    //TimeStamp as long
    public static long timeStampLong(){
        long timeStamp = System.currentTimeMillis();
        return timeStamp;
    }

    //TimeStamp as string
    public static String timeStampString(){
        long timeStamp = System.currentTimeMillis();
        String stringTimeStamp = Long.toString(timeStamp);
        return stringTimeStamp;
    }

    //Calender - Date Time
    public static String timeStampDate(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssa");
        String datetime = sdf.format(calendar.getTime());
        return datetime;
    }

    //Date Time Style Format, (yyyy-MM-DD HH:mm:ss a) 2020-12-06 08:25:59 PM
    public static String timeDateFormat(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

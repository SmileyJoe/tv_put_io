package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.format.Formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {

    private Format() {
    }

    public static String size(Context context, long size){
        if(size > 0){
            return Formatter.formatShortFileSize(context, size);
        } else {
            return null;
        }
    }

    public static String date(long millies){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(new Date(millies));
    }

    public static String timeAgo(Context context, long millies){
        if(millies > 0){
            return TimeUtil.toRelative(context, millies);
        } else {
            return null;
        }
    }
}

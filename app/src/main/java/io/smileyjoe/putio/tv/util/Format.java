package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.smileyjoe.putio.tv.R;

public class Format {

    private Format() {
    }

    public static String size(Context context, long size) {
        if (size > 0) {
            return Formatter.formatShortFileSize(context, size);
        } else {
            return null;
        }
    }

    public static String date(long millies) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(new Date(millies));
    }

    public static String timeAgo(Context context, long millies) {
        if (millies > 0) {
            return TimeUtil.toRelative(context, millies);
        } else {
            return null;
        }
    }

    public static long fromTmdbToMillies(String tmdbDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(tmdbDate);
            return date.getTime();
        } catch (ParseException e) {
            return -1;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static String runtime(Context context, int minutes) {
        if (minutes > 0) {
            int hours = minutes / 60;
            int minutesLeft = minutes % 60;
            return context.getString(R.string.text_runtime, hours, minutesLeft);
        } else {
            return null;
        }
    }
}

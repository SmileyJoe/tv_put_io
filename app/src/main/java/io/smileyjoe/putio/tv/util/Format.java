package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.text.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.smileyjoe.putio.tv.R;

public class Format {

    private Format() {
    }

    /**
     * Basically copied from {@link Formatter} because they won't let me use it,
     * and what I can use is fixed to using 1000 instead of 1024, which doesn't
     * work for this case.
     *
     * @param context context
     * @param sizeBytes size
     * @return formatted string
     */
    public static String size(Context context, long sizeBytes) {
        if (sizeBytes > 0) {
            final int unit = 1024;
            final int unitMax = unit - 1;
            final boolean isNegative = (sizeBytes < 0);
            float result = isNegative ? -sizeBytes : sizeBytes;
            int suffix = R.string.format_byte;
            long mult = 1;
            if (result > unitMax) {
                suffix = R.string.format_kilobyte;
                mult = unit;
                result = result / unit;
            }
            if (result > unitMax) {
                suffix = R.string.format_megabyte;
                mult *= unit;
                result = result / unit;
            }
            if (result > unitMax) {
                suffix = R.string.format_gigabyte;
                mult *= unit;
                result = result / unit;
            }
            if (result > unitMax) {
                suffix = R.string.format_terabyte;
                mult *= unit;
                result = result / unit;
            }
            if (result > unitMax) {
                suffix = R.string.format_petabyte;
                mult *= unit;
                result = result / unit;
            }

            final String roundFormat;
            if (mult == 1 || result >= 100) {
                roundFormat = "%.0f";
            } else if (result < 1) {
                roundFormat = "%.2f";
            } else if (result < 10) {
                roundFormat = "%.2f";
            } else {
                roundFormat = "%.2f";
            }

            if (isNegative) {
                result = -result;
            }

            final String roundedString = String.format(roundFormat, result);
            final String units = context.getString(suffix);
            return context.getString(R.string.format_disk_size, roundedString, units).toUpperCase(Locale.ROOT);
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

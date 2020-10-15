package io.smileyjoe.putio.tv.util;

import android.content.Context;

import androidx.annotation.PluralsRes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.smileyjoe.putio.tv.R;

/**
 * Taken from
 * https://stackoverflow.com/a/38117440
 */
public class TimeUtil {

    public static final Map<Integer, Long> sTimes = new LinkedHashMap<>();

    static {
        sTimes.put(R.plurals.plural_year, TimeUnit.DAYS.toMillis(365));
        sTimes.put(R.plurals.plural_month, TimeUnit.DAYS.toMillis(30));
        sTimes.put(R.plurals.plural_week, TimeUnit.DAYS.toMillis(7));
        sTimes.put(R.plurals.plural_day, TimeUnit.DAYS.toMillis(1));
        sTimes.put(R.plurals.plural_hour, TimeUnit.HOURS.toMillis(1));
        sTimes.put(R.plurals.plural_minute, TimeUnit.MINUTES.toMillis(1));
        sTimes.put(R.plurals.plural_second, TimeUnit.SECONDS.toMillis(1));
    }

    public static String toRelative(Context context, long timeAgo) {
        return toRelative(context, System.currentTimeMillis() - timeAgo, 1);
    }

    public static String toRelative(Context context, long duration, int maxLevel) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<Integer, Long> time : sTimes.entrySet()){
            int timeDelta = (int) (duration / time.getValue());
            if (timeDelta > 0){
                res.append(context.getResources().getQuantityString(time.getKey(), timeDelta, timeDelta))
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel){
                break;
            }
        }
        if ("".equals(res.toString())) {
            return context.getString(R.string.text_moments_ago);
        } else {
            res.setLength(res.length() - 2);
            return context.getString(R.string.text_time_ago, res.toString());
        }
    }

}

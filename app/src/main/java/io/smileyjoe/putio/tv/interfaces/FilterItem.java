package io.smileyjoe.putio.tv.interfaces;

import androidx.annotation.DrawableRes;

public interface FilterItem {

    @DrawableRes int getIconResId();
    boolean isDefaultSelected();
}

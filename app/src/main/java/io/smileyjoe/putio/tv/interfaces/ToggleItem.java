package io.smileyjoe.putio.tv.interfaces;

import androidx.annotation.DrawableRes;

public interface ToggleItem {

    @DrawableRes int getIconResId();
    boolean isSelected();
}

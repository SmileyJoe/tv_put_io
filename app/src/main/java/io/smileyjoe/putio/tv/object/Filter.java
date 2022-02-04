package io.smileyjoe.putio.tv.object;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.util.stream.Stream;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.ToggleItem;

public enum Filter implements ToggleItem {

    SHOW_WATCHED(0, R.drawable.ic_hide_watched_24, R.string.text_hide_watched, R.string.text_show_watched, false, Group.FILTER),
    SORT_CREATED(1, R.drawable.ic_sort_by_created_24, R.string.text_sort_created, R.string.text_sort_default, false, Group.SORT),
    SORT_RELEASED_ASCENDING(2, R.drawable.ic_sort_released_ascending_24, R.string.text_sort_released_ascending, R.string.text_sort_default, false, Group.SORT),
    SORT_RELEASED_DESCENDING(3, R.drawable.ic_sort_released_descending_24, R.string.text_sort_released_descending, R.string.text_sort_default, false, Group.SORT);

    public static enum Group {
        SORT(true),
        FILTER(false);

        private boolean mIsUnique;

        Group(boolean isUnique) {
            mIsUnique = isUnique;
        }

        public boolean isUnique() {
            return mIsUnique;
        }
    }

    private int mId;
    @DrawableRes
    private int mIconResId;
    @StringRes
    private int mTextResId;
    @StringRes
    private int mSelectedTextResId;
    private boolean mDefaultSelected;
    private Group mGroup;

    Filter(int id, @DrawableRes int iconResId, @StringRes int textResId, @StringRes int selectedTextResId, boolean defaultSelected, Group group) {
        mId = id;
        mIconResId = iconResId;
        mTextResId = textResId;
        mSelectedTextResId = selectedTextResId;
        mDefaultSelected = defaultSelected;
        mGroup = group;
    }

    @Override
    public int getIconResId() {
        return mIconResId;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public int getSelectedTextResId() {
        return mSelectedTextResId;
    }

    @Override
    public boolean isSelected() {
        return mDefaultSelected;
    }

    public Group getGroup() {
        return mGroup;
    }

    @Override
    public int getId() {
        return mId;
    }

    public static Filter getById(int id) {
        return Stream.of(values())
                .filter(filter -> filter.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

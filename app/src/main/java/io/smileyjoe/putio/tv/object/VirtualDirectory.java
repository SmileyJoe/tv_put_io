package io.smileyjoe.putio.tv.object;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;

public class VirtualDirectory implements Folder {

    private long mPutId;
    private String mTitle;
    @StringRes
    private int mSubtextOne;
    @StringRes
    private int mSubtextTwo;
    private FileType mFileType;
    @DrawableRes
    private int mIconResId;
    private Filter mDefaultFilter;

    public static VirtualDirectory getFromPutId(Context context, long putId) {
        if (putId == Putio.Files.PARENT_ID_RECENT) {
            return getRecentAdded(context);
        } else {
            return null;
        }
    }

    public static VirtualDirectory getRecentAdded(Context context) {
        VirtualDirectory recent = new VirtualDirectory();
        recent.setPutId(Putio.Files.PARENT_ID_RECENT);
        recent.setTitle(context.getString(R.string.text_recently_added));
        recent.setFileType(FileType.FOLDER);
        recent.setIconResId(R.drawable.ic_sort_by_created_24);
        recent.setDefaultFilter(Filter.SORT_CREATED);
        return recent;
    }

    public Video asVideo() {
        Video video = new Video();
        video.setPutId(getPutId());
        video.setTitle(getTitle());
        video.setFileType(getFileType());
        return video;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public long getPutId() {
        return mPutId;
    }

    public void setFileType(FileType fileType) {
        mFileType = fileType;
    }

    public FileType getFileType() {
        return mFileType;
    }

    public void setIconResId(@DrawableRes int iconResId) {
        mIconResId = iconResId;
    }

    @Override
    public int getIconResId() {
        return mIconResId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setSubtextOne(@StringRes int subtextOne) {
        mSubtextOne = subtextOne;
    }

    @Override
    public String getSubTextOne(Context context) {
        if (mSubtextOne != 0) {
            return context.getString(mSubtextOne);
        } else {
            return null;
        }
    }

    public void setSubtextTwo(@StringRes int subtextTwo) {
        mSubtextTwo = subtextTwo;
    }

    @Override
    public String getSubTextTwo(Context context) {
        if (mSubtextTwo != 0) {
            return context.getString(mSubtextTwo);
        } else {
            return null;
        }
    }

    @Override
    public FolderType getFolderType() {
        return FolderType.VIRTUAL;
    }

    public void setDefaultFilter(Filter defaultFilter) {
        mDefaultFilter = defaultFilter;
    }

    public Filter getDefaultFilter() {
        return mDefaultFilter;
    }
}

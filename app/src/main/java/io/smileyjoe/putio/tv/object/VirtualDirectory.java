package io.smileyjoe.putio.tv.object;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeString(this.mTitle);
        dest.writeInt(this.mSubtextOne);
        dest.writeInt(this.mSubtextTwo);
        dest.writeInt(this.mFileType == null ? -1 : this.mFileType.ordinal());
        dest.writeInt(this.mIconResId);
        dest.writeInt(this.mDefaultFilter == null ? -1 : this.mDefaultFilter.ordinal());
    }

    public VirtualDirectory() {
    }

    protected VirtualDirectory(Parcel in) {
        this.mPutId = in.readLong();
        this.mTitle = in.readString();
        this.mSubtextOne = in.readInt();
        this.mSubtextTwo = in.readInt();
        int tmpMFileType = in.readInt();
        this.mFileType = tmpMFileType == -1 ? null : FileType.values()[tmpMFileType];
        this.mIconResId = in.readInt();
        int tmpMDefaultFilter = in.readInt();
        this.mDefaultFilter = tmpMDefaultFilter == -1 ? null : Filter.values()[tmpMDefaultFilter];
    }

    public static final Parcelable.Creator<VirtualDirectory> CREATOR = new Parcelable.Creator<VirtualDirectory>() {
        @Override
        public VirtualDirectory createFromParcel(Parcel source) {
            return new VirtualDirectory(source);
        }

        @Override
        public VirtualDirectory[] newArray(int size) {
            return new VirtualDirectory[size];
        }
    };
}

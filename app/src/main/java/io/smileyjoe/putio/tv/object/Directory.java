package io.smileyjoe.putio.tv.object;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.util.Format;

public class Directory implements Folder, Parcelable {

    private long mPutId;
    private String mTitle;
    private long mSize;
    private long mUpdatedAt;
    private Video mVideo;

    public Directory() {
    }

    public Directory(Video video) {
        setPutId(video.getPutId());
        setSize(video.getSize());
        setTitle(video.getTitle());
        setUpdatedAt(video.getUpdatedAt());
        setVideo(video);
    }

    public long getPutId() {
        return mPutId;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public long getSize() {
        return mSize;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public Video getVideo() {
        return mVideo;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public void setUpdatedAt(long updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public void setVideo(Video video) {
        mVideo = video;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_folder_24;
    }

    @Override
    public String getSubTextOne(Context context) {
        return Format.size(context, mSize);
    }

    @Override
    public String getSubTextTwo(Context context) {
        return Format.timeAgo(context, mUpdatedAt);
    }

    @Override
    public FolderType getFolderType() {
        return FolderType.DIRECTORY;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "mPutId=" + mPutId +
                ", mTitle='" + mTitle + '\'' +
                ", mSize=" + mSize +
                ", mUpdatedAt=" + mUpdatedAt +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeString(this.mTitle);
        dest.writeLong(this.mSize);
        dest.writeLong(this.mUpdatedAt);
    }

    protected Directory(Parcel in) {
        this.mPutId = in.readLong();
        this.mTitle = in.readString();
        this.mSize = in.readLong();
        this.mUpdatedAt = in.readLong();
    }

    public static final Parcelable.Creator<Directory> CREATOR = new Parcelable.Creator<Directory>() {
        @Override
        public Directory createFromParcel(Parcel source) {
            return new Directory(source);
        }

        @Override
        public Directory[] newArray(int size) {
            return new Directory[size];
        }
    };
}

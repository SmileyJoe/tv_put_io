package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class HistoryItem implements Parcelable {

    private long mId;
    private String mTitle;
    private FolderType mFolderType;

    public static HistoryItem directory(Long id, String title){
        HistoryItem item = new HistoryItem();
        item.setId(id);
        item.setTitle(title);
        item.setFolderType(FolderType.DIRECTORY);
        return item;
    }

    public static HistoryItem group(Long id, String title){
        HistoryItem item = new HistoryItem();
        item.setId(id);
        item.setTitle(title);
        item.setFolderType(FolderType.GROUP);
        return item;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public FolderType getFolderType() {
        return mFolderType;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setFolderType(FolderType folderType) {
        mFolderType = folderType;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "mId=" + mId +
                ", mFolderType=" + mFolderType +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeInt(this.mFolderType == null ? -1 : this.mFolderType.ordinal());
    }

    public HistoryItem() {
    }

    protected HistoryItem(Parcel in) {
        this.mId = in.readLong();
        int tmpMFolderType = in.readInt();
        this.mFolderType = tmpMFolderType == -1 ? null : FolderType.values()[tmpMFolderType];
    }

    public static final Creator<HistoryItem> CREATOR = new Creator<HistoryItem>() {
        @Override
        public HistoryItem createFromParcel(Parcel source) {
            return new HistoryItem(source);
        }

        @Override
        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };
}

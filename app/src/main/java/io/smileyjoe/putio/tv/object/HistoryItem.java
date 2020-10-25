package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class HistoryItem implements Parcelable {

    private long mPutId;
    private ArrayList<Long> mPutIds;

    public long getPutId() {
        return mPutId;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public ArrayList<Long> getPutIds() {
        return mPutIds;
    }

    public void setPutIds(ArrayList<Long> putIds) {
        mPutIds = putIds;
    }

    public boolean isGroup(){
        if(mPutIds == null){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "mPutId=" + mPutId +
                ", mPutIds=" + mPutIds +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeList(this.mPutIds);
    }

    public HistoryItem() {
    }

    protected HistoryItem(Parcel in) {
        this.mPutId = in.readLong();
        this.mPutIds = new ArrayList<Long>();
        in.readList(this.mPutIds, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<HistoryItem> CREATOR = new Parcelable.Creator<HistoryItem>() {
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

package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.util.JsonUtil;

public class Account implements Parcelable {

    private String mUserName;
    private long mDiskUsed;
    private long mDiskAvailable;
    private long mDiskSize;
    private long mBandwidthUsage;

    public Account() {
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public long getDiskUsed() {
        return mDiskUsed;
    }

    public void setDiskUsed(long diskUsed) {
        mDiskUsed = diskUsed;
    }

    public long getDiskAvailable() {
        return mDiskAvailable;
    }

    public void setDiskAvailable(long diskAvailable) {
        mDiskAvailable = diskAvailable;
    }

    public long getDiskSize() {
        return mDiskSize;
    }

    public void setDiskSize(long diskSize) {
        mDiskSize = diskSize;
    }

    public long getBandwidthUsage() {
        return mBandwidthUsage;
    }

    public void setBandwidthUsage(long bandwidthUsage) {
        mBandwidthUsage = bandwidthUsage;
    }

    public static Account fromApi(JsonObject jsonObject){
        JsonUtil json = new JsonUtil(jsonObject.get("info").getAsJsonObject());
        JsonUtil jsonDisk = new JsonUtil(json.getJsonObject("disk"));

        Account account = new Account();
        account.setBandwidthUsage(json.getLong("monthly_bandwidth_usage"));
        account.setDiskAvailable(jsonDisk.getLong("avail"));
        account.setDiskSize(jsonDisk.getLong("size"));
        account.setDiskUsed(jsonDisk.getLong("used"));
        account.setUserName(json.getString("username"));

        return account;
    }

    @Override
    public String toString() {
        return "Account{" +
                "mUserName='" + mUserName + '\'' +
                ", mDiskUsed=" + mDiskUsed +
                ", mDiskAvailable=" + mDiskAvailable +
                ", mDiskSize=" + mDiskSize +
                ", mBandwidthUsage=" + mBandwidthUsage +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserName);
        dest.writeLong(this.mDiskUsed);
        dest.writeLong(this.mDiskAvailable);
        dest.writeLong(this.mDiskSize);
        dest.writeLong(this.mBandwidthUsage);
    }

    protected Account(Parcel in) {
        this.mUserName = in.readString();
        this.mDiskUsed = in.readLong();
        this.mDiskAvailable = in.readLong();
        this.mDiskSize = in.readLong();
        this.mBandwidthUsage = in.readLong();
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}

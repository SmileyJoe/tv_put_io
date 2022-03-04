package io.smileyjoe.putio.tv.object;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.util.JsonUtil;
import io.smileyjoe.putio.tv.util.VideoUtil;

public class Account implements Parcelable {

    public interface Listener{
        void retrieved(Account account);
    }

    private static Account sAccount;

    private String mUserName;
    private long mDiskUsed;
    private long mDiskAvailable;
    private long mDiskSize;
    private long mBandwidthUsage;
    private long mExpirationDate;

    public Account() {
    }

    public static void get(Context context, Listener listener){
        if(sAccount == null){
            Putio.Account.info(context, new Response() {
                @Override
                public void onSuccess(JsonObject result) {
                    sAccount = fromApi(result);
                    if(listener != null){
                        listener.retrieved(sAccount);
                    }
                }
            });
        } else {
            if(listener != null){
                listener.retrieved(sAccount);
            }
        }
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

    public long getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        mExpirationDate = expirationDate;
    }

    public void setExpirationDate(String date){
        setExpirationDate(VideoUtil.getMillies(date));
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
        account.setExpirationDate(json.getString("plan_expiration_date"));

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

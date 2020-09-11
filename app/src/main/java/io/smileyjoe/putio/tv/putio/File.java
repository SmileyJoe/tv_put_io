package io.smileyjoe.putio.tv.putio;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class File implements Parcelable {

    public enum Type{
        FOLDER("FOLDER", 1), VIDEO("VIDEO", 2), UNKNOWN("", 0);

        private String mApiKey;
        private int mOrder;

        Type(String apiKey, int order) {
            mApiKey = apiKey;
            mOrder = order;
        }

        public String getApiKey() {
            return mApiKey;
        }

        public int getOrder() {
            return mOrder;
        }

        public static Type fromApi(String apiKey){
            for(Type type:values()){
                if(type.getApiKey().equals(apiKey)){
                    return type;
                }
            }

            return UNKNOWN;
        }
    }

    private long mId;
    private String mName;
    private Type mFileType;
    private String mScreenShot;
    private String mDownloadUrl;
    private Uri mStreamUri;
    private boolean mIsWatched;
    private boolean mIsParent;

    public File() {
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Type getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        mFileType = Type.fromApi(fileType);
    }

    public String getScreenShot() {
        return mScreenShot;
    }

    public void setScreenShot(String screenShot) {
        mScreenShot = screenShot;
    }

    public Uri getStreamUri() {
        return mStreamUri;
    }

    public void setStreamUri(String url){
        setStreamUri(Uri.parse(url));
    }

    public void setStreamUri(Uri streamUri) {
        mStreamUri = streamUri;
    }

    public boolean isWatched() {
        return mIsWatched;
    }

    public void setWatched(boolean watched) {
        mIsWatched = watched;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return "File{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mFileType=" + mFileType +
                ", mScreenShot='" + mScreenShot + '\'' +
                ", mDownloadUrl='" + mDownloadUrl + '\'' +
                ", mStreamUri=" + mStreamUri +
                ", mIsWatched=" + mIsWatched +
                ", mIsParent=" + mIsParent +
                '}';
    }

    public boolean isParent() {
        return mIsParent;
    }

    public void setParent(boolean parent) {
        mIsParent = parent;
    }

    public static File fromApi(JsonObject fileJson){
        File file = new File();
        file.setName(fileJson.get("name").getAsString());
        file.setId(fileJson.get("id").getAsLong());
        file.setFileType(fileJson.get("file_type").getAsString());

        try{
            String firstAccessedAt = fileJson.get("first_accessed_at").getAsString();
            file.setWatched(!TextUtils.isEmpty(firstAccessedAt));
        } catch (UnsupportedOperationException e){
            file.setWatched(false);
        }
        try{
            file.setScreenShot(fileJson.get("screenshot").getAsString());
        } catch (UnsupportedOperationException e){
            // do nothing //
        }

        try{
            file.setStreamUri(fileJson.get("stream_url").getAsString());
        } catch (UnsupportedOperationException | NullPointerException e){
            // do nothing //
        }

        return file;
    }

    public static ArrayList<File> fromApi(JsonArray filesJson){
        ArrayList<File> files = new ArrayList<>();

        for(JsonElement fileElement:filesJson){
            files.add(fromApi(fileElement.getAsJsonObject()));
        }

        return files;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeInt(this.mFileType == null ? -1 : this.mFileType.ordinal());
        dest.writeString(this.mScreenShot);
        dest.writeString(this.mDownloadUrl);
        dest.writeParcelable(this.mStreamUri, flags);
        dest.writeByte(this.mIsWatched ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsParent ? (byte) 1 : (byte) 0);
    }

    protected File(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        int tmpMFileType = in.readInt();
        this.mFileType = tmpMFileType == -1 ? null : Type.values()[tmpMFileType];
        this.mScreenShot = in.readString();
        this.mDownloadUrl = in.readString();
        this.mStreamUri = in.readParcelable(Uri.class.getClassLoader());
        this.mIsWatched = in.readByte() != 0;
        this.mIsParent = in.readByte() != 0;
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel source) {
            return new File(source);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };
}

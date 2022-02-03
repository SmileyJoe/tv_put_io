package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.smileyjoe.putio.tv.util.JsonUtil;

public class Subtitle implements Parcelable {

    private long mPutId;
    private String mKey;
    private String mLanguage;
    private String mName;
    private String mSource;

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public long getPutId() {
        return mPutId;
    }

    public String getKey() {
        return mKey;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getName() {
        return mName;
    }

    public String getSource() {
        return mSource;
    }

    public static Subtitle fromApi(JsonObject jsonObject, long putId){
        Subtitle subtitle = new Subtitle();
        JsonUtil json = new JsonUtil(jsonObject);

        subtitle.setKey(json.getString("key"));
        subtitle.setLanguage(json.getString("language"));
        subtitle.setName(json.getString("name"));
        subtitle.setSource(json.getString("source"));
        subtitle.setPutId(putId);

        return subtitle;
    }

    public static ArrayList<Subtitle> fromApi(JsonArray jsonArray, long putId){
        return StreamSupport.stream(jsonArray.spliterator(), false)
                .map(jsonElement -> fromApi(jsonElement.getAsJsonObject(), putId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "mPutId=" + mPutId +
                ", mKey='" + mKey + '\'' +
                ", mLanguage='" + mLanguage + '\'' +
                ", mName='" + mName + '\'' +
                ", mSource='" + mSource + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeString(this.mKey);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mName);
        dest.writeString(this.mSource);
    }

    public Subtitle() {
    }

    protected Subtitle(Parcel in) {
        this.mPutId = in.readLong();
        this.mKey = in.readString();
        this.mLanguage = in.readString();
        this.mName = in.readString();
        this.mSource = in.readString();
    }

    public static final Parcelable.Creator<Subtitle> CREATOR = new Parcelable.Creator<Subtitle>() {
        @Override
        public Subtitle createFromParcel(Parcel source) {
            return new Subtitle(source);
        }

        @Override
        public Subtitle[] newArray(int size) {
            return new Subtitle[size];
        }
    };
}

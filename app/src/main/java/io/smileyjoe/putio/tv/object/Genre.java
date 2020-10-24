package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.util.JsonUtil;

@Entity(tableName = "genre")
public class Genre implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "title")
    private String mTitle;

    public Genre() {
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public static Genre fromApi(JsonObject jsonObject){
        Genre genre = new Genre();
        JsonUtil json = new JsonUtil(jsonObject);

        String name = json.getString("name");

        if(!TextUtils.isEmpty(name)){
            name = name.trim();
        }

        genre.setId(json.getInt("id"));
        genre.setTitle(name);

        return genre;
    }

    public static ArrayList<Genre> fromApi(JsonArray jsonArray){
        ArrayList<Genre> genres = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            genres.add(fromApi(jsonElement.getAsJsonObject()));
        }

        return genres;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
    }

    protected Genre(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
    }

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
}

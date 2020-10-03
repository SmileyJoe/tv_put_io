package io.smileyjoe.putio.tv.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;


public class TmdbDetails implements Parcelable {

    private long mId;
    private String mPoster;
    private String mBackdrop;
    private String mTitle;
    private ArrayList<Long> mGenreIds;
    private String mOverview;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String poster) {
        mPoster = poster;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    public void setBackdrop(String backdrop) {
        mBackdrop = backdrop;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<Long> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(ArrayList<Long> genreIds) {
        mGenreIds = genreIds;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public static TmdbDetails fromApi(JsonObject tmdbJson){
        TmdbDetails details = new TmdbDetails();

        details.setId(tmdbJson.get("id").getAsLong());
        details.setBackdrop(tmdbJson.get("backdrop_path").getAsString());
        details.setOverview(tmdbJson.get("overview").getAsString());
        details.setPoster(tmdbJson.get("poster_path").getAsString());
        details.setTitle(tmdbJson.get("title").getAsString());

        // genre_ids

        return details;
    }

    public static ArrayList<TmdbDetails> fromApi(JsonArray tmdbJson){
        ArrayList<TmdbDetails> details = new ArrayList<>();

        for(JsonElement fileElement:tmdbJson){
            details.add(fromApi(fileElement.getAsJsonObject()));
        }

        return details;
    }

    @Override
    public String toString() {
        return "TmdbDetails{" +
                "mId=" + mId +
                ", mPoster='" + mPoster + '\'' +
                ", mBackdrop='" + mBackdrop + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mGenreIds=" + mGenreIds +
                ", mOverview='" + mOverview + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mPoster);
        dest.writeString(this.mBackdrop);
        dest.writeString(this.mTitle);
        dest.writeList(this.mGenreIds);
        dest.writeString(this.mOverview);
    }

    public TmdbDetails() {
    }

    protected TmdbDetails(Parcel in) {
        this.mId = in.readLong();
        this.mPoster = in.readString();
        this.mBackdrop = in.readString();
        this.mTitle = in.readString();
        this.mGenreIds = new ArrayList<Long>();
        in.readList(this.mGenreIds, Long.class.getClassLoader());
        this.mOverview = in.readString();
    }

    public static final Parcelable.Creator<TmdbDetails> CREATOR = new Parcelable.Creator<TmdbDetails>() {
        @Override
        public TmdbDetails createFromParcel(Parcel source) {
            return new TmdbDetails(source);
        }

        @Override
        public TmdbDetails[] newArray(int size) {
            return new TmdbDetails[size];
        }
    };
}

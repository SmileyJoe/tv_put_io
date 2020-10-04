package io.smileyjoe.putio.tv.object;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "video")
public class Video implements Parcelable {

    // ids
    @PrimaryKey
    @ColumnInfo(name = "id_put_io")
    private long mPutId;
    @ColumnInfo(name = "id_tmdb")
    private long mTmdbId;
    // general
    @Ignore
    private VideoType mType;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "overview")
    private String mOverView;
    @Ignore
    private boolean mIsWatched;
    @Ignore
    private boolean mIsConverted;
    @Ignore
    private long mResumeTime;
    @Ignore
    private int mYear;
    @Ignore
    private int mSeason;
    @Ignore
    private int mEpisode;
    // images
    @ColumnInfo(name = "uri_poster")
    private String mPoster;
    @ColumnInfo(name = "uri_backdrop")
    private String mBackdrop;
    // video links
    @Ignore
    private Uri mStreamUri;
    @ColumnInfo(name = "is_tmdb_checked")
    private boolean mIsTmdbChecked = false;
    @ColumnInfo(name = "is_tmdb_found")
    private boolean mIsTmdbFound = false;
    @ColumnInfo(name = "genre_ids_json")
    private String mGenreIdsJson;
    @Ignore
    private ArrayList<Integer> mGenreIds;

    public Video() {
        mType = VideoType.UNKNOWN;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void setTmdbId(long tmdbId) {
        mTmdbId = tmdbId;
    }

    public void setType(VideoType type) {
        mType = type;
    }

    public void setType(String putType) {
        switch (putType) {
            case "FOLDER":
                setType(VideoType.FOLDER);
                break;
            case "VIDEO":
                setType(VideoType.VIDEO);
                break;
            default:
                setType(VideoType.UNKNOWN);
                break;
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setOverView(String overView) {
        mOverView = overView;
    }

    public void setWatched(boolean watched) {
        mIsWatched = watched;
    }

    public void setConverted(boolean converted) {
        mIsConverted = converted;
    }

    public void setResumeTime(long resumeTime) {
        mResumeTime = resumeTime;
    }

    public void setPoster(Uri poster) {
        mPoster = poster.toString();
    }

    public void setPoster(String poster) {
        if (!TextUtils.isEmpty(poster)) {
            setPoster(Uri.parse(poster));
        }
    }

    public void setBackdrop(Uri backdrop) {
        mBackdrop = backdrop.toString();
    }

    public void setBackdrop(String backdrop) {
        if (!TextUtils.isEmpty(backdrop)) {
            setBackdrop(Uri.parse(backdrop));
        }
    }

    public void setStreamUri(Uri streamUri) {
        mStreamUri = streamUri;
    }

    public void setStreamUri(String streamUri, String streamMp4Uri) {
        String uri;
        if (!TextUtils.isEmpty(streamMp4Uri)) {
            uri = streamMp4Uri;
        } else {
            uri = streamUri;
        }

        if (!TextUtils.isEmpty(uri)) {
            setStreamUri(Uri.parse(uri));
        }
    }

    public void setYear(int year) {
        mYear = year;
    }

    public void setSeason(int season) {
        mSeason = season;
    }

    public void setEpisode(int episode) {
        mEpisode = episode;
    }

    public void isTmdbChecked(boolean tmdbChecked) {
        mIsTmdbChecked = tmdbChecked;
    }

    public void isTmdbFound(boolean tmdbFound) {
        mIsTmdbFound = tmdbFound;
    }

    public void setGenreIdsJson(String genreIdsJson) {
        mGenreIdsJson = genreIdsJson;

        if(mGenreIds == null || mGenreIds.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            setGenreIds(gson.fromJson(genreIdsJson, type));
        }
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        mGenreIds = genreIds;

        if(TextUtils.isEmpty(mGenreIdsJson)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            setGenreIdsJson(gson.toJson(genreIds, type));
        }
    }

    public long getPutId() {
        return mPutId;
    }

    public long getTmdbId() {
        return mTmdbId;
    }

    public VideoType getType() {
        return mType;
    }

    public String getTitle() {
        if(mType == VideoType.EPISODE){
            return mTitle + " S" + String.format("%02d", getSeason()) + "E" + String.format("%02d", getEpisode());
        } else {
            return mTitle;
        }
    }

    public String getOverView() {
        return mOverView;
    }

    public boolean isWatched() {
        return mIsWatched;
    }

    public boolean isConverted() {
        return mIsConverted;
    }

    public long getResumeTime() {
        return mResumeTime;
    }

    public String getResumeTimeFormatted() {
        long hours = mResumeTime / 3600;
        long minutes = (mResumeTime % 3600) / 60;
        long seconds = mResumeTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getPoster() {
        return mPoster;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    public Uri getPosterAsUri() {
        if(!TextUtils.isEmpty(mPoster)) {
            return Uri.parse(mPoster);
        } else {
            return null;
        }
    }

    public Uri getBackdropAsUri() {
        if(!TextUtils.isEmpty(mBackdrop)) {
            return Uri.parse(mBackdrop);
        } else {
            return null;
        }
    }

    public Uri getStreamUri() {
        return mStreamUri;
    }

    public int getYear() {
        return mYear;
    }

    public int getSeason() {
        return mSeason;
    }

    public int getEpisode() {
        return mEpisode;
    }

    public boolean isTmdbChecked() {
        return mIsTmdbChecked;
    }

    public boolean isTmdbFound() {
        return mIsTmdbFound;
    }

    public String getGenreIdsJson() {
        return mGenreIdsJson;
    }

    public ArrayList<Integer> getGenreIds() {
        return mGenreIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeLong(this.mTmdbId);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeString(this.mTitle);
        dest.writeString(this.mOverView);
        dest.writeByte(this.mIsWatched ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsConverted ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mResumeTime);
        dest.writeInt(this.mYear);
        dest.writeInt(this.mSeason);
        dest.writeInt(this.mEpisode);
        dest.writeString(this.mPoster);
        dest.writeString(this.mBackdrop);
        dest.writeParcelable(this.mStreamUri, flags);
        dest.writeByte(this.mIsTmdbChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsTmdbFound ? (byte) 1 : (byte) 0);
        dest.writeString(this.mGenreIdsJson);
        dest.writeList(this.mGenreIds);
    }

    protected Video(Parcel in) {
        this.mPutId = in.readLong();
        this.mTmdbId = in.readLong();
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : VideoType.values()[tmpMType];
        this.mTitle = in.readString();
        this.mOverView = in.readString();
        this.mIsWatched = in.readByte() != 0;
        this.mIsConverted = in.readByte() != 0;
        this.mResumeTime = in.readLong();
        this.mYear = in.readInt();
        this.mSeason = in.readInt();
        this.mEpisode = in.readInt();
        this.mPoster = in.readString();
        this.mBackdrop = in.readString();
        this.mStreamUri = in.readParcelable(Uri.class.getClassLoader());
        this.mIsTmdbChecked = in.readByte() != 0;
        this.mIsTmdbFound = in.readByte() != 0;
        this.mGenreIdsJson = in.readString();
        this.mGenreIds = new ArrayList<Integer>();
        in.readList(this.mGenreIds, Integer.class.getClassLoader());
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}

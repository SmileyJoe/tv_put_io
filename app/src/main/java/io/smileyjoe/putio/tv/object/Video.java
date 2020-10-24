package io.smileyjoe.putio.tv.object;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.util.TimeUtil;
import io.smileyjoe.putio.tv.util.VideoUtil;

@Entity(tableName = "video")
public class Video implements Folder, Parcelable{

    // ids
    @PrimaryKey
    @ColumnInfo(name = "id_put_io")
    private long mPutId;
    @ColumnInfo(name = "id_tmdb")
    private long mTmdbId;
    // general
    @Ignore
    private VideoType mVideoType;
    @Ignore
    private FileType mFileType;
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
    @Ignore
    private long mSize;
    @Ignore
    private String mGenresFormatted;
    @Ignore
    private long mCreatedAt;
    @Ignore
    private long mUpdatedAt;

    public Video() {
        mVideoType = VideoType.UNKNOWN;
        mFileType = FileType.UNKNOWN;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void setTmdbId(long tmdbId) {
        mTmdbId = tmdbId;
    }

    public void setVideoType(VideoType videoType) {
        mVideoType = videoType;
    }

    public void setFileType(FileType fileType) {
        mFileType = fileType;
    }

    public void setFileType(String putType){
        setFileType(FileType.fromPut(putType));
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

    public void setSize(long size) {
        mSize = size;
    }

    public void setGenresFormatted(String genresFormatted) {
        mGenresFormatted = genresFormatted;
    }

    public void setCreatedAt(long createdAt) {
        mCreatedAt = createdAt;
    }

    public void setCreatedAt(String createdAt) {
        setCreatedAt(VideoUtil.getMillies(createdAt));
    }

    public void setUpdatedAt(long updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        setUpdatedAt(VideoUtil.getMillies(updatedAt));
    }

    public long getPutId() {
        return mPutId;
    }

    public long getTmdbId() {
        return mTmdbId;
    }

    public VideoType getVideoType() {
        return mVideoType;
    }

    public FileType getFileType() {
        return mFileType;
    }

    @Override
    public String getTitle() {
        if(mVideoType == VideoType.EPISODE){
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

    public long getSize() {
        return mSize;
    }

    public String getSizeFormatted(Context context){
        if(mSize > 0){
            return Formatter.formatShortFileSize(context, mSize);
        } else {
            return null;
        }
    }

    public String getGenresFormatted() {
        return mGenresFormatted;
    }

    public long getCreatedAt() {
        return mCreatedAt;
    }

    public String getCreatedAtFormatted(){
        return VideoUtil.getFormatted(mCreatedAt);
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getUpdatedAtFormatted(){
        return VideoUtil.getFormatted(mUpdatedAt);
    }

    public String getUpdatedAgo(Context context){
        if(mUpdatedAt > 0){
            return TimeUtil.toRelative(context, mUpdatedAt);
        } else {
            return null;
        }
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_folder_24;
    }

    @Override
    public String getSubTextOne(Context context) {
        return getSizeFormatted(context);
    }

    @Override
    public String getSubTextTwo(Context context) {
        return getUpdatedAgo(context);
    }

    @Override
    public String toString() {
        return "Video{" +
                "mPutId=" + mPutId +
                ", mTmdbId=" + mTmdbId +
                ", mVideoType=" + mVideoType +
                ", mFileType=" + mFileType +
                ", mTitle='" + mTitle + '\'' +
                ", mOverView='" + mOverView + '\'' +
                ", mIsWatched=" + mIsWatched +
                ", mIsConverted=" + mIsConverted +
                ", mResumeTime=" + mResumeTime +
                ", mYear=" + mYear +
                ", mSeason=" + mSeason +
                ", mEpisode=" + mEpisode +
                ", mPoster='" + mPoster + '\'' +
                ", mBackdrop='" + mBackdrop + '\'' +
                ", mStreamUri=" + mStreamUri +
                ", mIsTmdbChecked=" + mIsTmdbChecked +
                ", mIsTmdbFound=" + mIsTmdbFound +
                ", mGenreIdsJson='" + mGenreIdsJson + '\'' +
                ", mGenreIds=" + mGenreIds +
                ", mSize=" + mSize +
                ", mGenresFormatted='" + mGenresFormatted + '\'' +
                ", mCreatedAt=" + mCreatedAt +
                ", mUpdatedAt=" + mUpdatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Video)) return false;
        Video video = (Video) o;
        return mPutId == video.mPutId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPutId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeLong(this.mTmdbId);
        dest.writeInt(this.mVideoType == null ? -1 : this.mVideoType.ordinal());
        dest.writeInt(this.mFileType == null ? -1 : this.mFileType.ordinal());
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
        dest.writeLong(this.mSize);
        dest.writeString(this.mGenresFormatted);
        dest.writeLong(this.mCreatedAt);
        dest.writeLong(this.mUpdatedAt);
    }

    protected Video(Parcel in) {
        this.mPutId = in.readLong();
        this.mTmdbId = in.readLong();
        int tmpMVideoType = in.readInt();
        this.mVideoType = tmpMVideoType == -1 ? null : VideoType.values()[tmpMVideoType];
        int tmpMFileType = in.readInt();
        this.mFileType = tmpMFileType == -1 ? null : FileType.values()[tmpMFileType];
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
        this.mSize = in.readLong();
        this.mGenresFormatted = in.readString();
        this.mCreatedAt = in.readLong();
        this.mUpdatedAt = in.readLong();
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

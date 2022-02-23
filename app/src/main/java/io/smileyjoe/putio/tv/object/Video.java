package io.smileyjoe.putio.tv.object;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.converter.VideoTypeConverter;
import io.smileyjoe.putio.tv.util.TimeUtil;
import io.smileyjoe.putio.tv.util.VideoUtil;

@Entity(tableName = "video")
public class Video implements Parcelable {

    // ids
    @PrimaryKey
    @ColumnInfo(name = "id_put_io")
    private long mPutId;
    @ColumnInfo(name = "id_tmdb")
    private long mTmdbId;
    // general
    @ColumnInfo(name = "video_type")
    @TypeConverters(VideoTypeConverter.class)
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
    @ColumnInfo(name = "year")
    private int mYear;
    @ColumnInfo(name = "season")
    private int mSeason;
    @ColumnInfo(name = "episode")
    private int mEpisode;
    // images
    @ColumnInfo(name = "uri_poster")
    private String mPoster;
    @ColumnInfo(name = "uri_backdrop")
    private String mBackdrop;
    // video links
    @Ignore
    private Uri mStreamUri;
    @Ignore
    private Uri mStreamMp4Uri;
    @ColumnInfo(name = "is_tmdb_checked")
    private boolean mIsTmdbChecked = false;
    @ColumnInfo(name = "is_tmdb_found")
    private boolean mIsTmdbFound = false;
    @ColumnInfo(name = "genre_ids_json")
    private String mGenreIdsJson;
    @ColumnInfo(name = "youtube_trailer_key")
    private String mYoutubeTrailerKey;
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
    @ColumnInfo(name = "release_date")
    private long mReleaseDate;
    @ColumnInfo(name = "tagline")
    private String mTagLine;
    @ColumnInfo(name = "runtime")
    private int mRuntime;
    @Ignore
    private ArrayList<Character> mCharacters;
    @Ignore
    private String mPutTitle;
    @Ignore
    private boolean mIsConverting;

    public Video() {
        mVideoType = VideoType.UNKNOWN;
        mFileType = FileType.UNKNOWN;
    }

    public Video(Video video) {
        this.mPutId = video.getPutId();
        this.mTmdbId = video.getTmdbId();
        this.mVideoType = video.getVideoType();
        this.mFileType = video.getFileType();
        this.mTitle = video.getTitle();
        this.mOverView = video.getOverView();
        this.mIsWatched = video.isWatched();
        this.mIsConverted = video.isConverted();
        this.mResumeTime = video.getResumeTime();
        this.mYear = video.getYear();
        this.mSeason = video.getSeason();
        this.mEpisode = video.getEpisode();
        this.mPoster = video.getPoster();
        this.mBackdrop = video.getBackdrop();
        this.mStreamUri = video.getStreamUri();
        this.mIsTmdbChecked = video.isTmdbChecked();
        this.mIsTmdbFound = video.isTmdbFound();
        this.mGenreIdsJson = video.getGenreIdsJson();
        this.mYoutubeTrailerKey = video.getYoutubeTrailerKey();
        this.mGenreIds = video.getGenreIds();
        this.mSize = video.getSize();
        this.mGenresFormatted = video.getGenresFormatted();
        this.mCreatedAt = video.getCreatedAt();
        this.mUpdatedAt = video.getUpdatedAt();
        this.mReleaseDate = video.getReleaseDate();
        this.mTagLine = video.getTagLine();
        this.mRuntime = video.getRuntime();
        this.mCharacters = video.getCharacters();
        this.mPutTitle = video.getPutTitle();
        this.mIsConverting = video.isConverting();
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

    public void setFileType(String putType) {
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

    public void setStreamUri(String streamUri) {
        if (!TextUtils.isEmpty(streamUri)) {
            setStreamUri(Uri.parse(streamUri));
        }
    }

    public void setStreamMp4Uri(Uri streamMp4Uri) {
        mStreamMp4Uri = streamMp4Uri;
    }

    public void setStreamMp4Uri(String streamMp4Uri) {
        if (!TextUtils.isEmpty(streamMp4Uri)) {
            setStreamMp4Uri(Uri.parse(streamMp4Uri));
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

        if (mGenreIds == null || mGenreIds.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            setGenreIds(gson.fromJson(genreIdsJson, type));
        }
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        mGenreIds = genreIds;

        if (TextUtils.isEmpty(mGenreIdsJson)) {
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

    public void setReleaseDate(long releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setTagLine(String tagLine) {
        mTagLine = tagLine;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    public void setCharacters(ArrayList<Character> characters) {
        mCharacters = characters;
    }

    public void setYoutubeTrailerKey(String key) {
        mYoutubeTrailerKey = key;
    }

    public void setPutTitle(String putTitle) {
        mPutTitle = putTitle;
    }

    public void setConverting(boolean converting) {
        mIsConverting = converting;
    }

    public String getYoutubeTrailerKey() {
        return mYoutubeTrailerKey;
    }

    public String getYoutubeTrailerUrl() {
        if (!TextUtils.isEmpty(mYoutubeTrailerKey)) {
            return "https://www.youtube.com/watch?v=" + mYoutubeTrailerKey;
        } else {
            return null;
        }
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

    public String getTitleFormatted(Context context, boolean includeSeason) {
        if (mVideoType == VideoType.EPISODE) {
            String prefix = "";
            if (includeSeason) {
                prefix = String.format("S%02dE", getSeason());
            }

            prefix += String.format("%02d. ", getEpisode());
            return prefix + mTitle;
        } else if (mVideoType == VideoType.SEASON && includeSeason) {
            return mTitle + ": " + context.getString(R.string.text_season) + " " + getSeason();
        } else {
            return mTitle;
        }
    }

    public String getTitle() {
        return mTitle;
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
        if (!TextUtils.isEmpty(mPoster)) {
            return Uri.parse(mPoster);
        } else {
            return null;
        }
    }

    public Uri getBackdropAsUri() {
        if (!TextUtils.isEmpty(mBackdrop)) {
            return Uri.parse(mBackdrop);
        } else {
            return null;
        }
    }

    public Uri getStreamUri(boolean playMp4) {
        if (playMp4) {
            return getStreamMp4Uri();
        } else {
            return getStreamUri();
        }
    }

    public Uri getStreamUri() {
        return mStreamUri;
    }

    public Uri getStreamMp4Uri() {
        return mStreamMp4Uri;
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

    public String getSizeFormatted(Context context) {
        if (mSize > 0) {
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

    public String getCreatedAtFormatted() {
        return VideoUtil.getFormatted(mCreatedAt);
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getUpdatedAtFormatted() {
        return VideoUtil.getFormatted(mUpdatedAt);
    }

    public String getUpdatedAgo(Context context) {
        if (mUpdatedAt > 0) {
            return TimeUtil.toRelative(context, mUpdatedAt);
        } else {
            return null;
        }
    }

    public long getReleaseDate() {
        return mReleaseDate;
    }

    public String getReleaseDateFormatted() {
        return VideoUtil.getFormatted(mReleaseDate);
    }

    public String getTagLine() {
        return mTagLine;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public ArrayList<Character> getCharacters() {
        return mCharacters;
    }

    public String getPutTitle() {
        return mPutTitle;
    }

    public boolean isConverting() {
        return mIsConverting;
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
        dest.writeParcelable(this.mStreamMp4Uri, flags);
        dest.writeByte(this.mIsTmdbChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsTmdbFound ? (byte) 1 : (byte) 0);
        dest.writeString(this.mGenreIdsJson);
        dest.writeString(this.mYoutubeTrailerKey);
        dest.writeList(this.mGenreIds);
        dest.writeLong(this.mSize);
        dest.writeString(this.mGenresFormatted);
        dest.writeLong(this.mCreatedAt);
        dest.writeLong(this.mUpdatedAt);
        dest.writeLong(this.mReleaseDate);
        dest.writeString(this.mTagLine);
        dest.writeInt(this.mRuntime);
        dest.writeTypedList(this.mCharacters);
        dest.writeString(this.mPutTitle);
        dest.writeByte(this.mIsConverting ? (byte) 1 : (byte) 0);
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
        this.mStreamMp4Uri = in.readParcelable(Uri.class.getClassLoader());
        this.mIsTmdbChecked = in.readByte() != 0;
        this.mIsTmdbFound = in.readByte() != 0;
        this.mGenreIdsJson = in.readString();
        this.mYoutubeTrailerKey = in.readString();
        this.mGenreIds = new ArrayList<Integer>();
        in.readList(this.mGenreIds, Integer.class.getClassLoader());
        this.mSize = in.readLong();
        this.mGenresFormatted = in.readString();
        this.mCreatedAt = in.readLong();
        this.mUpdatedAt = in.readLong();
        this.mReleaseDate = in.readLong();
        this.mTagLine = in.readString();
        this.mRuntime = in.readInt();
        this.mCharacters = in.createTypedArrayList(Character.CREATOR);
        this.mPutTitle = in.readString();
        this.mIsConverting = in.readByte() != 0;
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
                ", mStreamMp4Uri=" + mStreamMp4Uri +
                ", mIsTmdbChecked=" + mIsTmdbChecked +
                ", mIsTmdbFound=" + mIsTmdbFound +
                ", mGenreIdsJson='" + mGenreIdsJson + '\'' +
                ", mYoutubeTrailerKey='" + mYoutubeTrailerKey + '\'' +
                ", mGenreIds=" + mGenreIds +
                ", mSize=" + mSize +
                ", mGenresFormatted='" + mGenresFormatted + '\'' +
                ", mCreatedAt=" + mCreatedAt +
                ", mUpdatedAt=" + mUpdatedAt +
                ", mReleaseDate=" + mReleaseDate +
                ", mTagLine='" + mTagLine + '\'' +
                ", mRuntime=" + mRuntime +
                ", mCharacters=" + mCharacters +
                ", mPutTitle='" + mPutTitle + '\'' +
                ", mIsConverting=" + mIsConverting +
                '}';
    }
}

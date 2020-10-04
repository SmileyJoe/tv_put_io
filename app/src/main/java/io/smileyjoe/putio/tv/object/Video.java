package io.smileyjoe.putio.tv.object;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Video implements Parcelable {

    // ids
    private long mPutId;
    private long mTmdbId;
    // general
    private VideoType mType;
    private String mTitle;
    private String mOverView;
    private boolean mIsWatched;
    private boolean mIsConverted;
    private long mResumeTime;
    private int mYear;
    private int mSeason;
    private int mEpisode;
    // images
    private Uri mPoster;
    private Uri mBackdrop;
    // video links
    private Uri mStreamUri;

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
        mPoster = poster;
    }

    public void setPoster(String poster) {
        if (!TextUtils.isEmpty(poster)) {
            setPoster(Uri.parse(poster));
        }
    }

    public void setBackdrop(Uri backdrop) {
        mBackdrop = backdrop;
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

    public Uri getPoster() {
        return mPoster;
    }

    public Uri getBackdrop() {
        return mBackdrop;
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
        dest.writeParcelable(this.mPoster, flags);
        dest.writeParcelable(this.mBackdrop, flags);
        dest.writeParcelable(this.mStreamUri, flags);
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
        this.mPoster = in.readParcelable(Uri.class.getClassLoader());
        this.mBackdrop = in.readParcelable(Uri.class.getClassLoader());
        this.mStreamUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
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

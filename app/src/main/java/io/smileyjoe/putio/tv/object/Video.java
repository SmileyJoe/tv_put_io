package io.smileyjoe.putio.tv.object;

import android.net.Uri;
import android.text.TextUtils;

public class Video {

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

    public void setType(String putType){
        switch (putType){
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

    public void setPoster(String poster){
        setPoster(Uri.parse(poster));
    }

    public void setBackdrop(Uri backdrop) {
        mBackdrop = backdrop;
    }

    public void setBackdrop(String backdrop){
        setBackdrop(Uri.parse(backdrop));
    }

    public void setStreamUri(Uri streamUri) {
        mStreamUri = streamUri;
    }

    public void setStreamUri(String streamUri, String streamMp4Uri){
        if(!TextUtils.isEmpty(streamMp4Uri)){
            setStreamUri(Uri.parse(streamMp4Uri));
        } else {
            setStreamUri(Uri.parse(streamUri));
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
}

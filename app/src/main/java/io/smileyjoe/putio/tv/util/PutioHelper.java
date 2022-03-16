package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;

import io.smileyjoe.putio.tv.comparator.FolderComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.FileType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.object.VirtualDirectory;

public class PutioHelper {

    public interface Listener extends TmdbUtil.Listener {
    }

    private ArrayList<Folder> mFolders;
    private ArrayList<Video> mVideos;
    private Video mCurrent;
    private Context mContext;
    private Listener mListener;

    public PutioHelper(Context context) {
        mContext = context;
        mFolders = new ArrayList<>();
        mVideos = new ArrayList<>();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ArrayList<Video> getVideos() {
        return mVideos;
    }

    public ArrayList<Folder> getFolders() {
        return mFolders;
    }

    public Video getCurrent() {
        return mCurrent;
    }

    public void parse(long putId, JsonObject jsonObject) {
        JsonArray filesJson = jsonObject.getAsJsonArray("files");

        try {
            JsonObject parentObject = jsonObject.getAsJsonObject("parent");
            mCurrent = VideoUtil.parseFromPut(mContext, parentObject);
        } catch (ClassCastException e) {
            mCurrent = VirtualDirectory.getFromPutId(mContext, putId).asVideo();
        }

        if (mCurrent.getFileType() == FileType.FOLDER) {

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(mContext, filesJson));

            if (videos != null && videos.size() == 1) {
                Video currentDbVideo = VideoUtil.getFromDbByPutId(mContext, mCurrent.getPutId());

                if (currentDbVideo != null && currentDbVideo.isTmdbFound()) {
                    Video updated = VideoUtil.updateFromDb(videos.get(0), currentDbVideo);
                    AppDatabase.getInstance(mContext).videoDao().insert(updated);
                }
            }

            for (Video video : videos) {
                updateTmdb(video);
            }
        } else {
            Video currentDbVideo = VideoUtil.getFromDbByPutId(mContext, mCurrent.getPutId());

            if (currentDbVideo != null) {
                if(currentDbVideo.isTmdbFound()){
                    Video updated = VideoUtil.updateFromDb(mCurrent, currentDbVideo);
                    mVideos.add(updated);
                } else {
                    updateTmdb(currentDbVideo);
                }
            } else {
                updateTmdb(mCurrent);
            }
        }

        VideoUtil.sort(mVideos);
        Collections.sort(mFolders, new FolderComparator());
    }

    private void updateTmdb(Video video){
        // todo: this needs to happen for current //
        switch (video.getVideoType()) {
            case MOVIE:
                if (!video.isTmdbChecked()) {
                    TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(mContext, video);
                    response.setListener(mListener);
                    Tmdb.Movie.search(mContext, video.getTitle(), video.getYear(), response);
                }
            case EPISODE:
                if (!video.isTmdbChecked() && mCurrent.getTmdbId() > 0) {
                    TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(mContext, video);
                    response.setListener(mListener);
                    Tmdb.Series.getEpisode(mContext, mCurrent.getTmdbId(), video.getSeason(), video.getEpisode(), response);
                }
                mVideos.add(video);
                break;
            case SEASON:
                if (!video.isTmdbChecked()) {
                    TmdbUtil.OnTmdbSeriesSearchResponse response = new TmdbUtil.OnTmdbSeriesSearchResponse(mContext, video);
                    response.setListener(mListener);
                    Tmdb.Series.search(mContext, video.getTitle(), response);
                }
                mVideos.add(video);
                break;
            case UNKNOWN:
                switch (video.getFileType()) {
                    case VIDEO:
                        mVideos.add(video);
                        break;
                    case FOLDER:
                    case UNKNOWN:
                    default:
                        mFolders.add(new Directory(video));
                        break;
                }
                break;
        }
    }

}

package io.smileyjoe.putio.tv.util;

import android.content.Context;

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
import io.smileyjoe.putio.tv.object.VirtualDirectory;

public class PutioHelper {

    private ArrayList<Folder> mFolders;
    private ArrayList<Video> mVideos;
    private Video mCurrent;
    private Context mContext;

    public PutioHelper(Context context) {
        mContext = context;
        mFolders = new ArrayList<>();
        mVideos = new ArrayList<>();
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
        parse(putId, 0, jsonObject);
    }

    public void parse(long putId, long parentTmdbId, JsonObject jsonObject) {
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
                updateTmdb(mCurrent.getTmdbId(), video);
            }
        } else {
            Video currentDbVideo = VideoUtil.getFromDbByPutId(mContext, mCurrent.getPutId());

            if (currentDbVideo != null) {
                if (currentDbVideo.isTmdbFound()) {
                    Video updated = VideoUtil.updateFromDb(mCurrent, currentDbVideo);
                    AppDatabase.getInstance(mContext).videoDao().insert(updated);
                    mVideos.add(updated);
                } else {
                    AppDatabase.getInstance(mContext).videoDao().insert(mCurrent);
                    updateTmdb(parentTmdbId, currentDbVideo);
                }
            } else {
                AppDatabase.getInstance(mContext).videoDao().insert(mCurrent);
                updateTmdb(parentTmdbId, mCurrent);
            }
        }

        VideoUtil.sort(mVideos);
        Collections.sort(mFolders, new FolderComparator());
    }

    private void updateTmdb(long parentTmdbId, Video video) {
        switch (video.getVideoType()) {
            case MOVIE:
                if (!video.isTmdbChecked()) {
                    TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(mContext, video);
                    Tmdb.Movie.search(mContext, video.getTitle(), video.getYear(), response);
                }
            case EPISODE:
                if (!video.isTmdbChecked() && parentTmdbId > 0) {
                    video.setParentTmdbId(parentTmdbId);
                    TmdbUtil.OnTmdbResponse response = new TmdbUtil.OnTmdbResponse(mContext, video);
                    Tmdb.Series.getEpisode(mContext, parentTmdbId, video.getSeason(), video.getEpisode(), response);
                }
                mVideos.add(video);
                break;
            case SEASON:
                if (!video.isTmdbChecked()) {
                    TmdbUtil.OnTmdbSeriesSearchResponse response = new TmdbUtil.OnTmdbSeriesSearchResponse(mContext, video);
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

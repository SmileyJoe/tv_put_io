package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.smileyjoe.putio.tv.comparator.FolderComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.Video;

public class PutioHelper {

    public interface Listener{
        void update(Video video);
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

    public void setListener(Listener listener){
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

    public void parse(long putId, JsonObject jsonObject){

        JsonArray filesJson = jsonObject.getAsJsonArray("files");
        JsonObject parentObject = jsonObject.getAsJsonObject("parent");

        if(putId == Putio.NO_PARENT){
            List<Group> groups = AppDatabase.getInstance(mContext).groupDao().getAll();

            if(groups != null && !groups.isEmpty()){
                for(Group group:groups){
                    mFolders.add(group);
                }
            }
        }

        ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(mContext, filesJson));
        mCurrent = VideoUtil.parseFromPut(mContext, parentObject);

        if(videos != null && videos.size() == 1){
            Video currentDbVideo = AppDatabase.getInstance(mContext).videoDao().getByPutId(mCurrent.getPutId());

            if(currentDbVideo != null && currentDbVideo.isTmdbFound()){
                Video updated = VideoUtil.updateFromDb(videos.get(0), currentDbVideo);
                AppDatabase.getInstance(mContext).videoDao().insert(updated);
            }
        }

        for (Video video : videos) {
            switch (video.getVideoType()) {
                case MOVIE:
                    if(!video.isTmdbChecked()) {
                        Tmdb.searchMovie(mContext, video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                    }
                case EPISODE:
                    mVideos.add(video);
                    break;
                case UNKNOWN:
                    switch (video.getFileType()){
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

        VideoUtil.sort(mVideos);
        Collections.sort(mFolders, new FolderComparator());
    }

    private class OnTmdbSearchResponse extends Response {

        private Video mVideo;

        public OnTmdbSearchResponse(Video video) {
            mVideo = video;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessTmdbResponse task = new ProcessTmdbResponse(mVideo, result);
            task.execute();
        }
    }

    private class ProcessTmdbResponse extends AsyncTask<Void, Void, Video> {
        private JsonObject mResult;
        private Video mVideo;

        public ProcessTmdbResponse(Video video, JsonObject result) {
            mVideo = video;
            mResult = result;
        }

        @Override
        protected Video doInBackground(Void... voids) {
            VideoUtil.updateFromTmdb(mVideo, mResult.get("results").getAsJsonArray());

            AppDatabase.getInstance(mContext).videoDao().insert(mVideo);
            return mVideo;
        }

        @Override
        protected void onPostExecute(Video video) {
            if(mListener != null) {
                mListener.update(video);
            }
        }
    }

}

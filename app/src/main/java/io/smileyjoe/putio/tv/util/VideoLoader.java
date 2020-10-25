package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

public class VideoLoader {

    public interface Listener extends PutioHelper.Listener{
        void onVideosLoadStarted();
        void onVideosLoadFinished(Long currentPutId, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory);
    }

    private Context mContext;
    private HashMap<Long, ArrayList<Video>> mVideos;
    private HashMap<Long, ArrayList<Folder>> mFolders;
    private ArrayList<Long> mHistory;
    private Listener mListener;

    public VideoLoader(Context context, Listener listener) {
        mContext = context;
        mVideos = new HashMap<>();
        mFolders = new HashMap<>();
        mHistory = new ArrayList<>();
        mListener = listener;
    }

    public void load(){
        getFromPut(Putio.NO_PARENT);
    }

    public void load(Long putId){
        ArrayList<Video> videos = getVideos(putId);

        if(videos == null){
            getFromPut(putId);
        } else {
            onVideosLoaded(putId, videos, getFolders(putId), true);
        }
    }

    public void load(Group group){
        mListener.onVideosLoadStarted();
        GetFromPut task = new GetFromPut(group);
        task.execute();
    }

    public boolean back(){
        if(mHistory != null && mHistory.size() >= 2) {
            Long current = getCurrentPutId();
            mHistory.remove(current);
            current = getCurrentPutId();
            onVideosLoaded(current, getVideos(current), getFolders(current), false);
            return true;
        }

        return false;
    }

    public Long getCurrentPutId(){
        return mHistory.get(mHistory.size() - 1);
    }

    private ArrayList<Video> getVideos(long putId){
        return mVideos.get(putId);
    }

    private ArrayList<Folder> getFolders(long putId){
        return mFolders.get(putId);
    }

    private void onVideosLoaded(Long current, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory){
        mListener.onVideosLoadFinished(current, videos, folders, shouldAddToHistory);
    }

    private void getFromPut(long putId){
        mListener.onVideosLoadStarted();
        Putio.getFiles(mContext, putId, new OnPutResponse(putId));
    }

    public void addToHistory(Long currentPutId){
        mHistory.add(currentPutId);
    }

    private class GetFromPut extends AsyncTask<Void, Void, Void>{
        private Group mGroup;
        private ArrayList<Video> mGroupVideos;
        private ArrayList<Folder> mGroupFolders;

        public GetFromPut(Group group) {
            mGroupFolders = new ArrayList<>();
            mGroupVideos = new ArrayList<>();
            mGroup = group;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mGroup != null && mGroup.getPutIds() != null && !mGroup.getPutIds().isEmpty()) {
                PutioHelper helper;
                for (long id:mGroup.getPutIds()){
                    ArrayList<Video> videos = getVideos(id);
                    ArrayList<Folder> folders = getFolders(id);

                    if(videos == null) {
                        helper = new PutioHelper(mContext);
                        helper.setListener(mListener);

                        JsonObject result = Putio.getFiles(mContext, id);
                        Log.d("PutThings", "Result: " + result.toString());
                        helper.parse(id, result);

                        videos = helper.getVideos();
                        folders = helper.getFolders();

                        mVideos.put(id, videos);
                        mFolders.put(id, folders);
                    }

                    mGroupVideos.addAll(videos);
                    mGroupFolders.addAll(folders);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onVideosLoaded(mGroup.getIdAsLong(), mGroupVideos, mGroupFolders, true);
        }
    }

    private class OnPutResponse extends Response {

        private long mPutId;

        public OnPutResponse(long putId) {
            mPutId = putId;
        }

        @Override
        public void onSuccess(JsonObject result) {
            ProcessPutResponse task = new ProcessPutResponse(mPutId, result);
            task.execute();
        }
    }

    private class ProcessPutResponse extends AsyncTask<Void, Void, Void> {

        private long mPutId;
        private JsonObject mResult;
        private long mCurrentPutId;

        public ProcessPutResponse(long putId, JsonObject result) {
            mPutId = putId;
            mResult = result;
        }

        @Override
        protected Void doInBackground(Void... params) {
            PutioHelper helper = new PutioHelper(mContext);
            helper.setListener(mListener);
            helper.parse(mPutId, mResult);

            mCurrentPutId = helper.getCurrent().getPutId();

            mVideos.put(mCurrentPutId, helper.getVideos());
            mFolders.put(mCurrentPutId, helper.getFolders());

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            onVideosLoaded(mCurrentPutId, mVideos.get(mCurrentPutId), mFolders.get(mCurrentPutId), true);
        }
    }


}

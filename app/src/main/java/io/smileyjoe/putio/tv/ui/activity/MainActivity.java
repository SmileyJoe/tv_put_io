package io.smileyjoe.putio.tv.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Room;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements VideoListFragment.Listener {

    private TextView mTextTitle;

    private ArrayList<Video> mParentFiles;
    private Video mCurrentFile = null;

    private VideoListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;

    private LinearLayout mLayoutLists;
    private ProgressBar mProgressLoading;

    private VideoType mVideoTypeFocus = VideoType.UNKNOWN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentFiles = new ArrayList<>();
        mTextTitle = findViewById(R.id.text_title);
        mLayoutLists = findViewById(R.id.layout_lists);
        mProgressLoading = findViewById(R.id.progress_loading);

        mFragmentFolderList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentFolderList.setType(VideoListAdapter.Type.LIST, VideoType.FOLDER);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(VideoListAdapter.Type.GRID, VideoType.VIDEO);

        getFiles(Putio.NO_PARENT);

        // todo: this needs to be called when an id is not found in the db //
        Tmdb.updateMovieGenres(getBaseContext());
    }

    @Override
    public void onBackPressed() {
        if (mParentFiles != null && !mParentFiles.isEmpty()) {
            Video parentFile = mParentFiles.get(mParentFiles.size() - 1);
            getFiles(parentFile.getPutId());
            mParentFiles.remove(parentFile);
            mCurrentFile = null;
        } else {
            super.onBackPressed();
        }
    }

    private void getFiles(long parentId) {
        mLayoutLists.setVisibility(View.GONE);
        mProgressLoading.setVisibility(View.VISIBLE);
        Putio.getFiles(getBaseContext(), parentId, new OnPutResponse());
    }

    @Override
    public void onVideoClicked(Video video) {
        switch (video.getType()){
            case FOLDER:
                getFiles(video.getPutId());
                break;
            case EPISODE:
            case MOVIE:
            case VIDEO:
                showDetails(video);
                break;
        }
    }

    @Override
    public void hasFocus(VideoType videoType) {
        if(mVideoTypeFocus != videoType){
            mVideoTypeFocus = videoType;

            switch (videoType){
                case FOLDER:
                    mTextTitle.setVisibility(View.VISIBLE);
                    changeFolderWidth(R.dimen.width_folder_list_expanded);
                    mFragmentVideoList.setFullScreen(false);
                    break;
                case VIDEO:
                    mTextTitle.setVisibility(View.GONE);
                    changeFolderWidth(R.dimen.width_folder_list_contracted);
                    mFragmentVideoList.setFullScreen(true);
                    break;
            }
        }
    }

    private void changeFolderWidth(@DimenRes int widthResId){
        ViewGroup.LayoutParams params = mFragmentFolderList.getView().getLayoutParams();
        params.width = getResources().getDimensionPixelOffset(widthResId);
        mFragmentFolderList.getView().setLayoutParams(params);
    }

    private void showDetails(Video video){
        startActivity(DetailsActivity.getIntent(getBaseContext(), video, mFragmentVideoList.getVideos()));
    }

    private void populate(ArrayList<Video> videos) {
        ArrayList<Video> folders = new ArrayList<>();
        ArrayList<Video> videosSorted = new ArrayList<>();

        for (Video video : videos) {
            switch (video.getType()) {
                case VIDEO:
                case MOVIE:
                case EPISODE:
                    videosSorted.add(video);
                    break;
                case FOLDER:
                    folders.add(video);
                    break;
            }
        }

        mTextTitle.setText(mCurrentFile.getTitle());

        if((folders == null || folders.isEmpty()) && (videosSorted != null && videosSorted.size() == 1)){
            showDetails(videosSorted.get(0));
        } else {
            boolean folderFragmentIsVisible = populateFolders(folders);

            mFragmentVideoList.setFullScreen(!folderFragmentIsVisible);
            mFragmentVideoList.setVideos(videosSorted);
        }

        mLayoutLists.setVisibility(View.VISIBLE);
        mProgressLoading.setVisibility(View.GONE);
    }

    private boolean populateFolders(ArrayList<Video> videos) {
        boolean fragmentIsVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (videos == null || videos.isEmpty()) {
            transaction.hide(mFragmentFolderList);
            fragmentIsVisible = false;
        } else {
            transaction.show(mFragmentFolderList);
            mFragmentFolderList.setVideos(videos);
            fragmentIsVisible = true;
        }

        transaction.commit();
        return fragmentIsVisible;
    }

    private class OnPutResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            ProcessPutResponse task = new ProcessPutResponse(result);
            task.execute();
        }
    }

    private class ProcessPutResponse extends AsyncTask<Void, Void, ArrayList<Video>>{

        private JsonObject mResult;

        public ProcessPutResponse(JsonObject result) {
            mResult = result;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params) {

            if (mCurrentFile != null) {
                mParentFiles.add(mCurrentFile);
            }

            JsonArray filesJson = mResult.getAsJsonArray("files");
            JsonObject parentObject = mResult.getAsJsonObject("parent");

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(getBaseContext(), filesJson));
            VideoUtil.sort(videos);
            mCurrentFile = VideoUtil.parseFromPut(getBaseContext(), parentObject);

            for (Video video : videos) {
                if (video.getType() == VideoType.MOVIE) {
                    if(!video.isTmdbChecked()) {
                        Tmdb.searchMovie(getBaseContext(), video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                    }
                }
            }

            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            populate(videos);
        }
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

    private class ProcessTmdbResponse extends AsyncTask<Void, Void, Video>{
        private JsonObject mResult;
        private Video mVideo;

        public ProcessTmdbResponse(Video video, JsonObject result) {
            mVideo = video;
            mResult = result;
        }

        @Override
        protected Video doInBackground(Void... voids) {
            VideoUtil.updateFromTmdb(mVideo, mResult.get("results").getAsJsonArray());

            AppDatabase.getInstance(getBaseContext()).videoDao().insert(mVideo);
            return mVideo;
        }

        @Override
        protected void onPostExecute(Video video) {
            mFragmentVideoList.update(video);
        }
    }
}

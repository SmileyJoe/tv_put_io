package io.smileyjoe.putio.tv.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Video;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentFiles = new ArrayList<>();
        mTextTitle = findViewById(R.id.text_title);
        mLayoutLists = findViewById(R.id.layout_lists);
        mProgressLoading = findViewById(R.id.progress_loading);

        mFragmentFolderList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentFolderList.setType(VideoListAdapter.Type.LIST);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(VideoListAdapter.Type.GRID);

        getFiles(Putio.NO_PARENT);
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

        if(videosSorted != null && videosSorted.size() == 1){
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
            if (mCurrentFile != null) {
                mParentFiles.add(mCurrentFile);
            }

            JsonArray filesJson = result.getAsJsonArray("files");
            JsonObject parentObject = result.getAsJsonObject("parent");

            ArrayList<Video> videos = VideoUtil.filter(VideoUtil.parseFromPut(filesJson));
            VideoUtil.sort(videos);
            mCurrentFile = VideoUtil.parseFromPut(parentObject);

            // todo: testing, honey I shrunk the kids //
            for (Video video : videos) {
                if (video.getPutId() == 774524118) {
                    Tmdb.searchMovie(getBaseContext(), video.getTitle(), video.getYear(), new OnTmdbSearchResponse(video));
                    break;
                }
            }

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
            VideoUtil.updateFromTmdb(mVideo, result.get("results").getAsJsonArray());
        }
    }
}

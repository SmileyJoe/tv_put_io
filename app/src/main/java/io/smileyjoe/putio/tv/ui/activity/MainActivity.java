package io.smileyjoe.putio.tv.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import io.smileyjoe.putio.tv.ui.fragment.VideoSummaryFragment;
import io.smileyjoe.putio.tv.util.VideoLoader;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements VideoListFragment.Listener, VideoLoader.Listener {

    private TextView mTextTitle;

    private VideoListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;
    private VideoSummaryFragment mFragmentSummary;

    private LinearLayout mLayoutLists;
    private ProgressBar mProgressLoading;

    private VideoType mVideoTypeFocus = VideoType.UNKNOWN;
    private VideoLoader mVideoLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTitle = findViewById(R.id.text_title);
        mLayoutLists = findViewById(R.id.layout_lists);
        mProgressLoading = findViewById(R.id.progress_loading);

        mFragmentFolderList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentFolderList.setType(VideoListAdapter.Type.LIST, VideoType.FOLDER);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(VideoListAdapter.Type.GRID, VideoType.VIDEO);
        mFragmentSummary = (VideoSummaryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_summary);

        mVideoLoader = new VideoLoader(getBaseContext(), this);
        mVideoLoader.load();

        // todo: this needs to be called when an id is not found in the db //
        Tmdb.updateMovieGenres(getBaseContext());
    }

    @Override
    public void onBackPressed() {
        boolean hasHistory = mVideoLoader.back();

        if(!hasHistory){
            super.onBackPressed();
        }
    }

    @Override
    public void onVideosLoadStarted() {
        mLayoutLists.setVisibility(View.GONE);
        mProgressLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideosLoadFinished(Video current, ArrayList<Video> videos) {
        populate(current, videos);
    }

    @Override
    public void update(Video video) {
        mFragmentVideoList.update(video);
    }

    @Override
    public void onVideoClicked(Video video) {
        switch (video.getType()){
            case FOLDER:
                mVideoLoader.load(video);
                break;
            case EPISODE:
            case MOVIE:
            case VIDEO:
                showDetails(video);
                break;
        }
    }

    @Override
    public void hasFocus(VideoType videoType, Video video) {
        if(videoType == VideoType.VIDEO) {
            if (video.isTmdbFound()) {
                showFragment(mFragmentSummary);
                mFragmentSummary.setVideo(video);
            } else {
                hideFragment(mFragmentSummary);
            }
        }

        if(mVideoTypeFocus != videoType){
            mVideoTypeFocus = videoType;

            switch (videoType){
                case FOLDER:
                    mTextTitle.setVisibility(View.VISIBLE);
                    changeFragmentWidth(mFragmentFolderList, R.dimen.width_folder_list_expanded);
                    hideFragment(mFragmentSummary);
                    mFragmentVideoList.setFullScreen(false);
                    break;
                case VIDEO:
                    mTextTitle.setVisibility(View.GONE);
                    changeFragmentWidth(mFragmentFolderList, R.dimen.width_folder_list_contracted);
                    mFragmentVideoList.setFullScreen(true);
                    break;
            }
        }
    }

    private void hideFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    private void changeFragmentWidth(Fragment fragment, @DimenRes int widthResId){
        ViewGroup.LayoutParams params = fragment.getView().getLayoutParams();
        params.width = getResources().getDimensionPixelOffset(widthResId);
        fragment.getView().setLayoutParams(params);
    }

    private void showDetails(Video video){
        startActivity(DetailsActivity.getIntent(getBaseContext(), video, mFragmentVideoList.getVideos()));
    }

    private void populate(Video current, ArrayList<Video> videos) {
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

        mTextTitle.setText(current.getTitle());

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


        if (videos == null || videos.isEmpty()) {
            hideFragment(mFragmentFolderList);
            fragmentIsVisible = false;
        } else {
            showFragment(mFragmentFolderList);
            mFragmentFolderList.setVideos(videos);
            fragmentIsVisible = true;
        }

        return fragmentIsVisible;
    }
}

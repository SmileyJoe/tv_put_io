package io.smileyjoe.putio.tv.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;
import io.smileyjoe.putio.tv.ui.fragment.FilterFragment;
import io.smileyjoe.putio.tv.ui.fragment.GenreListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.util.VideoLoader;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements VideoLoader.Listener {

    private TextView mTextTitle;

    private VideoListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;
    private GenreListFragment mFragmentGenreList;
    private FilterFragment mFragmentFilter;

    private LinearLayout mLayoutLists;
    private ProgressBar mProgressLoading;

    private FragmentType mVideoTypeFocus = FragmentType.UNKNOWN;
    private VideoLoader mVideoLoader;

    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTitle = findViewById(R.id.text_title);
        mLayoutLists = findViewById(R.id.layout_lists);
        mProgressLoading = findViewById(R.id.progress_loading);

        mFragmentFolderList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentFolderList.setType(VideoListAdapter.Type.LIST, FragmentType.FOLDER);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(VideoListAdapter.Type.GRID, FragmentType.VIDEO);
        mFragmentGenreList = (GenreListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_genre_list);
        mFragmentFilter = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filter);
        mFragmentFilter.setListener(new FilterListener());

        mFragmentVideoList.setListener(new VideoListListener());
        mFragmentFolderList.setListener(new FolderListListener());
        mFragmentGenreList.setListener(new GenreListListener());

        mVideoLoader = new VideoLoader(getBaseContext(), this);
        mVideoLoader.load();

        hideFragment(mFragmentGenreList);

        // todo: this needs to be called when an id is not found in the db //
        Tmdb.updateMovieGenres(getBaseContext());
    }

    @Override
    public void onBackPressed() {
        boolean hasHistory = mVideoLoader.back();
        mFragmentVideoList.hideDetails();

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
            switch (video.getVideoType()) {
                case EPISODE:
                case MOVIE:
                    videosSorted.add(video);
                    break;
                case UNKNOWN:
                    folders.add(video);
                    break;
            }
        }

        if((folders == null || folders.isEmpty()) && (videosSorted != null && videosSorted.size() == 1)){
            showDetails(videosSorted.get(0));
        } else {
            mVideoLoader.addToHistory(current);
            handleGenres(videosSorted);
            mTextTitle.setText(current.getTitle());

            boolean folderFragmentIsVisible = populateFolders(folders);

            mFragmentVideoList.setFullScreen(!folderFragmentIsVisible);
            mFragmentVideoList.setVideos(videosSorted);
            mFragmentFilter.reset();

            if(videosSorted != null && !videosSorted.isEmpty()){
                showFragment(mFragmentFilter);
            } else {
                hideFragment(mFragmentFilter);
            }
        }

        mLayoutLists.setVisibility(View.VISIBLE);
        mProgressLoading.setVisibility(View.GONE);
    }

    private void handleGenres(ArrayList<Video> videos){
        Set<Integer> temp = new HashSet<>();

        for(Video video:videos){
            ArrayList<Integer> genres = video.getGenreIds();

            if(genres != null && !genres.isEmpty()){
                temp.addAll(genres);
            }
        }

        ArrayList<Integer> genresAvailable = new ArrayList<>(temp);

        if(genresAvailable == null || genresAvailable.isEmpty()){
            hideFragment(mFragmentGenreList);
        } else {
            showFragment(mFragmentGenreList);
            mFragmentGenreList.setGenreIds(genresAvailable);
        }
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

    private void showFolders(){
        mTextTitle.setVisibility(View.VISIBLE);
        changeFragmentWidth(mFragmentFolderList, R.dimen.home_fragment_width_expanded);
        mFragmentVideoList.setFullScreen(false);
        mFragmentVideoList.hideDetails();
    }

    private void hideFolders(){
        mTextTitle.setVisibility(View.GONE);
        changeFragmentWidth(mFragmentFolderList, R.dimen.home_fragment_width_contracted);
        mFragmentVideoList.setFullScreen(true);
    }

    private class FilterListener implements FilterFragment.Listener{
        @Override
        public void onItemClicked(View view, Filter filter, boolean isSelected) {
            mFragmentVideoList.filter(filter, isSelected);
        }

        @Override
        public void hasFocus(FragmentType type, Filter item, View view, int position) {
            mFragmentVideoList.hideDetails();
        }
    }

    private class GenreListListener implements GenreListFragment.Listener{
        private int mSelectedGenre = -1;

        @Override
        public void onItemClicked(View view, Genre genre) {
            if(mSelectedGenre == genre.getId()){
                mSelectedGenre = -1;
            } else {
                mSelectedGenre = genre.getId();
            }
            mFragmentVideoList.filterByGenre(mSelectedGenre);
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Genre genre, View view, int position) {
            if(mVideoTypeFocus != fragmentType) {
                mVideoTypeFocus = fragmentType;
                mFragmentVideoList.hideDetails();
                hideFolders();
            }
        }
    }

    private class FolderListListener implements VideoListFragment.Listener{
        @Override
        public void onItemClicked(View view, Video video) {
            mVideoLoader.load(video);
            mFragmentGenreList.clearSelected();
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Video item, View view, int position) {
            if(mVideoTypeFocus != fragmentType){
                mVideoTypeFocus = fragmentType;

                showFolders();
            }
        }
    }

    private class VideoListListener implements VideoListFragment.Listener{
        @Override
        public void onItemClicked(View view, Video video) {
            switch (video.getFileType()){
                case VIDEO:
                    showDetails(video);
                    break;
                case FOLDER:
                    mVideoLoader.load(video);
                    break;
            }
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Video video, View view, int position) {
            if(mVideoTypeFocus != fragmentType){
                mVideoTypeFocus = fragmentType;
                hideFolders();
            }
        }
    }
}

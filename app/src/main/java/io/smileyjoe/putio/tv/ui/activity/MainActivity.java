package io.smileyjoe.putio.tv.ui.activity;

import android.os.Bundle;
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
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;
import io.smileyjoe.putio.tv.ui.fragment.GenreListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.util.VideoLoader;

/*
 * Main Activity class that loads {@link MainFragment}.
 *
 *
 * - Zoom limit summary to half height
 * - Selected text to black
 * - Watched triangle to selected colour on zoom view
 * - genres at the top
 * - filters on the right panel where genres are
 * - folder list dark get background for select
 * - folder list highlight text image primary color
 * - Back option in list of folders
 * - Grid title set to 2
 * - app logo on empty
 * - folder list show inside count
 * - different folder icons that the user can set
 * - Folder list extra details
 * - Filter options in list with sub text
 */
public class MainActivity extends FragmentActivity implements VideoLoader.Listener {

    private TextView mTextTitle;

    private VideoListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;
    private GenreListFragment mFragmentGenreList;

    private LinearLayout mLayoutLists;
    private ProgressBar mProgressLoading;

    private FragmentType mVideoTypeFocus = FragmentType.UNKNOWN;
    private VideoLoader mVideoLoader;

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

        handleGenres(videosSorted);

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

    private class GenreListListener implements GenreListFragment.Listener{
        @Override
        public void onItemClicked(Genre genre) {
            mFragmentVideoList.filterByGenre(genre.getId());
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Genre genre, View view, int position) {
            if(mVideoTypeFocus != fragmentType) {
                mVideoTypeFocus = fragmentType;
                changeFragmentWidth(mFragmentGenreList, R.dimen.home_fragment_width_expanded);
                mFragmentVideoList.hideDetails();
            }
        }
    }

    private class FolderListListener implements VideoListFragment.Listener{
        @Override
        public void onItemClicked(Video video) {
            mVideoLoader.load(video);
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Video item, View view, int position) {
            if(mVideoTypeFocus != fragmentType){
                mVideoTypeFocus = fragmentType;

                mTextTitle.setVisibility(View.VISIBLE);
                changeFragmentWidth(mFragmentFolderList, R.dimen.home_fragment_width_expanded);
                mFragmentVideoList.setFullScreen(false);
                changeFragmentWidth(mFragmentGenreList, R.dimen.home_fragment_width_contracted);
                mFragmentVideoList.hideDetails();
            }
        }
    }

    private class VideoListListener implements VideoListFragment.Listener{
        @Override
        public void onItemClicked(Video video) {
            showDetails(video);
        }

        @Override
        public void hasFocus(FragmentType fragmentType, Video video, View view, int position) {

            if(mVideoTypeFocus != fragmentType){
                mVideoTypeFocus = fragmentType;
                mTextTitle.setVisibility(View.GONE);
                changeFragmentWidth(mFragmentFolderList, R.dimen.home_fragment_width_contracted);
                mFragmentVideoList.setFullScreen(true);
                changeFragmentWidth(mFragmentGenreList, R.dimen.home_fragment_width_contracted);
            }
        }
    }
}

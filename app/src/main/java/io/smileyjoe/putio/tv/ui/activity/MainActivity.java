package io.smileyjoe.putio.tv.ui.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;
import io.smileyjoe.putio.tv.ui.fragment.GenreListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoListFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideoSummaryFragment;
import io.smileyjoe.putio.tv.util.VideoLoader;
import io.smileyjoe.putio.tv.util.VideoUtil;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity implements VideoLoader.Listener {

    private TextView mTextTitle;

    private VideoListFragment mFragmentFolderList;
    private VideoListFragment mFragmentVideoList;
    private VideoSummaryFragment mFragmentSummary;
    private GenreListFragment mFragmentGenreList;

    private LinearLayout mLayoutLists;
    private ProgressBar mProgressLoading;
    private RelativeLayout mLayoutLargeVideo;

    private FragmentType mVideoTypeFocus = FragmentType.UNKNOWN;
    private VideoLoader mVideoLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTitle = findViewById(R.id.text_title);
        mLayoutLists = findViewById(R.id.layout_lists);
        mProgressLoading = findViewById(R.id.progress_loading);
        mLayoutLargeVideo = findViewById(R.id.layout_large_video);

        mFragmentFolderList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentFolderList.setType(VideoListAdapter.Type.LIST, FragmentType.FOLDER);
        mFragmentVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentVideoList.setType(VideoListAdapter.Type.GRID, FragmentType.VIDEO);
        mFragmentSummary = (VideoSummaryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_summary);
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
        hideLargeVideo();

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

    private void moveSummaryFragment(int rule){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFragmentSummary.getView().getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(rule);
        mFragmentSummary.getView().setLayoutParams(params);
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

    private void hideLargeVideo(){
        mLayoutLargeVideo.setVisibility(View.INVISIBLE);
    }

    private void displayLargeVideo(Video video, View smallView){
        float x = smallView.getX();
        float y = smallView.getY();
        float height = smallView.getHeight();
        float width = smallView.getWidth();
        float centerX = x + width/2;
        float centerY = y + height/2;
        float largeWidth = mLayoutLargeVideo.getWidth();
        float largeHeight = mLayoutLargeVideo.getHeight();
        float largeCenterX = centerX - (largeWidth/2);
        float largeCenterY = centerY - (largeHeight/2);

        if(largeCenterY < 0){
            largeCenterY = 0;
        } else if((largeCenterY + largeHeight) > mFragmentVideoList.getHeight()){
            largeCenterY = mFragmentVideoList.getHeight() - largeHeight;
        }

        if(largeCenterX < 0){
            largeCenterX = 0;
        } else if((largeCenterX + largeWidth) > mFragmentVideoList.getWidth()){
            largeCenterX = mFragmentVideoList.getWidth() - largeWidth;
        }

        mLayoutLargeVideo.setElevation(10);

        Glide.with(getBaseContext())
                .load(video.getPosterAsUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into((ImageView) mLayoutLargeVideo.findViewById(R.id.image_poster));

        TextView textSummary = mLayoutLargeVideo.findViewById(R.id.text_summary);
        TextView textTitle = mLayoutLargeVideo.findViewById(R.id.text_title);
        textTitle.setText(video.getTitle());
        textTitle.setTypeface(null, Typeface.BOLD);
        textSummary.setText(video.getOverView());
        textSummary.setVisibility(View.VISIBLE);

        mLayoutLargeVideo.setX(largeCenterX);
        mLayoutLargeVideo.setY(largeCenterY);
        mLayoutLargeVideo.setVisibility(View.VISIBLE);
        Log.d("FocusThings", centerX + " : " + centerY + " : " + largeCenterX + " : " + largeCenterY);
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
                changeFragmentWidth(mFragmentGenreList, R.dimen.width_folder_list_expanded);
                hideFragment(mFragmentSummary);
                hideLargeVideo();
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
                changeFragmentWidth(mFragmentFolderList, R.dimen.width_folder_list_expanded);
                hideFragment(mFragmentSummary);
                mFragmentVideoList.setFullScreen(false);
                changeFragmentWidth(mFragmentGenreList, R.dimen.width_folder_list_contracted);
                hideLargeVideo();
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
            displayLargeVideo(video, view);

//            if (video.isTmdbFound()) {
//                showFragment(mFragmentSummary);
//                mFragmentSummary.setVideo(video);
//
//                int topRow = mFragmentVideoList.getFirstVisiblePosition()/7;
//                int selectedRow = position/7;
//
//                if((selectedRow - topRow >= 2)){
//                    moveSummaryFragment(RelativeLayout.ALIGN_PARENT_TOP);
//                } else {
//                    moveSummaryFragment(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                }
//            } else {
//                hideFragment(mFragmentSummary);
//            }

            if(mVideoTypeFocus != fragmentType){
                mVideoTypeFocus = fragmentType;
                mTextTitle.setVisibility(View.GONE);
                changeFragmentWidth(mFragmentFolderList, R.dimen.width_folder_list_contracted);
                mFragmentVideoList.setFullScreen(true);
                changeFragmentWidth(mFragmentGenreList, R.dimen.width_folder_list_contracted);
            }
        }
    }
}

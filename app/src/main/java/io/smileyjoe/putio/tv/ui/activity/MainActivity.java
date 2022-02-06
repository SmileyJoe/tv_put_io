package io.smileyjoe.putio.tv.ui.activity;

import static android.view.View.FOCUS_DOWN;
import static android.view.View.FOCUS_LEFT;
import static android.view.View.FOCUS_RIGHT;
import static android.view.View.FOCUS_UP;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.ActivityMainBinding;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Directory;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FolderType;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.fragment.BaseFragment;
import io.smileyjoe.putio.tv.ui.fragment.FilterFragment;
import io.smileyjoe.putio.tv.ui.fragment.FolderListFragment;
import io.smileyjoe.putio.tv.ui.fragment.GenreListFragment;
import io.smileyjoe.putio.tv.ui.fragment.GroupFragment;
import io.smileyjoe.putio.tv.ui.fragment.ToggleFragment;
import io.smileyjoe.putio.tv.ui.fragment.VideosFragment;
import io.smileyjoe.putio.tv.util.FragmentUtil;
import io.smileyjoe.putio.tv.util.VideoLoader;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements VideoLoader.Listener, BaseFragment.OnFocusSearchListener {

    private FolderListFragment mFragmentFolderList;
    private VideosFragment mFragmentVideoList;
    private GenreListFragment mFragmentGenreList;
    private FilterFragment mFragmentFilter;
    private GroupFragment mFragmentGroup;

    private FragmentType mVideoTypeFocus = FragmentType.UNKNOWN;
    private VideoLoader mVideoLoader;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentFolderList = (FolderListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_folder_list);
        mFragmentVideoList = (VideosFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video_list);
        mFragmentGenreList = (GenreListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_genre_list);
        mFragmentFilter = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filter);
        mFragmentGroup = (GroupFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_groups);

        mFragmentVideoList.setListener(new VideoListListener());
        mFragmentFolderList.setListener(new FolderListListener());
        mFragmentGenreList.setListener(new GenreListListener());
        mFragmentFilter.setListener(new FilterListener());
        mFragmentGroup.setListener(new GroupListener());

        mFragmentFolderList.setFocusSearchListener(this);
        mFragmentVideoList.setFocusSearchListener(this);
        mFragmentGenreList.setFocusSearchListener(this);
        mFragmentFilter.setFocusSearchListener(this);
        mFragmentGroup.setFocusSearchListener(this);

        mVideoLoader = VideoLoader.getInstance(getApplicationContext(), this);
        mVideoLoader.loadDirectory();

        mView.layoutShowFolders.setOnClickListener(v -> toggleFolders());

        FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentGenreList);

        // todo: this needs to be called when an id is not found in the db //
        Tmdb.Genre.update(getBaseContext());
    }

    @Override
    protected ActivityMainBinding inflate() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onBackPressed() {
        if(mView.layoutFolders.getVisibility() == View.VISIBLE && mFragmentVideoList.hasVideos()){
            hideFolders();
        } else {
            boolean hasHistory = mVideoLoader.back();
            mFragmentVideoList.hideDetails();

            if (!hasHistory) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mVideoLoader != null) {
            mVideoLoader.setListener(this);
        }
    }

    @Override
    public void onVideosLoadStarted() {
        mView.frameLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideosLoadFinished(HistoryItem historyItem, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory) {
        populate(historyItem, videos, folders, shouldAddToHistory);
    }

    @Override
    public void update(Video video) {
        mFragmentVideoList.update(video);
    }

    private void showDetails(Video video) {
        if (video.isTmdbFound()) {
            startActivity(VideoDetailsBackdropActivity.getIntent(getBaseContext(), video));
        } else {
            startActivity(VideoDetailsActivity.getIntent(getBaseContext(), video, mFragmentVideoList.getVideos()));
        }
    }

    private void populate(HistoryItem historyItem, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory) {
        if ((folders == null || folders.isEmpty()) && (videos != null && videos.size() == 1) && historyItem.getFolderType() == FolderType.DIRECTORY) {
            showDetails(videos.get(0));
        } else {
            if (shouldAddToHistory) {
                mVideoLoader.addToHistory(historyItem);
            }
            handleGenres(videos);

            mView.textTitleFolders.setText(historyItem.getTitle());

            mFragmentFolderList.setFolders(folders);
            mFragmentVideoList.hideDetails();
            mFragmentVideoList.setVideos(videos);
            mFragmentFilter.reset();

            if (videos != null && !videos.isEmpty()) {
                FragmentUtil.showFragment(getSupportFragmentManager(), mFragmentFilter);
            } else {
                FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentFilter);
            }

            switch (historyItem.getFolderType()) {
                case GROUP:
                    FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentGroup);
                    break;
                case DIRECTORY:
                    if (mVideoLoader.hasHistory()) {
                        FragmentUtil.showFragment(getSupportFragmentManager(), mFragmentGroup);
                        mFragmentGroup.setCurrentPutId(historyItem.getId());
                    } else {
                        FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentGroup);
                    }
                    break;
            }
        }

        if(videos != null && !videos.isEmpty()) {
            hideFolders();
        } else {
            showFolders();
        }
        mView.frameLoading.setVisibility(View.GONE);
    }

    private void handleGenres(ArrayList<Video> videos) {
        ArrayList<Integer> genresAvailable = videos.stream()
                .filter(video -> {
                    ArrayList<Integer> genreIds = video.getGenreIds();
                    return genreIds != null && !genreIds.isEmpty();
                })
                .map(video -> video.getGenreIds())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        if (genresAvailable == null || genresAvailable.isEmpty()) {
            FragmentUtil.hideFragment(getSupportFragmentManager(), mFragmentGenreList);
        } else {
            FragmentUtil.showFragment(getSupportFragmentManager(), mFragmentGenreList);
            mFragmentGenreList.setGenreIds(genresAvailable);
        }
    }

    private void toggleFolders(){
        if(mView.layoutFolders.getVisibility() == View.GONE){
            showFolders();
        } else {
            hideFolders();
        }
    }

    private void showFolders() {
        mView.layoutFolders.setVisibility(View.VISIBLE);
        mFragmentVideoList.hideDetails();
    }

    private void hideFolders() {
        mView.layoutFolders.setVisibility(View.GONE);
    }

    @Override
    public View onFocusSearch(View focused, int direction, FragmentType type) {
        switch (type){
            case GROUP:
                switch (direction){
                    case FOCUS_UP:
                        return focused;
                    case FOCUS_DOWN:
                        return mFragmentFolderList.getFocusableView();
                    case FOCUS_RIGHT:
                    case FOCUS_LEFT:
                        if(mFragmentGroup.canFocus(focused, direction)){
                            return null;
                        } else {
                            return focused;
                        }
                }
            case FOLDER:
                if(direction == FOCUS_UP){
                    return mFragmentGroup.getFocusableView();
                } else {
                    return focused;
                }
            case VIDEO:
                if(direction == FOCUS_UP){
                    return null;
                } else {
                    return focused;
                }
            default:
                return null;
        }
    }

    private class GroupListener extends HomeListener<Group> implements ToggleFragment.Listener<Group> {
        @Override
        public void onItemClicked(View view, Group group, boolean isSelected) {
            UpdateGroup task = new UpdateGroup(group, isSelected);
            task.execute();
        }

        private class UpdateGroup extends AsyncTask<Void, Void, Void> {
            private Group mGroup;
            private boolean mIsSelected;

            public UpdateGroup(Group group, boolean isSelected) {
                mGroup = group;
                mIsSelected = isSelected;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (mIsSelected) {
                    mGroup.addPutId(mVideoLoader.getCurrentHistory().getId());
                } else {
                    mGroup.removePutId(mVideoLoader.getCurrentHistory().getId());
                }

                AppDatabase.getInstance(getBaseContext()).groupDao().insert(mGroup);
                return null;
            }
        }
    }

    private class FilterListener extends HomeListener<Filter> implements ToggleFragment.Listener<Filter> {
        @Override
        public void onItemClicked(View view, Filter filter, boolean isSelected) {
            mFragmentVideoList.filter(filter, isSelected);
        }
    }

    private class GenreListListener extends HomeListener<Genre> implements GenreListFragment.Listener {
        private int mSelectedGenre = -1;

        @Override
        public void onItemClicked(View view, Genre genre) {
            if (mSelectedGenre == genre.getId()) {
                mSelectedGenre = -1;
            } else {
                mSelectedGenre = genre.getId();
            }
            mFragmentVideoList.filterByGenre(mSelectedGenre);
        }
    }

    private class FolderListListener extends HomeListener<Folder> implements FolderListFragment.Listener {
        @Override
        public void onItemClicked(View view, Folder folder) {
            switch (folder.getFolderType()) {
                case DIRECTORY:
                    Directory directory = (Directory) folder;
                    mVideoLoader.loadDirectory(directory.getPutId(), directory.getTitle());
                    break;
                case GROUP:
                    mVideoLoader.loadGroup(((Group) folder).getId());
                    break;
            }
            mFragmentGenreList.clearSelected();
        }
    }

    private class VideoListListener extends HomeListener<Video> implements VideosFragment.Listener {
        @Override
        public void onItemClicked(View view, Video video) {
            switch (video.getFileType()) {
                case VIDEO:
                    showDetails(video);
                    break;
                case FOLDER:
                    if (video.getVideoType() == VideoType.SEASON) {
                        startActivity(SeriesActivity.getIntent(getBaseContext(), video));
                    } else {
                        mVideoLoader.loadDirectory(video.getPutId(), video.getTitle());
                    }
                    break;
            }
        }
    }

    private abstract class HomeListener<T> implements HomeFragmentListener<T> {

        @Override
        public void hasFocus(FragmentType type, T item, View view, int position) {
            if (mVideoTypeFocus != type) {
                mVideoTypeFocus = type;

                if(type != FragmentType.VIDEO){
                    mFragmentVideoList.hideDetails();
                }
            }
        }
    }
}

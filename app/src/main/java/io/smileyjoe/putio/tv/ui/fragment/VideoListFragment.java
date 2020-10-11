package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;
import io.smileyjoe.putio.tv.ui.view.ZoomGridVideo;
import io.smileyjoe.putio.tv.util.VideoUtil;

public class VideoListFragment extends Fragment {

    public interface Listener extends VideoListAdapter.Listener<Video> {
    }

    private RecyclerView mRecycler;
    private LinearLayout mLayoutEmpty;
    private ProgressBar mProgressLoading;
    private ZoomGridVideo mZoomGridVideo;

    private VideoListAdapter mVideoListAdapter;
    private VideoListAdapter.Type mType = VideoListAdapter.Type.LIST;
    private boolean mIsFullScreen = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Video> mVideosAll;
    private ArrayList<Filter> mAppliedFilters = new ArrayList<>();
    private Integer mAppliedGenreId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_list, null);

        mRecycler = view.findViewById(R.id.recycler);
        mLayoutEmpty = view.findViewById(R.id.layout_empty);
        mProgressLoading = view.findViewById(R.id.progress_loading);
        mZoomGridVideo = view.findViewById(R.id.zoom_grid_video);

        return view;
    }

    public void setType(VideoListAdapter.Type type, FragmentType fragmentType) {
        mType = type;

        if(mVideoListAdapter != null){
            mVideoListAdapter.setType(type);
            mVideoListAdapter.setFragmentType(fragmentType);
        }

        setLayoutManager(true);
    }

    public void setFullScreen(boolean fullScreen) {
        if(mIsFullScreen != fullScreen) {
            mIsFullScreen = fullScreen;
            mZoomGridVideo.reset();
            boolean created = setLayoutManager(false);

            if (!created && mLayoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) mLayoutManager).setSpanCount(getSpanCount());
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoListAdapter = new VideoListAdapter(getContext(), mType);

        mRecycler.setAdapter(mVideoListAdapter);
        setLayoutManager(false);
    }

    public void hideDetails(){
        mZoomGridVideo.hide();
    }

    public void setListener(Listener listener) {
        mVideoListAdapter.setListener(new AdapterListener(listener));
    }

    public void update(Video video){
        if(mVideoListAdapter != null){
            mVideoListAdapter.update(video);
        }
    }

    private int getSpanCount(){
        int spanCount;

        if (mIsFullScreen) {
            spanCount = 7;
        } else {
            spanCount = 4;
        }

        return spanCount;
    }

    public int getFirstVisiblePosition(){
        if(mType == VideoListAdapter.Type.GRID) {
            return ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        return 0;
    }

    private boolean setLayoutManager(boolean force){
        boolean created = false;
        if(mRecycler != null) {
            if(mLayoutManager == null || force) {
                switch (mType) {
                    case LIST:
                        mLayoutManager = new LinearLayoutManager(getContext());
                        break;
                    case GRID:
                        mLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                        break;
                }

                mRecycler.setLayoutManager(mLayoutManager);
                created = true;
            }
        }
        return created;
    }

    public void startLoading(){
        mProgressLoading.setVisibility(View.VISIBLE);
        mLayoutEmpty.setVisibility(View.GONE);
        mRecycler.setVisibility(View.GONE);
    }

    public void setVideos(ArrayList<Video> videos) {
        mAppliedGenreId = -1;
        mAppliedFilters = new ArrayList<>();
        mVideosAll = videos;
        populate();
    }

    private void populate(){
        ArrayList<Video> videos = applyFilters();
        mProgressLoading.setVisibility(View.GONE);
        if (videos == null || videos.isEmpty()) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);

            mVideoListAdapter.setItems(videos);
            mVideoListAdapter.notifyDataSetChanged();
        }
    }

    public void filter(Filter filter, boolean isSelected){
        if(!isSelected){
            mAppliedFilters.remove(filter);
        } else {
            mAppliedFilters.add(filter);
        }

        populate();
    }

    private ArrayList<Video> applyFilters(){
        ArrayList<Video> filtered = new ArrayList<>();

        for(Video video:mVideosAll){
            boolean includeVideo = true;

            if(mAppliedGenreId > 0){
                ArrayList<Integer> genreIds = video.getGenreIds();
                if(genreIds == null || !genreIds.contains(mAppliedGenreId)){
                    includeVideo = false;
                }
            }

            if(includeVideo && mAppliedFilters != null && !mAppliedFilters.isEmpty()){
                for(Filter filter:mAppliedFilters){
                    switch (filter){
                        case SHOW_WATCHED:
                            if(video.isWatched()){
                                includeVideo = false;
                            }
                            break;
                    }
                }
            }

            if(includeVideo){
                filtered.add(video);
            }
        }

        if(mAppliedFilters.contains(Filter.SORT_CREATED)) {
            VideoUtil.sort(filtered, Filter.SORT_CREATED);
        } else {
            VideoUtil.sort(filtered);
        }

        return filtered;
    }

    public void filterByGenre(Integer genreId){
        if(mAppliedGenreId != genreId) {
            mAppliedGenreId = genreId;
            populate();
        }
    }

    public ArrayList<Video> getVideos(){
        if(mVideoListAdapter != null){
            return mVideoListAdapter.getItems();
        }

        return null;
    }

    public float getHeight(){
        return getView().getHeight();
    }

    public float getWidth(){
        return getView().getWidth();
    }

    private class AdapterListener implements VideoListAdapter.Listener<Video>{
        private Listener mListener;

        public AdapterListener(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onItemClicked(View view, Video item) {
            if(mLayoutManager != null){
                mListener.onItemClicked(view, item);
            }
        }

        @Override
        public void hasFocus(FragmentType type, Video item, View view, int position) {
            if(mType == VideoListAdapter.Type.GRID){
                mZoomGridVideo.show(view, item);
            }

            if(mListener != null){
                mListener.hasFocus(type, item, view, position);
            }
        }
    }
}

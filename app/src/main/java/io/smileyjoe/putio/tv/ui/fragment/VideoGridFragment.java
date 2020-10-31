package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideoGridAdapter;
import io.smileyjoe.putio.tv.ui.view.ZoomGridVideo;
import io.smileyjoe.putio.tv.util.VideoUtil;

public class VideoGridFragment extends Fragment {

    public interface Listener extends VideoGridAdapter.Listener<Video> {
    }

    private RecyclerView mRecycler;
    private LinearLayout mLayoutEmpty;
    private ZoomGridVideo mZoomGridVideo;

    private VideoGridAdapter mVideoGridAdapter;
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
        mZoomGridVideo = view.findViewById(R.id.zoom_grid_video);

        return view;
    }

    public void setType(FragmentType fragmentType) {

        if(mVideoGridAdapter != null){
            mVideoGridAdapter.setFragmentType(fragmentType);
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
        mVideoGridAdapter = new VideoGridAdapter(getContext());

        mRecycler.setAdapter(mVideoGridAdapter);
        setLayoutManager(false);
    }

    public void hideDetails(){
        mZoomGridVideo.hide();
    }

    public void setListener(Listener listener) {
        mVideoGridAdapter.setListener(new AdapterListener(listener));
    }

    public void update(Video video){
        if(mVideoGridAdapter != null){
            mVideoGridAdapter.update(video);
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

    private boolean setLayoutManager(boolean force){
        boolean created = false;
        if(mRecycler != null) {
            if(mLayoutManager == null || force) {
                mLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                mRecycler.setLayoutManager(mLayoutManager);
                created = true;
            }
        }
        return created;
    }

    public void setVideos(ArrayList<Video> videos) {
        mAppliedGenreId = -1;
        mAppliedFilters = new ArrayList<>();
        mVideosAll = videos;
        populate();
    }

    private void populate(){
        ArrayList<Video> videos = applyFilters();

        if (videos == null || videos.isEmpty()) {
            mLayoutEmpty.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mLayoutEmpty.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);

            mVideoGridAdapter.setItems(videos);
            mVideoGridAdapter.notifyDataSetChanged();
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
        if(mVideoGridAdapter != null){
            return mVideoGridAdapter.getItems();
        }

        return null;
    }

    public float getHeight(){
        return getView().getHeight();
    }

    public float getWidth(){
        return getView().getWidth();
    }

    private class AdapterListener implements VideoGridAdapter.Listener<Video>{
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
            mZoomGridVideo.show(view, item);

            if(mListener != null){
                mListener.hasFocus(type, item, view, position);
            }
        }
    }
}

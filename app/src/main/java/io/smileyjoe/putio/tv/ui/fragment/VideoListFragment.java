package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;

public class VideoListFragment extends Fragment {

    public interface Listener extends VideoListAdapter.Listener<Video> {
    }

    private RecyclerView mRecycler;
    private TextView mTextEmpty;
    private ProgressBar mProgressLoading;

    private VideoListAdapter mVideoListAdapter;
    private VideoListAdapter.Type mType = VideoListAdapter.Type.LIST;
    private boolean mIsFullScreen = false;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_list, null);

        mRecycler = view.findViewById(R.id.recycler);
        mTextEmpty = view.findViewById(R.id.text_empty);
        mProgressLoading = view.findViewById(R.id.progress_loading);

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
        mIsFullScreen = fullScreen;
        boolean created = setLayoutManager(false);

        if(!created && mLayoutManager instanceof GridLayoutManager){
            ((GridLayoutManager) mLayoutManager).setSpanCount(getSpanCount());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoListAdapter = new VideoListAdapter(getContext(), mType);

        mRecycler.setAdapter(mVideoListAdapter);
        setLayoutManager(false);
    }

    public void setListener(Listener listener) {
        mVideoListAdapter.setListener(listener);
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
        mTextEmpty.setVisibility(View.GONE);
        mRecycler.setVisibility(View.GONE);
    }

    public void setVideos(ArrayList<Video> videos) {
        mProgressLoading.setVisibility(View.GONE);
        if (videos == null || videos.isEmpty()) {
            mTextEmpty.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);

            mVideoListAdapter.setItems(videos);
            mVideoListAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<Video> getVideos(){
        if(mVideoListAdapter != null){
            return mVideoListAdapter.getItems();
        }

        return null;
    }
}

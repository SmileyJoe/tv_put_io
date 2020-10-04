package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;

public class VideoListFragment extends Fragment {

    public interface Listener extends VideoListAdapter.Listener {
    }

    private RecyclerView mRecycler;
    private TextView mTextEmpty;
    private ProgressBar mProgressLoading;

    private VideoListAdapter mVideoListAdapter;
    private Listener mListener;
    private VideoListAdapter.Type mType = VideoListAdapter.Type.LIST;
    private boolean mIsFullScreen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_list, null);

        mRecycler = view.findViewById(R.id.recycler);
        mTextEmpty = view.findViewById(R.id.text_empty);
        mProgressLoading = view.findViewById(R.id.progress_loading);

        return view;
    }

    public void setType(VideoListAdapter.Type type) {
        mType = type;

        if(mVideoListAdapter != null){
            mVideoListAdapter.setType(type);
        }

        setLayoutManager();
    }

    public void setFullScreen(boolean fullScreen) {
        mIsFullScreen = fullScreen;
        setLayoutManager();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof Listener) {
            mListener = (Listener) getActivity();
        }

        mVideoListAdapter = new VideoListAdapter(getContext(), mType);
        mVideoListAdapter.setListener(mListener);

        mRecycler.setAdapter(mVideoListAdapter);
        setLayoutManager();
    }

    private void setLayoutManager(){
        if(mRecycler != null) {
            switch (mType) {
                case LIST:
                    mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    break;
                case GRID:
                    int spanCount;

                    if(mIsFullScreen){
                        spanCount = 5;
                    } else {
                        spanCount = 3;
                    }
                    mRecycler.setLayoutManager(new GridLayoutManager(getContext(),spanCount));
                    break;
            }
        }
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

            mVideoListAdapter.setVideos(videos);
            mVideoListAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<Video> getVideos(){
        if(mVideoListAdapter != null){
            return mVideoListAdapter.getVideos();
        }

        return null;
    }
}

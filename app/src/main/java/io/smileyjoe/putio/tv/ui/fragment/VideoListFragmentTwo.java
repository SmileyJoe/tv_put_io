package io.smileyjoe.putio.tv.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.smileyjoe.putio.tv.ui.adapter.FolderListAdapter;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapterTwo;

public class VideoListFragmentTwo extends Fragment {

    public interface Listener extends VideoListAdapterTwo.Listener {
    }

    private RecyclerView mRecycler;
    private TextView mTextEmpty;

    private VideoListAdapterTwo mVideoListAdapter;
    private Listener mListener;
    private VideoListAdapterTwo.Type mType = VideoListAdapterTwo.Type.LIST;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_list_two, null);

        mRecycler = view.findViewById(R.id.recycler);
        mTextEmpty = view.findViewById(R.id.text_empty);

        return view;
    }

    public void setType(VideoListAdapterTwo.Type type) {
        mType = type;

        if(mVideoListAdapter != null){
            mVideoListAdapter.setType(type);
        }

        setLayoutManager();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof Listener) {
            mListener = (Listener) getActivity();
        }

        mVideoListAdapter = new VideoListAdapterTwo(getContext(), mType);
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
                    mRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
                    break;
            }
        }
    }

    public void setVideos(ArrayList<Video> videos) {
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

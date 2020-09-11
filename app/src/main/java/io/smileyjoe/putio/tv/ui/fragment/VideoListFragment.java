package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;
import io.smileyjoe.putio.tv.ui.adapter.FileSelectedListener;
import io.smileyjoe.putio.tv.ui.adapter.VideoAdapter;

public class VideoListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    public interface Listener{
        void onVideoClicked(File file, ArrayList<File> relatedVideos);
    }

    private GridView mGridVideos;
    private TextView mTextEmpty;
    private VideoAdapter mVideoAdapter;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_list, null);

        mGridVideos = view.findViewById(R.id.grid_videos);
        mTextEmpty = view.findViewById(R.id.text_empty);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVideoAdapter = new VideoAdapter(getContext());

        mGridVideos.setAdapter(mVideoAdapter);
        mGridVideos.setOnItemClickListener(this);
        mGridVideos.setOnItemSelectedListener(new FileSelectedListener(mVideoAdapter));

        if(getActivity() instanceof Listener){
            mListener = (Listener) getActivity();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListener != null){
            mListener.onVideoClicked(mVideoAdapter.getItem(position), mVideoAdapter.getFiles());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mVideoAdapter.setSelectedPosition(position);
        mVideoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mVideoAdapter.setSelectedPosition(-1);
        mVideoAdapter.notifyDataSetChanged();
    }

    public void setVideos(ArrayList<File> files){
        if(files == null || files.isEmpty()){
            mTextEmpty.setVisibility(View.VISIBLE);
            mGridVideos.setVisibility(View.GONE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
            mGridVideos.setVisibility(View.VISIBLE);

            mVideoAdapter.setFiles(files);
            mVideoAdapter.notifyDataSetChanged();
        }
    }
}

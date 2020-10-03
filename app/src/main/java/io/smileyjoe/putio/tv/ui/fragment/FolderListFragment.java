package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.FolderListAdapter;
import io.smileyjoe.putio.tv.ui.viewholder.FolderListViewHolder;

public class FolderListFragment extends Fragment {

    public interface Listener extends FolderListAdapter.Listener{}

    private TextView mTextEmpty;
    private RecyclerView mRecycleFolders;

    private FolderListAdapter mFolderAdapter;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, null);

        mTextEmpty = view.findViewById(R.id.text_empty);
        mRecycleFolders = view.findViewById(R.id.recycle_folders);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() instanceof Listener){
            mListener = (Listener) getActivity();
        }

        mFolderAdapter = new FolderListAdapter(getContext());
        mFolderAdapter.setListener(mListener);

        mRecycleFolders.setAdapter(mFolderAdapter);
        mRecycleFolders.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public void setVideos(ArrayList<Video> videos){
        if(videos == null || videos.isEmpty()){
            mTextEmpty.setVisibility(View.VISIBLE);
            mRecycleFolders.setVisibility(View.GONE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
            mRecycleFolders.setVisibility(View.VISIBLE);

            mFolderAdapter.setVideos(videos);
            mFolderAdapter.notifyDataSetChanged();
        }
    }
}

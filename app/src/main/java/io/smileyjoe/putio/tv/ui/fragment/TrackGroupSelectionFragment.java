package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.adapter.BaseListAdapter;
import io.smileyjoe.putio.tv.ui.adapter.TrackGroupListAdapter;

public class TrackGroupSelectionFragment extends Fragment implements BaseListAdapter.Listener<TracksInfo.TrackGroupInfo> {

    public interface Listener{
        void onTrackSelected(@C.TrackType int trackType, TrackGroup item);
    }

    private TrackGroupListAdapter mAdapter;
    private RecyclerView mRecycler;
    private ProgressBar mProgressLoading;
    private TextView mTextEmpty;
    private TextView mTextTitle;
    private Listener mListener;
    @C.TrackType int mTrackType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_audio_track_selection, null);

        mRecycler = layout.findViewById(R.id.recycler_audio_tracks);
        mProgressLoading = layout.findViewById(R.id.progress_loading);
        mTextEmpty = layout.findViewById(R.id.text_empty);
        mTextTitle = layout.findViewById(R.id.text_title);

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new TrackGroupListAdapter(getContext());
        mAdapter.setListener(this);

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setTracksInfo(@C.TrackType int trackType, TracksInfo tracksInfo){
        mTrackType = trackType;

        switch (trackType){
            case C.TRACK_TYPE_AUDIO:
                mTextTitle.setText(R.string.title_audio_track_selection);
                break;
        }

        ArrayList<TracksInfo.TrackGroupInfo> validGroups = new ArrayList<>();
        for (TracksInfo.TrackGroupInfo groupInfo : tracksInfo.getTrackGroupInfos()) {
            if(groupInfo.getTrackType() == trackType && groupInfo.isSupported()) {
                TrackGroup group = groupInfo.getTrackGroup();

                if(group != null && group.length > 0){
                    validGroups.add(groupInfo);
                }
            }
        }

        mProgressLoading.setVisibility(View.GONE);

        if(!validGroups.isEmpty()){
            mAdapter.setItems(validGroups);
            mRecycler.setVisibility(View.VISIBLE);
            mTextEmpty.setVisibility(View.GONE);
        } else {
            mRecycler.setVisibility(View.GONE);
            mTextEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(View view, TracksInfo.TrackGroupInfo item) {
        if(mListener != null){
            mListener.onTrackSelected(mTrackType, item.getTrackGroup());
        }
    }

    @Override
    public void hasFocus(FragmentType type, TracksInfo.TrackGroupInfo item, View view, int position) {
        // do nothing //
    }
}

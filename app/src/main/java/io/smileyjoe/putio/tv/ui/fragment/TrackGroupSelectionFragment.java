package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.TrackGroup;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.FragmentTrackSelectionBinding;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.adapter.BaseListAdapter;
import io.smileyjoe.putio.tv.ui.adapter.TrackGroupListAdapter;

public class TrackGroupSelectionFragment extends BaseFragment<FragmentTrackSelectionBinding> implements BaseListAdapter.Listener<TracksInfo.TrackGroupInfo> {

    public interface Listener {
        void onTrackSelected(@C.TrackType int trackType, TrackGroup item);
    }

    private TrackGroupListAdapter mAdapter;
    private Optional<Listener> mListener;
    @C.TrackType int mTrackType;

    @Override
    protected FragmentTrackSelectionBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentTrackSelectionBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.TRACK_SELECTION);

        mAdapter = new TrackGroupListAdapter(getContext());
        mAdapter.setListener(this);

        mView.recycler.setAdapter(mAdapter);
        mView.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void setTracksInfo(@C.TrackType int trackType, TracksInfo tracksInfo) {
        mTrackType = trackType;

        switch (trackType) {
            case C.TRACK_TYPE_AUDIO:
                mView.textTitle.setText(R.string.title_audio_track_selection);
                mView.textEmpty.setText(R.string.text_no_audio_tracks);
                break;
        }

        ArrayList<TracksInfo.TrackGroupInfo> validGroups = tracksInfo.getTrackGroupInfos().stream()
                .filter(groupInfo -> groupInfo.getTrackType() == trackType && groupInfo.isSupported())
                .filter(groupInfo -> {
                    TrackGroup group = groupInfo.getTrackGroup();
                    return group != null && group.length > 0;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        mView.progressLoading.setVisibility(View.GONE);

        if (!validGroups.isEmpty()) {
            mView.recycler.setVisibility(View.VISIBLE);
            mView.textEmpty.setVisibility(View.GONE);
        } else {
            mView.recycler.setVisibility(View.GONE);
            mView.textEmpty.setVisibility(View.VISIBLE);
        }

        mAdapter.setItems(validGroups);
    }

    @Override
    public View getFocusableView() {
        return mView.recycler;
    }

    @Override
    public void onItemClicked(View view, TracksInfo.TrackGroupInfo item) {
        mListener.ifPresent(listener -> listener.onTrackSelected(mTrackType, item.getTrackGroup()));
    }

    @Override
    public void hasFocus(FragmentType type, TracksInfo.TrackGroupInfo item, View view, int position) {
        // do nothing //
    }
}

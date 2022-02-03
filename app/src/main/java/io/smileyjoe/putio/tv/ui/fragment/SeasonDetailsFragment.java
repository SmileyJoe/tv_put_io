package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.smileyjoe.putio.tv.databinding.FragmentSeasonDetailsBinding;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;

public class SeasonDetailsFragment extends BaseFragment<FragmentSeasonDetailsBinding> {

    private VideoDetailsViewHolder mVideoDetailsViewHolder;

    @Override
    protected FragmentSeasonDetailsBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentSeasonDetailsBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoDetailsViewHolder = VideoDetailsViewHolder.getInstance(getContext(), mView.frameDetails, true);
    }

    public void update(Video video) {
        mVideoDetailsViewHolder.bind(video);
    }

}

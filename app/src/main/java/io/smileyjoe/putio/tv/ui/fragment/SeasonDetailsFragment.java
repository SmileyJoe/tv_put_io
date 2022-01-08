package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.VideoDetailsViewHolder;

public class SeasonDetailsFragment extends Fragment {

    private VideoDetailsViewHolder mVideoDetailsViewHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_season_details, null);

        ViewGroup viewDetails = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.item_video_details, view.findViewById(R.id.frame_details), true);
        mVideoDetailsViewHolder = new VideoDetailsViewHolder(viewDetails);

        return view;
    }

    public void update(Video video){
        mVideoDetailsViewHolder.bind(video);
    }

}

package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.PopulateGenres;

public class VideoSummaryFragment extends Fragment {

    private TextView mTextOverView;
    private TextView mTextTitle;
    private TextView mTextGenres;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_video_summary, null);

        mTextOverView = view.findViewById(R.id.text_overview);
        mTextTitle = view.findViewById(R.id.text_title);
        mTextGenres = view.findViewById(R.id.text_genres);

        return view;
    }

    public void setVideo(Video video){
        mTextOverView.setText(video.getOverView());
        mTextTitle.setText(video.getTitle());

        (new PopulateGenres(mTextGenres, video)).execute();
    }



}

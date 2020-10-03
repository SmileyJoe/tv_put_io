package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class VideoAdapter extends FileAdapter {

    public VideoAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Video video = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, null);
        TextView textTitle = view.findViewById(R.id.text_title);
        ImageView imagePoster = view.findViewById(R.id.image_poster);
        FrameLayout frameWatched = view.findViewById(R.id.frame_watched);
        FrameLayout frameSelected = view.findViewById(R.id.frame_selected);

        textTitle.setText(video.getTitle());

        if(isSelected(position)){
            textTitle.setLines(textTitle.getLineCount());
            textTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fastlane_background));
            textTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_selected));
            frameSelected.setVisibility(View.VISIBLE);
        } else {
            textTitle.setLines(2);
            textTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.overlay));
            textTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.text_unselected));
            frameSelected.setVisibility(View.GONE);
        }

        if(video.isWatched()){
            frameWatched.setVisibility(View.VISIBLE);
        } else {
            frameWatched.setVisibility(View.GONE);
        }
        if(video.getPoster() != null){
            Glide.with(getContext())
                    .load(video.getPoster())
                    .into(imagePoster);
        }

        return view;
    }
}

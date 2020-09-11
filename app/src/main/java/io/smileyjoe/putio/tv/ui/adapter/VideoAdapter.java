package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.putio.File;

public class VideoAdapter extends FileAdapter {

    public VideoAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_video, null);
        TextView textTitle = view.findViewById(R.id.text_title);
        ImageView imagePoster = view.findViewById(R.id.image_poster);
        FrameLayout frameWatched = view.findViewById(R.id.frame_watched);
        FrameLayout frameSelected = view.findViewById(R.id.frame_selected);

        textTitle.setText(file.getName());

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

        if(file.isWatched()){
            frameWatched.setVisibility(View.VISIBLE);
        } else {
            frameWatched.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(file.getScreenShot())){
            Glide.with(getContext())
                    .load(Uri.parse(file.getScreenShot()))
                    .into(imagePoster);
        }

        return view;
    }
}

package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public abstract class FileAdapter extends BaseAdapter {

    private ArrayList<Video> mVideos;
    private Context mContext;
    private int mSelectedPosition = -1;

    public FileAdapter(Context context) {
        mContext = context;
        setVideos(new ArrayList<>());
    }

    protected Context getContext() {
        return mContext;
    }

    public ArrayList<Video> getVideos() {
        return mVideos;
    }

    public void setVideos(ArrayList<Video> videos) {
        mVideos = videos;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
    }

    protected boolean isSelected(int position){
        return mSelectedPosition == position;
    }

    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Video getItem(int position) {
        return mVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getPutId();
    }
}

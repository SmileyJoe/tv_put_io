package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;

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

    protected boolean isSelected(int position) {
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

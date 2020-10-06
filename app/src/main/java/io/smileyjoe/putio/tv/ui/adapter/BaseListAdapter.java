package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;

public abstract class BaseListAdapter<T, U extends BaseViewHolder<T>> extends RecyclerView.Adapter<U> {

    public interface Listener<T> extends BaseViewHolder.Listener<T> {
    }

    private Context mContext;
    private Listener<T> mListener;
    private ArrayList<T> mItems = new ArrayList<>();
    private FragmentType mFragmentType;

    public BaseListAdapter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setListener(Listener<T> listener) {
        mListener = listener;
    }

    public void setItems(ArrayList<T> items) {
        mItems = items;
    }

    public void setFragmentType(FragmentType fragmentType) {
        mFragmentType = fragmentType;
    }

    public FragmentType getFragmentType() {
        return mFragmentType;
    }

    public ArrayList<T> getItems() {
        return mItems;
    }

    public Listener<T> getListener() {
        return mListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bindView(getItem(position), position);
    }
}

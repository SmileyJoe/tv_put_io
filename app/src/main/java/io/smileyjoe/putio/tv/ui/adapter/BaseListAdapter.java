package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;
import io.smileyjoe.putio.tv.ui.viewholder.VideoGridViewHolder;

public abstract class BaseListAdapter<T, U extends BaseViewHolder<T>> extends RecyclerView.Adapter<U> implements BaseViewHolder.Listener<T> {

    public interface Listener<T> extends HomeFragmentListener<T> {
        void onItemClicked(View view, T item);
    }

    private Context mContext;
    private Listener<T> mListener;
    private ArrayList<T> mItems = new ArrayList<>();
    private FragmentType mFragmentType;
    private View mViewSelected;
    private int mSelectedPosition = -1;
    private boolean mMarkSelected = false;
    private boolean mAllowDeselect = false;

    protected abstract @LayoutRes int getLayoutResId();
    protected abstract U getViewHolder(View view, FragmentType fragmentType);

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

    protected void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
        notifyItemChanged(selectedPosition);
    }

    protected void shouldMarkSelected(boolean shouldMarkSelected){
        mMarkSelected = shouldMarkSelected;
    }

    protected void shouldAllowDeselect(boolean allowDeselect){
        mAllowDeselect = allowDeselect;
    }

    @NonNull
    @Override
    public U onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutResId(), parent, false);
        U holder = getViewHolder(view, getFragmentType());

        holder.setListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bindView(getItem(position), position);

        if(mMarkSelected) {
            if (position == mSelectedPosition) {
                highlightItem(holder.getView(), position);
            } else {
                holder.getView().setSelected(false);
            }
        }
    }

    private void highlightItem(View view, int position){
        if(mMarkSelected) {
            if (mViewSelected != null) {
                mViewSelected.setSelected(false);
            }

            view.setSelected(true);
            mViewSelected = view;
            mSelectedPosition = position;
        }
    }

    private void unhighlightItem(View view){
        if(mMarkSelected) {
            mViewSelected = null;
            view.setSelected(false);
            mSelectedPosition = -1;
        }
    }

    @Override
    public void onItemClicked(View view, T item, int position) {
        boolean callListener = false;
        if(view.isSelected()){
            if(mAllowDeselect){
                unhighlightItem(view);
                callListener = true;
            }
        } else {
            highlightItem(view, position);
            callListener = true;
        }

        if(callListener && mListener != null){
            mListener.onItemClicked(view, item);
        }
    }

    public void clearSelected(){
        mSelectedPosition = -1;
        if(mViewSelected != null) {
            mViewSelected.setSelected(false);
            mViewSelected = null;
        }
    }

    @Override
    public void hasFocus(FragmentType type, T item, View view, int position) {
        if(mListener != null){
            mListener.hasFocus(type, item, view, position);
        }
    }
}

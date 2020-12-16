package io.smileyjoe.putio.tv.ui.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

    public interface Listener<T> extends HomeFragmentListener<T> {
        void onItemClicked(View view, T item, int position);
    }

    private T mItem;
    private FragmentType mFragmentType;
    private int mPosition;

    public BaseViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView);

        mFragmentType = fragmentType;
        itemView.setOnClickListener(this);
        itemView.setOnFocusChangeListener(this);
    }

    private Listener<T> mListener;

    public void setListener(Listener<T> listener) {
        mListener = listener;
    }

    public void bindView(T item, int position){
        mItem = item;
        mPosition = position;
    }

    public View getView(){
        return itemView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && mListener != null){
            mListener.hasFocus(mFragmentType, mItem, v, mPosition);
        }
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onItemClicked(v, mItem, mPosition);
        }
    }

    protected void setText(TextView textView, String text){
        if(!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}

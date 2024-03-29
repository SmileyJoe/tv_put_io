package io.smileyjoe.putio.tv.ui.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.Optional;

import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;

public abstract class BaseViewHolder<T, V extends ViewBinding> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, View.OnLongClickListener {

    public interface Listener<T> extends HomeFragmentListener<T> {
        void onItemClicked(View view, T item, int position);
        void update(T item, int position);
    }

    private T mItem;
    private FragmentType mFragmentType;
    private int mPosition;
    protected V mView;
    private Optional<Listener<T>> mListener = Optional.empty();

    protected abstract V inflate(View itemView);

    public BaseViewHolder(@NonNull View itemView, FragmentType fragmentType) {
        super(itemView);

        mView = inflate(itemView);
        mFragmentType = fragmentType;
        itemView.setOnClickListener(this);
        itemView.setOnFocusChangeListener(this);
        itemView.setOnLongClickListener(this);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    public void setListener(Listener<T> listener) {
        mListener = Optional.ofNullable(listener);
    }

    protected Optional<Listener<T>> getListener() {
        return mListener;
    }

    public void bindView(T item, int position) {
        mItem = item;
        mPosition = position;
    }

    protected int getInternalPosition() {
        return mPosition;
    }

    public View getView() {
        return itemView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && mListener.isPresent()) {
            mListener.get().hasFocus(mFragmentType, mItem, v, mPosition);
        }
    }

    @Override
    public void onClick(View v) {
        mListener.ifPresent(listener -> listener.onItemClicked(v, mItem, mPosition));
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    protected void setText(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}

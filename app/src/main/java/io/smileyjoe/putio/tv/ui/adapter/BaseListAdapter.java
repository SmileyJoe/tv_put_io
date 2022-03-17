package io.smileyjoe.putio.tv.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.Optional;

import io.smileyjoe.putio.tv.interfaces.HomeFragmentListener;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.ui.viewholder.BaseViewHolder;

public abstract class BaseListAdapter<T, U extends BaseViewHolder<T, ? extends ViewBinding>> extends RecyclerView.Adapter<U> implements BaseViewHolder.Listener<T> {

    public interface Listener<T> extends HomeFragmentListener<T> {
        void onItemClicked(View view, T item);
    }

    private Context mContext;
    private Optional<Listener<T>> mListener;
    private ArrayList<T> mItems = new ArrayList<>();
    private FragmentType mFragmentType;
    private View mViewSelected;
    private int mSelectedPosition = -1;
    private boolean mMarkSelected = false;
    private boolean mAllowDeselect = false;

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract U getViewHolder(View view, FragmentType fragmentType);

    public BaseListAdapter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setListener(Listener<T> listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void setItems(ArrayList<T> items) {
        int oldSize = getItemCount();

        if (items != null) {
            mItems = items;
        } else {
            mItems = new ArrayList<>();
        }

        int newSize = getItemCount();

        if (newSize == 0) {
            notifyItemRangeRemoved(0, oldSize);
        } else if (oldSize == 0) {
            notifyItemRangeInserted(0, newSize);
        } else if (oldSize == newSize) {
            notifyItemRangeChanged(0, newSize);
        } else if (newSize > oldSize) {
            notifyItemRangeChanged(0, oldSize);
            notifyItemRangeInserted(oldSize, newSize);
        } else if (oldSize > newSize) {
            notifyItemRangeChanged(0, newSize);
            notifyItemRangeRemoved(newSize, oldSize);
        }
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

    public Optional<Listener<T>> getListener() {
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

    protected void shouldMarkSelected(boolean shouldMarkSelected) {
        mMarkSelected = shouldMarkSelected;
    }

    protected void shouldAllowDeselect(boolean allowDeselect) {
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

        if (mMarkSelected) {
            if (position == mSelectedPosition) {
                highlightItem(holder.getView(), position);
            } else {
                holder.getView().setSelected(false);
            }
        }
    }

    private void highlightItem(View view, int position) {
        if (mMarkSelected) {
            if (mViewSelected != null) {
                mViewSelected.setSelected(false);
            }

            view.setSelected(true);
            mViewSelected = view;
            mSelectedPosition = position;
        }
    }

    private void unhighlightItem(View view) {
        if (mMarkSelected) {
            mViewSelected = null;
            view.setSelected(false);
            mSelectedPosition = -1;
        }
    }

    @Override
    public void onItemClicked(View view, T item, int position) {
        boolean callListener = false;
        if (view.isSelected()) {
            if (mAllowDeselect) {
                unhighlightItem(view);
                callListener = true;
            }
        } else {
            highlightItem(view, position);
            callListener = true;
        }

        if (callListener && mListener.isPresent()) {
            mListener.get().onItemClicked(view, item);
        }
    }

    @Override
    public void update(T item, int position) {
        mItems.set(position, item);
        notifyItemChanged(position);
    }

    public void clearSelected() {
        mSelectedPosition = -1;
        if (mViewSelected != null) {
            mViewSelected.setSelected(false);
            mViewSelected = null;
        }
    }

    @Override
    public void hasFocus(FragmentType type, T item, View view, int position) {
        mListener.ifPresent(listener -> listener.hasFocus(type, item, view, position));
    }
}

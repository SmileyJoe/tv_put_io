package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.databinding.FragmentGenreListBinding;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.adapter.GenreListAdapter;
import io.smileyjoe.putio.tv.util.Async;

public class GenreListFragment extends BaseFragment<FragmentGenreListBinding> {

    public interface Listener extends GenreListAdapter.Listener<Genre> {
    }

    private GenreListAdapter mAdapter;

    @Override
    protected FragmentGenreListBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentGenreListBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setType(FragmentType.GENRE);

        Listener listener = null;

        if (getActivity() instanceof Listener) {
            listener = (Listener) getActivity();
        }

        mAdapter = new GenreListAdapter(getContext());
        mAdapter.setFragmentType(FragmentType.GENRE);
        mAdapter.setListener(listener);

        mView.recycler.setAdapter(mAdapter);
        mView.recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    public boolean hasItems() {
        if (mAdapter != null) {
            return mAdapter.getItemCount() > 0;
        } else {
            return false;
        }
    }

    public void clearSelected() {
        mAdapter.clearSelected();
    }

    public void setListener(Listener listener) {
        mAdapter.setListener(listener);
    }

    public void setGenres(ArrayList<Genre> genres) {
        mAdapter.setItems(genres);
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        if (genreIds != null && !genreIds.isEmpty()) {
            Async.run(() ->
                            AppDatabase.getInstance(getContext()).genreDao().getByIds(genreIds),
                    genres -> setGenres(new ArrayList(genres)));
        } else {
            setGenres(new ArrayList<>());
        }
    }
}

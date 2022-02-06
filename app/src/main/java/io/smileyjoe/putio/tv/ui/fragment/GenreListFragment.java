package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.smileyjoe.putio.tv.comparator.GenreComparator;
import io.smileyjoe.putio.tv.databinding.FragmentGenreListBinding;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.adapter.GenreListAdapter;

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

    public void clearSelected() {
        mAdapter.clearSelected();
    }

    public void setListener(Listener listener) {
        mAdapter.setListener(listener);
    }

    public void setGenres(ArrayList<Genre> genres) {
        mAdapter.setItems(genres);
        mAdapter.notifyDataSetChanged();
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        GetGenresTask task = new GetGenresTask(genreIds);
        task.execute();
    }

    private class GetGenresTask extends AsyncTask<Void, Void, List<Genre>> {
        private ArrayList<Integer> mGenreIds;

        public GetGenresTask(ArrayList<Integer> genreIds) {
            mGenreIds = genreIds;
        }

        @Override
        protected List<Genre> doInBackground(Void... voids) {
            List<Genre> genres = AppDatabase.getInstance(getContext()).genreDao().getByIds(mGenreIds);
            Collections.sort(genres, new GenreComparator());
            return genres;
        }

        @Override
        protected void onPostExecute(List<Genre> genres) {
            setGenres(new ArrayList<>(genres));
        }
    }
}

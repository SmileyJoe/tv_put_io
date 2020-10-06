package io.smileyjoe.putio.tv.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.comparator.GenreComparator;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.object.Genre;
import io.smileyjoe.putio.tv.ui.adapter.GenreListAdapter;
import io.smileyjoe.putio.tv.ui.adapter.VideoListAdapter;

public class GenreListFragment extends Fragment {

    public interface Listener extends GenreListAdapter.Listener<Genre> {
    }

    private RecyclerView mRecycler;
    private GenreListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_genre_list, null);
        mRecycler = view.findViewById(R.id.recycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Listener listener = null;

        if(getActivity() instanceof Listener){
            listener = (Listener) getActivity();
        }

        mAdapter = new GenreListAdapter(getContext());
        mAdapter.setListener(listener);

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setListener(Listener listener){
        mAdapter.setListener(listener);
    }

    public void setGenres(ArrayList<Genre> genres){
        mAdapter.setItems(genres);
        mAdapter.notifyDataSetChanged();
    }

    public void setGenreIds(ArrayList<Integer> genreIds){
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

            Genre genreAll = new Genre();
            genreAll.setId(-1);
            genreAll.setTitle(getString(R.string.text_all));

            genres.add(0, genreAll);
            return genres;
        }

        @Override
        protected void onPostExecute(List<Genre> genres) {
            setGenres(new ArrayList<>(genres));
        }
    }
}

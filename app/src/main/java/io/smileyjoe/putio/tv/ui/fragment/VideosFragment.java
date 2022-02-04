package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import io.smileyjoe.putio.tv.databinding.FragmentVideoListBinding;
import io.smileyjoe.putio.tv.object.Filter;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.adapter.VideosAdapter;
import io.smileyjoe.putio.tv.util.VideoUtil;

public class VideosFragment extends BaseFragment<FragmentVideoListBinding> {

    public interface Listener extends VideosAdapter.Listener<Video> {
    }

    private VideosAdapter mVideosAdapter;
    private boolean mIsFullScreen = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Video> mVideosAll;
    private ArrayList<Filter> mAppliedFilters = new ArrayList<>();
    private Integer mAppliedGenreId = -1;
    private VideosAdapter.Style mStyle = VideosAdapter.Style.GRID;

    @Override
    protected FragmentVideoListBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentVideoListBinding.inflate(inflater, container, savedInstanceState);
    }

    public void setStyle(VideosAdapter.Style style) {
        mStyle = style;

        if (mVideosAdapter != null) {
            mVideosAdapter.setStyle(style);
        }

        setLayoutManager(true);
    }

    public void setType(FragmentType fragmentType) {

        if (mVideosAdapter != null) {
            mVideosAdapter.setFragmentType(fragmentType);
        }

        setLayoutManager(true);
    }

    public void setFullScreen(boolean fullScreen) {
        if (mIsFullScreen != fullScreen) {
            mIsFullScreen = fullScreen;
            mView.zoomGridVideo.reset();
            boolean created = setLayoutManager(false);

            if (!created && mLayoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) mLayoutManager).setSpanCount(getSpanCount());
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideosAdapter = new VideosAdapter(getContext(), mStyle);

        mView.recycler.setAdapter(mVideosAdapter);
        setLayoutManager(false);
    }

    public void hideDetails() {
        mView.zoomGridVideo.hide();
    }

    public void setListener(Listener listener) {
        setListener(new AdapterListener(listener));
    }

    public void setListener(VideosAdapter.Listener<Video> listener) {
        mVideosAdapter.setListener(listener);
    }

    public void update(Video video) {
        if (mVideosAdapter != null) {
            mVideosAdapter.update(video);
        }
    }

    private int getSpanCount() {
        int spanCount;

        if (mIsFullScreen) {
            spanCount = 7;
        } else {
            spanCount = 4;
        }

        return spanCount;
    }

    private boolean setLayoutManager(boolean force) {
        boolean created = false;
        if (mView.recycler != null) {
            if (mLayoutManager == null || force) {
                switch (mStyle) {
                    case GRID:
                        mLayoutManager = new GridLayoutManager(getContext(), getSpanCount());
                        break;
                    case LIST:
                        mLayoutManager = new LinearLayoutManager(getContext());
                        break;
                }

                mView.recycler.setLayoutManager(mLayoutManager);
                created = true;
            }
        }
        return created;
    }

    public void setVideos(ArrayList<Video> videos) {
        mAppliedGenreId = -1;
        mAppliedFilters = new ArrayList<>();
        mVideosAll = videos;
        populate();
    }

    private void populate() {
        ArrayList<Video> videos = applyFilters();

        if (videos == null || videos.isEmpty()) {
            mView.layoutEmpty.setVisibility(View.VISIBLE);
            mView.recycler.setVisibility(View.GONE);
        } else {
            mView.layoutEmpty.setVisibility(View.GONE);
            mView.recycler.setVisibility(View.VISIBLE);

            mVideosAdapter.setItems(videos);
            mVideosAdapter.notifyDataSetChanged();
        }
    }

    public void filter(Filter filter, boolean isSelected) {
        if (!isSelected) {
            mAppliedFilters.remove(filter);
        } else {
            if (filter.getGroup().isUnique()) {
                mAppliedFilters.stream()
                        .filter(applied -> applied.getGroup() == filter.getGroup())
                        .collect(Collectors.toList())
                        .forEach(applied -> mAppliedFilters.remove(applied));
            }
            mAppliedFilters.add(filter);
        }

        populate();
    }

    private ArrayList<Video> applyFilters() {
        ArrayList<Video> filtered = new ArrayList<>();

        for (Video video : mVideosAll) {
            boolean includeVideo = true;

            if (mAppliedGenreId > 0) {
                ArrayList<Integer> genreIds = video.getGenreIds();
                if (genreIds == null || !genreIds.contains(mAppliedGenreId)) {
                    includeVideo = false;
                }
            }

            if (includeVideo && mAppliedFilters != null && !mAppliedFilters.isEmpty()) {
                for (Filter filter : mAppliedFilters) {
                    switch (filter) {
                        case SHOW_WATCHED:
                            if (video.isWatched()) {
                                includeVideo = false;
                            }
                            break;
                    }
                }
            }

            if (includeVideo) {
                filtered.add(video);
            }
        }

        Filter filterSort = mAppliedFilters.stream()
                .filter(filter -> filter.getGroup() == Filter.Group.SORT)
                .findFirst()
                .orElse(null);

        if (filterSort != null) {
            VideoUtil.sort(filtered, filterSort);
        } else {
            VideoUtil.sort(filtered);
        }

        return filtered;
    }

    public void filterByGenre(Integer genreId) {
        if (mAppliedGenreId != genreId) {
            mAppliedGenreId = genreId;
            populate();
        }
    }

    public ArrayList<Video> getVideos() {
        if (mVideosAdapter != null) {
            return mVideosAdapter.getItems();
        }

        return null;
    }

    public float getHeight() {
        return getView().getHeight();
    }

    public float getWidth() {
        return getView().getWidth();
    }

    private class AdapterListener implements VideosAdapter.Listener<Video> {
        private Optional<Listener> mListener;

        public AdapterListener(Listener listener) {
            mListener = Optional.ofNullable(listener);
        }

        @Override
        public void onItemClicked(View view, Video item) {
            mListener.ifPresent(listener -> listener.onItemClicked(view, item));
        }

        @Override
        public void hasFocus(FragmentType type, Video item, View view, int position) {
            mView.zoomGridVideo.show(view, item);
            mListener.ifPresent(listener -> listener.hasFocus(type, item, view, position));
        }
    }
}

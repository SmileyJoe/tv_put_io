package io.smileyjoe.putio.tv.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.FragmentSubtitleBinding;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.ResponseString;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Subtitle;
import io.smileyjoe.putio.tv.ui.adapter.SubtitleListAdapter;
import io.smileyjoe.putio.tv.util.FileUtil;

public class SubtitleFragment extends BaseFragment<FragmentSubtitleBinding> implements SubtitleListAdapter.Listener<Subtitle>{

    public interface Listener{
        void showSubtitles(Uri uri);
    }

    private long mPutId;
    private SubtitleListAdapter mAdapter;
    private Optional<Listener> mListener = Optional.empty();

    @Override
    protected FragmentSubtitleBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentSubtitleBinding.inflate(inflater, container, savedInstanceState);
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void setPutId(long putId) {
        mPutId = putId;
        getSubtitles();
    }

    private void getSubtitles(){
        mView.progressLoading.setVisibility(View.VISIBLE);
        mView.recyclerSubtitle.setVisibility(View.GONE);
        mView.textEmpty.setVisibility(View.GONE);

        Putio.getAvailableSubtitles(getContext(), mPutId, new OnAvailableSubtitlesGetResponse());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new SubtitleListAdapter(getContext());
        mAdapter.setListener(this);

        mView.recyclerSubtitle.setAdapter(mAdapter);
        mView.recyclerSubtitle.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public void onItemClicked(View view, Subtitle item) {
        if(item.getPutId() != 0) {
            Putio.getSubtitles(getContext(), item.getPutId(), item.getKey(), new OnSubtitlesGetResponse());
        } else {
            mListener.ifPresent(listener -> listener.showSubtitles(null));
        }
    }

    @Override
    public void hasFocus(FragmentType type, Subtitle item, View view, int position) {
        // do nothing //
    }

    private class OnSubtitlesGetResponse extends ResponseString {
        @Override
        public void onSuccess(String result) {
            Uri uri = FileUtil.saveSubtitle(getContext(), mPutId, result);

            if(uri != null && mListener.isPresent()){
                mListener.get().showSubtitles(uri);
            }
        }
    }

    private class OnAvailableSubtitlesGetResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            if(result != null && result.has("subtitles")) {
                ArrayList<Subtitle> subtitles = Subtitle.fromApi(result.getAsJsonArray("subtitles"), mPutId);

                mView.progressLoading.setVisibility(View.GONE);

                if(subtitles == null || subtitles.isEmpty()){
                    mView.recyclerSubtitle.setVisibility(View.GONE);
                    mView.textEmpty.setVisibility(View.VISIBLE);
                } else {
                    Subtitle subtitleEmpty = new Subtitle();
                    subtitleEmpty.setLanguage(getString(R.string.text_none));
                    subtitleEmpty.setPutId(0);

                    subtitles.add(0, subtitleEmpty);

                    mAdapter.setItems(subtitles);
                    mAdapter.notifyDataSetChanged();

                    mView.recyclerSubtitle.setVisibility(View.VISIBLE);
                    mView.textEmpty.setVisibility(View.GONE);
                }
            }
        }
    }
}

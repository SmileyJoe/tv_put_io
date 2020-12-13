package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Subtitle;
import io.smileyjoe.putio.tv.ui.adapter.FolderListAdapter;
import io.smileyjoe.putio.tv.ui.adapter.SubtitleListAdapter;

public class SubtitleFragment extends Fragment {

    private long mPutId;
    private SubtitleListAdapter mAdapter;
    private RecyclerView mRecycler;
    private ProgressBar mProgressLoading;
    private TextView mTextEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_subtitle, null);

        mRecycler = layout.findViewById(R.id.recycler_subtitle);
        mProgressLoading = layout.findViewById(R.id.progress_loading);
        mTextEmpty = layout.findViewById(R.id.text_empty);

        return layout;
    }

    public void setPutId(long putId) {
        mPutId = putId;
        getSubtitles();
    }

    private void getSubtitles(){
        mProgressLoading.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        mTextEmpty.setVisibility(View.GONE);

        Putio.getSubtitles(getContext(), mPutId, new OnSubtitlesGetResponse());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new SubtitleListAdapter(getContext());

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private class OnSubtitlesGetResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            if(result != null && result.has("subtitles")) {
                ArrayList<Subtitle> subtitles = Subtitle.fromApi(result.getAsJsonArray("subtitles"), mPutId);

                mProgressLoading.setVisibility(View.GONE);

                if(subtitles == null || subtitles.isEmpty()){
                    mRecycler.setVisibility(View.GONE);
                    mTextEmpty.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.setItems(subtitles);
                    mAdapter.notifyDataSetChanged();

                    mRecycler.setVisibility(View.VISIBLE);
                    mTextEmpty.setVisibility(View.GONE);
                }
            }
        }
    }
}

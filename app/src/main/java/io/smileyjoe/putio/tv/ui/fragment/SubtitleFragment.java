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

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.ResponseString;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.object.Subtitle;
import io.smileyjoe.putio.tv.ui.adapter.SubtitleListAdapter;
import io.smileyjoe.putio.tv.util.FileUtil;

public class SubtitleFragment extends Fragment implements SubtitleListAdapter.Listener<Subtitle>{

    public interface Listener{
        void showSubtitles(Uri uri);
    }

    private long mPutId;
    private SubtitleListAdapter mAdapter;
    private RecyclerView mRecycler;
    private ProgressBar mProgressLoading;
    private TextView mTextEmpty;
    private Listener mListener;
    private View mViewSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_subtitle, null);

        mRecycler = layout.findViewById(R.id.recycler_subtitle);
        mProgressLoading = layout.findViewById(R.id.progress_loading);
        mTextEmpty = layout.findViewById(R.id.text_empty);

        return layout;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setPutId(long putId) {
        mPutId = putId;
        getSubtitles();
    }

    private void getSubtitles(){
        mProgressLoading.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        mTextEmpty.setVisibility(View.GONE);

        Putio.getAvailableSubtitles(getContext(), mPutId, new OnAvailableSubtitlesGetResponse());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new SubtitleListAdapter(getContext());
        mAdapter.setListener(this);

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public void onItemClicked(View view, Subtitle item) {
        if(mViewSelected != null){
            mViewSelected.setSelected(false);
        }

        view.setSelected(true);
        mViewSelected = view;

        Putio.getSubtitles(getContext(), item.getPutId(), item.getKey(), new OnSubtitlesGetResponse());
    }

    @Override
    public void hasFocus(FragmentType type, Subtitle item, View view, int position) {
        // do nothing //
    }

    private class OnSubtitlesGetResponse extends ResponseString {
        @Override
        public void onSuccess(String result) {
            Uri uri = FileUtil.saveSubtitle(getContext(), mPutId, result);

            if(uri != null && mListener != null){
                mListener.showSubtitles(uri);
            }
        }
    }

    private class OnAvailableSubtitlesGetResponse extends Response {
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

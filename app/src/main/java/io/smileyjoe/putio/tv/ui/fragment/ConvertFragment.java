package io.smileyjoe.putio.tv.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.channel.UriHandler;
import io.smileyjoe.putio.tv.databinding.FragmentConvertBinding;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Conversion;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.PutioHelper;

public class ConvertFragment extends BaseFragment<FragmentConvertBinding> {

    public interface Listener{
        void conversionFinished(Video video);
    }

    private Video mVideo;
    private Optional<Listener> mListener = Optional.empty();
    private Handler mStatusHandler;

    @Override
    protected FragmentConvertBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentConvertBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStatusHandler = new Handler();
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void setVideo(Video video){
        mVideo = video;
        Glide.with(getContext())
                .load(video.getBackdropAsUri())
                .into(mView.imagePoster);

        mView.textTitle.setText(video.getTitle());
    }

    public void convert(){
        getConversionStatus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            getConversionStatus();
        }
    }

    private void getConversionStatus() {
        if (!mVideo.isConverted() && getContext() != null) {
            Putio.getConversionStatus(getContext(), mVideo.getPutId(), new OnConvertResponse());
        }
    }

    private class OnConvertResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            if(isVisible()) {
                Conversion conversion = Conversion.fromApi(result.getAsJsonObject("mp4"));

                switch (conversion.getStatus()){
                    case IN_QUEUE:
                        mView.textStatus.setText(R.string.convert_status_in_queue);
                        if(isVisible()) {
                            new Handler().postDelayed(() -> getConversionStatus(), 5000);
                        }
                        break;
                    case EXTRACTING:
                    case CONVERTING:
                        mView.textStatus.setText(conversion.getPercentFormatted());
                        if(isVisible()) {
                            new Handler().postDelayed(() -> getConversionStatus(), 1000);
                        }
                        break;
                    case EXTRACTED:
                    case COMPLETED:
                        mView.textStatus.setText(R.string.convert_status_extracted);
                        mVideo.setConverted(true);
                        new LoadVideo().execute();
                        break;
                    case NOT_AVAILABLE:
                        Putio.convertFile(getContext(), mVideo.getPutId(), new Response() {
                            @Override
                            public void onSuccess(JsonObject result) {
                                getConversionStatus();
                            }
                        });
                        break;
                }
            }
        }
    }

    private class LoadVideo extends AsyncTask<Void, Void, Video> {

        @Override
        protected Video doInBackground(Void... voids) {
            PutioHelper helper = new PutioHelper(getContext());
            helper.parse(mVideo.getPutId(), Putio.getFiles(getContext(), mVideo.getPutId()));
            return helper.getCurrent();
        }

        @Override
        protected void onPostExecute(Video video) {
            mListener.ifPresent(listener -> listener.conversionFinished(video));
        }
    }
}

package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.FragmentConvertBinding;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.object.Conversion;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;
import io.smileyjoe.putio.tv.video.VideoCache;

public class ConvertFragment extends BaseFragment<FragmentConvertBinding> {

    private static final int WAIT_SHORT = 1000;
    private static final int WAIT_LONG = 5000;

    public interface Listener {
        void conversionFinished(Video video);
    }

    private Video mVideo;
    private Optional<Listener> mListener = Optional.empty();

    @Override
    protected FragmentConvertBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentConvertBinding.inflate(inflater, container, savedInstanceState);
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void setVideo(Video video) {
        mVideo = video;
        Glide.with(getContext())
                .load(video.getBackdropAsUri())
                .into(mView.imagePoster);

        mView.textTitle.setText(video.getTitle());
    }

    public void convert() {
        getConversionStatus();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getConversionStatus();
        }
    }

    public void getConversionStatus() {
        if (!mVideo.isConverted() && getContext() != null) {
            Putio.Convert.status(getContext(), mVideo.getPutId(), new OnConvertResponse());
        }
    }

    private class OnConvertResponse extends Response {
        @Override
        public void onSuccess(JsonObject result) {
            if (isVisible()) {
                Conversion conversion = Conversion.fromApi(result.getAsJsonObject("mp4"));

                switch (conversion.getStatus()) {
                    case IN_QUEUE:
                        mView.textStatus.setText(R.string.convert_status_in_queue);
                        if (isVisible()) {
                            new Handler().postDelayed(ConvertFragment.this::getConversionStatus, WAIT_LONG);
                        }
                        break;
                    case EXTRACTING:
                    case CONVERTING:
                        mView.textStatus.setText(conversion.getPercentFormatted());
                        if (isVisible()) {
                            new Handler().postDelayed(ConvertFragment.this::getConversionStatus, WAIT_SHORT);
                        }
                        break;
                    case EXTRACTED:
                    case COMPLETED:
                        mView.textStatus.setText(R.string.convert_status_extracted);
                        mVideo.setConverted(true);
                        Async.run(() -> {
                            PutioHelper helper = new PutioHelper(getContext());
                            helper.parse(mVideo.getPutId(), Putio.Files.get(getContext(), mVideo.getPutId()));
                            return helper.getCurrent();
                        }, video -> {
                            VideoCache.getInstance().update(video);
                            mListener.ifPresent(listener -> listener.conversionFinished(video));
                        });
                        break;
                    case NOT_AVAILABLE:
                        Putio.Convert.start(getContext(), mVideo.getPutId(), new Response() {
                            @Override
                            public void onSuccess(JsonObject result) {
                                mVideo.setConverting(true);
                                VideoCache.getInstance().update(mVideo);
                                getConversionStatus();
                            }
                        });
                        break;
                    case ERROR:
                        mView.textStatus.setText(R.string.convert_status_error);
                        mView.textInstructions.setText(R.string.error_generic);
                        break;
                    default:
                        mView.textStatus.setText(R.string.convert_status_unknown);
                        if (isVisible()) {
                            new Handler().postDelayed(ConvertFragment.this::getConversionStatus, WAIT_LONG);
                        }
                        break;
                }
            }
        }
    }
}

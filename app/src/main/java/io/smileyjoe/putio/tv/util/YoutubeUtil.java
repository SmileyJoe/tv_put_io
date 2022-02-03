package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.util.Optional;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class YoutubeUtil {

    public interface Listener{
        void onYoutubeExtracted(String title, String videoUrl);
        void onYoutubeFailed();
    }

    private Extractor mExtractor;
    private Optional<Listener> mListener = Optional.empty();

    public YoutubeUtil(Context context) {
        mExtractor = new Extractor(context);
    }

    public void setListener(Listener listener) {
        mListener = Optional.ofNullable(listener);
    }

    public void extract(String url){
        mExtractor.extract(url, false, true);
    }

    private class Extractor extends YouTubeExtractor{

        public Extractor(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
            if(ytFiles != null && videoMeta != null) {
                YtFile ytFile = ytFiles.get(22);
                String videoUrl = null;

                if(ytFile != null){
                    videoUrl = ytFile.getUrl();
                } else {
                    int maxHeight = -1;
                    for (int i = 0; i < ytFiles.size(); i++) {
                        int key = ytFiles.keyAt(i);
                        // get the object by the key.
                        YtFile file = ytFiles.get(key);

                        if (file.getFormat().getHeight() > maxHeight && file.getFormat().getAudioBitrate() > 0) {
                            videoUrl = file.getUrl();
                            maxHeight = file.getFormat().getHeight();
                        }
                    }
                }

                if(mListener.isPresent()){
                    mListener.get().onYoutubeExtracted(videoMeta.getTitle(), videoUrl);
                }
            } else {
                mListener.ifPresent(listener -> listener.onYoutubeFailed());
            }
        }
    }

}

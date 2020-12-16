package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class YoutubeUtil {

    public interface Listener{
        void onYoutubeExtracted(String title, String videoUrl, String audioUrl);
    }

    private Extractor mExtractor;
    private Listener mListener;

    public YoutubeUtil(Context context) {
        mExtractor = new Extractor(context);
    }

    public void setListener(Listener listener) {
        mListener = listener;
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
            int maxHeight = -1;
            int maxBitrate = -1;
            String videoUrl = null;
            String audioUrl = null;

            for(int i = 0; i < ytFiles.size(); i++) {
                int key = ytFiles.keyAt(i);
                // get the object by the key.
                YtFile file = ytFiles.get(key);

                if(file.getFormat().getHeight() > maxHeight){
                    videoUrl = file.getUrl();
                    maxHeight = file.getFormat().getHeight();
                }

                if(file.getFormat().getAudioBitrate() > maxBitrate){
                    audioUrl = file.getUrl();
                    maxBitrate = file.getFormat().getAudioBitrate();
                }
            }

            if(mListener != null){
                mListener.onYoutubeExtracted(videoMeta.getTitle(), videoUrl, audioUrl);
            }
        }
    }

}

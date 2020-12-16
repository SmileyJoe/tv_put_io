package io.smileyjoe.putio.tv.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;

public class MediaUtil {

    private DefaultDataSourceFactory mDataSourceFactory;
    private ArrayList<MediaSource> mSources;

    public MediaUtil(Context context) {
        mSources = new ArrayList<>();
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        mDataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
    }

    public void addSubtitles(Uri uri){
        MediaSource source = new SingleSampleMediaSource(
                uri,
                mDataSourceFactory,
                com.google.android.exoplayer2.Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en", null),
                C.TIME_UNSET);

        mSources.add(source);
    }

    public MediaSource getSource(){
        if(mSources.size() == 1){
            return mSources.get(0);
        } else {
            return new MergingMediaSource((MediaSource[]) mSources.toArray());
        }
    }

    private void addMedia(String url){
        addMedia(Uri.parse(url));
    }

    private void addMedia(Uri uri){
        MediaSource source =
                new ExtractorMediaSource(
                        uri,
                        mDataSourceFactory,
                        new DefaultExtractorsFactory(),
                        null,
                        null);

        mSources.add(source);
    }
}

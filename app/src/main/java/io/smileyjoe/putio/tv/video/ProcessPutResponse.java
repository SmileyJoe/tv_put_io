package io.smileyjoe.putio.tv.video;

import android.content.Context;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.broadcast.Broadcast;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.VirtualDirectory;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;

public class ProcessPutResponse extends Async.Runner<Void> {

    private long mPutId;
    private JsonObject mResult;
    private long mCurrentPutId;
    private String mCurrentTitle;
    private Context mContext;
    private VideoCache mCache;

    public ProcessPutResponse(Context context, long putId, JsonObject result) {
        mContext = context;
        mPutId = putId;
        mResult = result;
        mCache = VideoCache.getInstance();
    }

    @Override
    protected Void onBackground() {
        PutioHelper helper = new PutioHelper(mContext);
        helper.setListener(video -> Broadcast.Videos.update(mContext, video));
        helper.parse(mPutId, mResult);

        mCurrentPutId = helper.getCurrent().getPutId();
        mCurrentTitle = helper.getCurrent().getTitleFormatted(mContext, true);

        mCache.add(
                mCurrentPutId,
                helper.getVideos(),
                helper.getFolders(),
                helper.getCurrent()
        );

        return null;
    }

    @Override
    protected void onMain(Void param) {
        VirtualDirectory virtual = VirtualDirectory.getFromPutId(mContext, mCurrentPutId);
        HistoryItem historyItem;

        if (virtual == null) {
            historyItem = HistoryItem.directory(mCurrentPutId, mCurrentTitle);
        } else {
            historyItem = HistoryItem.virtualDirectory(mCurrentPutId, mCurrentTitle);
        }

        Broadcast.Videos.loaded(mContext, historyItem, mCache.getVideos(mCurrentPutId), mCache.getFolders(mCurrentPutId), true);
    }

}

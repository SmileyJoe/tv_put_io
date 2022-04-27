package io.smileyjoe.putio.tv.broadcast;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.interfaces.Folder;
import io.smileyjoe.putio.tv.object.HistoryItem;
import io.smileyjoe.putio.tv.object.Video;

public class Broadcast {

    interface Listener {
        void onReceive(Context context, Intent intent);
    }

    private Broadcast() {
    }

    private static abstract class Base {
        private Base() {
        }

        protected static String action(String type) {
            return BuildConfig.APPLICATION_ID + "." + type;
        }
    }

    public static class Videos extends Base {

        public static final String STARTED = action("started");
        public static final String LOADED = action("loaded");
        public static final String UPDATE = action("update");
        public static final String EXTRA_HISTORY = "history_item";
        public static final String EXTRA_VIDEOS = "videos";
        public static final String EXTRA_FOLDERS = "folders";
        public static final String EXTRA_SHOULD_ADD_HISTORY = "should_add_history";
        public static final String EXTRA_VIDEO = "video";

        private Videos() {
        }

        public static void loadStarted(Context context) {
            Intent intent = new Intent();
            intent.setAction(STARTED);
            context.sendBroadcast(intent);
        }

        public static void loaded(Context context, HistoryItem item, ArrayList<Video> videos, ArrayList<Folder> folders, boolean shouldAddToHistory) {
            Intent intent = new Intent();
            intent.setAction(LOADED);
            intent.putExtra(EXTRA_HISTORY, item);
            intent.putExtra(EXTRA_VIDEOS, videos);
            intent.putExtra(EXTRA_FOLDERS, folders);
            intent.putExtra(EXTRA_SHOULD_ADD_HISTORY, shouldAddToHistory);
            context.sendBroadcast(intent);
        }

        public static void update(Context context, Video video) {
            Intent intent = new Intent();
            intent.setAction(UPDATE);
            intent.putExtra(EXTRA_VIDEO, video);
            context.sendBroadcast(intent);
        }
    }
}

package io.smileyjoe.putio.tv.channel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.PutioHelper;

public class UriHandler implements Parcelable {

    public enum Type {
        CHANNEL("channel", "https://%1$s/%2$s/%3$s"),
        VIDEO("video", "https://%1$s/%2$s/%3$d"),
        SERIES("series", "https://%1$s/%2$s/%3$d");

        private String mSegment;
        private String mUriBase;

        Type(String segment, String uriBase) {
            mSegment = segment;
            mUriBase = uriBase;
        }

        public String getSegment() {
            return mSegment;
        }

        public String getUriBase() {
            return mUriBase;
        }

        public static Type fromSegment(String segment) {
            return Arrays.stream(values())
                    .filter(type -> type.getSegment().equals(segment))
                    .findFirst()
                    .orElse(null);
        }
    }

    public interface LoadedListener {
        void onLoaded(Type type, Video video);
    }

    private long mPutId = -1;
    private Type mType;
    private ChannelType mChannelType;

    public static Uri buildChannel(Context context, ChannelType type) {
        return Uri.parse(String.format(Type.CHANNEL.getUriBase(), context.getString(R.string.host_name), Type.CHANNEL.getSegment(), type.getInternalId()));
    }

    public static Uri buildVideo(Context context, Video video) {
        return Uri.parse(String.format(Type.VIDEO.getUriBase(), context.getString(R.string.host_name), Type.VIDEO.getSegment(), video.getPutId()));
    }

    public static Uri buildSeries(Context context, Video video) {
        return Uri.parse(String.format(Type.SERIES.getUriBase(), context.getString(R.string.host_name), Type.SERIES.getSegment(), video.getPutId()));
    }

    public UriHandler() {
    }

    public boolean isValid() {
        return mType != null;
    }

    private void clear() {
        mPutId = -1;
        mType = null;
        mChannelType = null;
    }

    public long getPutId() {
        return mPutId;
    }

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void process(Intent intent) {
        // Navigates to other fragments based on Intent's action
        // [MainActivity] is the main entry point for all intent filters
        if (intent.getAction() == Intent.ACTION_VIEW) {
            Uri uri = intent.getData();

            if (uri != null) {
                uri.getPathSegments().stream().findFirst().ifPresent(segment -> {
                    mType = Type.fromSegment(segment);
                    switch (mType) {
                        case CHANNEL:
                            handleChannel(uri);
                            break;
                        case VIDEO:
                            handleVideo(uri);
                            break;
                        case SERIES:
                            handleSeries(uri);
                            break;
                    }
                });
            }
        }
    }

    private void handleSeries(Uri uri) {
        try {
            mPutId = Long.parseLong(uri.getLastPathSegment());
        } catch (NumberFormatException e) {
            clear();
        }
    }

    private void handleChannel(Uri uri) {
        mChannelType = ChannelType.fromInternalId(uri.getLastPathSegment());
    }

    private void handleVideo(Uri uri) {
        try {
            mPutId = Long.parseLong(uri.getLastPathSegment());
        } catch (NumberFormatException e) {
            clear();
        }
    }

    public void execute(Context context, LoadedListener listener) {
        execute(context, Optional.ofNullable(listener));
    }

    public void execute(Context context, Optional<LoadedListener> listener) {
        boolean loading = false;
        if (mType != null) {
            switch (mType) {
                case SERIES:
                case VIDEO:
                    if (mPutId > 0) {
                        Async.run(() -> {
                            PutioHelper helper = new PutioHelper(context);
                            helper.parse(mPutId, Putio.Files.get(context, mPutId));
                            return helper.getCurrent();
                        }, video -> {
                            listener.ifPresent(l -> l.onLoaded(mType, video));
                            clear();
                        });

                        loading = true;
                    }
                    break;
                case CHANNEL:
                default:
                    loading = false;
                    break;
            }
        }

        if (!loading && listener.isPresent()) {
            listener.get().onLoaded(mType, null);
            clear();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mPutId);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
    }

    public void readFromParcel(Parcel source) {
        this.mPutId = source.readLong();
        int tmpMType = source.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
    }

    protected UriHandler(Parcel in) {
        this.mPutId = in.readLong();
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : Type.values()[tmpMType];
    }

    public static final Creator<UriHandler> CREATOR = new Creator<UriHandler>() {
        @Override
        public UriHandler createFromParcel(Parcel source) {
            return new UriHandler(source);
        }

        @Override
        public UriHandler[] newArray(int size) {
            return new UriHandler[size];
        }
    };

    @Override
    public String toString() {
        return "UriHandler{" +
                "mPutId=" + mPutId +
                ", mType=" + mType +
                '}';
    }
}

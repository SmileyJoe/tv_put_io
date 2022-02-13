package io.smileyjoe.putio.tv.channel;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.tvprovider.media.tv.PreviewChannel;
import androidx.tvprovider.media.tv.PreviewChannelHelper;
import androidx.tvprovider.media.tv.TvContractCompat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;

public class Channels {

    private Channels() {
    }

    public static void addProgramme(Context context, ChannelType type, Video video) {
        Programmes.add(context, type, video);
    }

    public static Optional<PreviewChannel> get(Context context, ChannelType type) {
        PreviewChannelHelper helper = new PreviewChannelHelper(context);
        List<PreviewChannel> channels = helper.getAllChannels();

        return channels.stream()
                .filter(previewChannel -> previewChannel.getInternalProviderId().equals(type.getInternalId()))
                .findFirst();
    }

    public static Optional<PreviewChannel> create(Context context, ChannelType type) {
        Optional<PreviewChannel> channel = get(context, type);

        if (!channel.isPresent()) {
            PreviewChannel newChannel = build(context, null, type);

            try {
                long channelId = (new PreviewChannelHelper(context)).publishChannel(newChannel);
                channel = Optional.ofNullable(newChannel);

                if (channelId >= 0 && type == ChannelType.DEFAULT) {
                    TvContractCompat.requestChannelBrowsable(context, channelId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return channel;
    }

    public static void update(Context context, ChannelType type) {
        Optional<PreviewChannel> channel = get(context, type);

        channel.ifPresent(current -> {
            PreviewChannel updateChannel = build(context, current, type);

            try {
                (new PreviewChannelHelper(context)).updatePreviewChannel(channel.get().getId(), updateChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static PreviewChannel build(Context context, PreviewChannel current, ChannelType type) {
        Uri logo = getIcon(context.getResources(), R.mipmap.ic_channel_default);

        PreviewChannel.Builder builder;

        if (current != null) {
            builder = new PreviewChannel.Builder(current);
        } else {
            builder = new PreviewChannel.Builder();
        }

        return builder
                .setInternalProviderId(type.getInternalId())
                .setLogo(logo)
                .setAppLinkIntentUri(UriHandler.buildChannel(context, type))
                .setDisplayName(type.getTitle(context))
                .setDescription(type.getDescription(context))
                .build();
    }

    private static Uri getIcon(Resources resources, @DrawableRes int icon) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(icon))
                .appendPath(resources.getResourceTypeName(icon))
                .appendPath(resources.getResourceEntryName(icon))
                .build();
    }
}

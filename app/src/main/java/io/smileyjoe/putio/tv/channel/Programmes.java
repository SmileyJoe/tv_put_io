package io.smileyjoe.putio.tv.channel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.tvprovider.media.tv.PreviewChannel;
import androidx.tvprovider.media.tv.PreviewChannelHelper;
import androidx.tvprovider.media.tv.PreviewProgram;
import androidx.tvprovider.media.tv.TvContractCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.ui.activity.MainActivity;

public class Programmes {

    private Programmes(){}

    public static void add(Context context, ChannelType type, Video video){
        add(context, Channels.get(context, type) , video);
    }

    @SuppressLint("RestrictedApi")
    private static void add(Context context, Optional<PreviewChannel> channel, Video video){
        if(channel.isPresent()) {
            long channelId = channel.get().getId();
            List<PreviewProgram> programs = get(context, channelId);

            PreviewProgram program = programs.stream()
                    .filter(previewProgram -> previewProgram.getContentId().equals(Long.toString(video.getPutId())))
                    .findFirst()
                    .orElse(null);

            PreviewProgram.Builder builder;
            if (program != null) {
                builder = new PreviewProgram.Builder(program);
            } else {
                builder = new PreviewProgram.Builder();
            }

            UriHandler handler = new UriHandler();
            handler.setPutId(video.getPutId());

            builder.setChannelId(channelId)
                    .setContentId(Long.toString(video.getPutId()))
                    .setPosterArtUri(video.getPosterAsUri())
                    .setThumbnailUri(video.getPosterAsUri())
                    .setTitle(video.getTitleFormatted())
                    .setIntentUri(UriHandler.buildVideo(context, video))
                    .setType(TvContractCompat.PreviewProgramColumns.TYPE_MOVIE);

            try {
                if (program == null) {
                    (new PreviewChannelHelper(context)).publishPreviewProgram(builder.build());
                } else {
                    (new PreviewChannelHelper(context)).updatePreviewProgram(program.getId(), builder.build());
                }
            } catch (IllegalArgumentException e) {
                Log.d("Channel", "Unable to add program: $updatedProgram", e);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private static List<PreviewProgram> get(Context context, long channelId){
        List<PreviewProgram> programs = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    TvContractCompat.PreviewPrograms.CONTENT_URI,
                    PreviewProgram.PROJECTION,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    PreviewProgram program = PreviewProgram.fromCursor(cursor);
                    if (channelId == program.getChannelId()) {
                        programs.add(program);
                    }
                } while (cursor.moveToNext());
            }
            if(cursor != null){
                cursor.close();
            }

        } catch (IllegalArgumentException e) {
            Log.e("Channel", "Error retrieving preview programs", e);
        }

        return programs;
    }
}

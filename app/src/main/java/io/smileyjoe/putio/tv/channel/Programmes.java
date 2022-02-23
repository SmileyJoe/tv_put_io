package io.smileyjoe.putio.tv.channel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import androidx.tvprovider.media.tv.PreviewChannel;
import androidx.tvprovider.media.tv.PreviewChannelHelper;
import androidx.tvprovider.media.tv.PreviewProgram;
import androidx.tvprovider.media.tv.TvContractCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class Programmes {

    private static final int MAX = 10;

    private Programmes() {
    }

    public static void add(Context context, ChannelType type, Video video) {
        add(context, Channels.get(context, type), video);
    }

    @SuppressLint("RestrictedApi")
    private static void add(Context context, Optional<PreviewChannel> channel, Video video) {
        if (channel.isPresent()) {
            PreviewChannelHelper helper = new PreviewChannelHelper(context);
            long channelId = channel.get().getId();
            int timeLeft = (video.getRuntime() * 60000) - (Math.toIntExact(video.getResumeTime()) * 1000);
            List<PreviewProgram> programs = get(context, channelId);

            // get the current item or null //
            PreviewProgram program = programs.stream()
                    .filter(previewProgram -> previewProgram.getContentId().equals(Long.toString(video.getPutId())))
                    .findFirst()
                    .orElse(null);

            if (video.getVideoType() == VideoType.MOVIE && timeLeft <= 600000) {
                helper.deletePreviewProgram(program.getId());
                return;
            } else {
                // Remove older items //
                if (programs.size() > MAX) {
                    IntStream.range(0, programs.size() - MAX)
                            .forEach(i -> helper.deletePreviewProgram(programs.get(i).getId()));
                }

                // reduce the weight of everything in the channel by one //
                programs.stream()
                        .forEach(programLooped -> {
                            PreviewProgram.Builder builder = new PreviewProgram.Builder(programLooped);
                            builder.setWeight(programLooped.getWeight() - 1);
                            helper.updatePreviewProgram(programLooped.getId(), builder.build());
                        });

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
                        .setPosterArtUri(video.getBackdropAsUri())
                        .setThumbnailUri(video.getBackdropAsUri())
                        .setTitle(video.getTitleFormatted(context,true))
                        .setReleaseDate(Calendar.getInstance().getTime())
                        .setWeight(programs.size())
                        .setDescription(video.getOverView())
                        .setPosterArtAspectRatio(TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9)
                        .setThumbnailAspectRatio(TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9);

                switch (video.getVideoType()) {
                    case MOVIE:
                        builder.setIntentUri(UriHandler.buildVideo(context, video))
                                .setType(TvContractCompat.PreviewProgramColumns.TYPE_MOVIE)
                                .setDurationMillis(timeLeft);
                        break;
                    case SEASON:
                        builder.setIntentUri(UriHandler.buildSeries(context, video))
                                .setSeasonNumber(video.getSeason())
                                .setType(TvContractCompat.PreviewProgramColumns.TYPE_TV_SEASON);
                        break;
                }

                try {
                    if (program == null) {
                        helper.publishPreviewProgram(builder.build());
                    } else {
                        helper.updatePreviewProgram(program.getId(), builder.build());
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private static List<PreviewProgram> get(Context context, long channelId) {
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
            if (cursor != null) {
                cursor.close();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return programs;
    }
}

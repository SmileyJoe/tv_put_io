package io.smileyjoe.putio.tv.torrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;

public class Parse {

    public static HashMap<String, String> PATTERNS = new HashMap<>();

    static {
        PATTERNS.put("season", "([Ss]?([0-9]{1,2}))[Eex]");
        PATTERNS.put("episode", "([Eex]([0-9]{2})(?:[^0-9]|$))");
        PATTERNS.put("year", "([\\[\\(]?((?:19[0-9]|20[01])[0-9])[\\]\\)]?)");
    }

    public static Video update(Video video) {
        HashMap<String, String> details = Parse.parse(video.getTitle());

        video.setTitle(details.get("title"));

        if (details.containsKey("year")) {
            video.setYear(Integer.parseInt(details.get("year")));
        }

        if (details.containsKey("season")) {
            video.setSeason(Integer.parseInt(details.get("season")));
        }

        if (details.containsKey("episode")) {
            video.setEpisode(Integer.parseInt(details.get("episode")));
        }

        if (video.getType() == VideoType.VIDEO) {
            if (details.containsKey("is_movie")) {
                boolean isMovie = Boolean.parseBoolean(details.get("is_movie"));

                if (isMovie) {
                    video.setType(VideoType.MOVIE);
                } else {
                    video.setType(VideoType.EPISODE);
                }
            }
        }

        return video;
    }

    public static HashMap<String, String> parse(String rawTitle) {
        ArrayList<String> matchesRaw = new ArrayList<>();
        HashMap<String, String> matchesClean = new HashMap<>();
        int titleStart = 0;
        int titleEnd = rawTitle.length();

        for (Map.Entry<String, String> entry : PATTERNS.entrySet()) {
            String key = entry.getKey();
            String pattern = entry.getValue();

            Matcher matcher = Pattern.compile(pattern).matcher(rawTitle);

            while (matcher.find()) {
                int matchIndex = 0;

                if (matcher.groupCount() > 1) {
                    matchIndex = 1;
                }

                String match = matcher.group(matchIndex);

                matchesRaw.add(match);

                boolean isMovie = true;

                switch (key) {
                    case "season":
                    case "episode":
                        isMovie = false;
                    case "year":
                        match = String.valueOf(Integer.parseInt(match.replaceAll("[^\\d]", "")));
                        break;
                }

                matchesClean.put(key, match);
                matchesClean.put("is_movie", String.valueOf(isMovie));

                if (matcher.start() == 0) {
                    int end = matcher.end();

                    if (titleStart < end) {
                        titleStart = end;
                    }

                } else {
                    int start = matcher.start();

                    if (titleEnd > start) {
                        titleEnd = start;
                    }
                }
            }
        }

        if (titleEnd <= titleStart) {
            titleEnd = rawTitle.length();
        }

        String cleanTitle = rawTitle.substring(titleStart, titleEnd);

        if (!cleanTitle.contains(" ") && cleanTitle.contains(".")) {
            cleanTitle = cleanTitle.replace(".", " ");
        }

        matchesClean.put("title", cleanTitle.trim());

        return matchesClean;
    }
}

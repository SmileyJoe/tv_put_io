package io.smileyjoe.putio.tv.torrent;

import android.util.Log;

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
        PATTERNS.put("year", "([\\[\\(]?((?:19[0-9]|20[0-9])[0-9])[\\]\\)]?)");
        PATTERNS.put("resolution", "([0-9]{3,4}p)");
        PATTERNS.put("quality", "((?:PPV\\.)?[HP]DTV|(?:HD)?CAM|B[DR]Rip|(?:HD-?)?TS|(?:PPV )?WEB-?DL(?: DVDRip)?|HDRip|DVDRip|DVDRIP|CamRip|W[EB]BRip|BluRay|DvDScr|hdtv|telesync)");
        PATTERNS.put("codec", "(xvid|[HhXx]\\.?26[45])");
        PATTERNS.put("audio", "(MP3|DD5\\.?1|Dual[\\- ]Audio|LiNE|DTS|AAC[.-]LC|AAC(?:\\.?2\\.0)?|AC3(?:\\.5\\.1)?)");
        PATTERNS.put("group", "(- ?([^-]+(?:-=\\{[^-]+-?$)?))$");
        PATTERNS.put("region", "R[0-9]");
        PATTERNS.put("extended", "(EXTENDED(:?.CUT)?)");
        PATTERNS.put("hardcoded", "HC");
        PATTERNS.put("proper", "PROPER");
        PATTERNS.put("repack", "REPACK");
        PATTERNS.put("container", "(MKV|AVI|MP4)");
        PATTERNS.put("widescreen", "WS");
        PATTERNS.put("website", "^(\\[ ?([^\\]]+?) ?\\])");
        PATTERNS.put("language", "(rus\\.eng|ita\\.eng)");
        PATTERNS.put("sbs", "(?:Half-)?SBS");
        PATTERNS.put("unrated", "UNRATED");
        PATTERNS.put("size", "(\\d+(?:\\.\\d+)?(?:GB|MB))");
        PATTERNS.put("3d", "3D");
    }

    public static Video update(Video video) {
        HashMap<String, String> details = Parse.parse(video.getTitle());

        if(!video.isTmdbFound()) {
            video.setTitle(details.get("title"));

            if (details.containsKey("is_movie")) {
                boolean isMovie = Boolean.parseBoolean(details.get("is_movie"));

                if (isMovie) {
                    video.setType(VideoType.MOVIE);
                } else {
                    if(video.getType() != VideoType.FOLDER) {
                        video.setType(VideoType.EPISODE);
                    }
                }
            }
        } else {
            video.setType(VideoType.MOVIE);
        }

        if (details.containsKey("year")) {
            video.setYear(Integer.parseInt(details.get("year")));
        }

        if (details.containsKey("season")) {
            video.setSeason(Integer.parseInt(details.get("season")));
        }

        if (details.containsKey("episode")) {
            video.setEpisode(Integer.parseInt(details.get("episode")));
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

                switch (key) {
                    case "season":
                    case "episode":
                    case "year":
                        match = String.valueOf(Integer.parseInt(match.replaceAll("[^\\d]", "")));
                        break;
                }

                matchesClean.put(key, match);

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

        boolean isMovie = false;
        if(matchesClean.containsKey("year")){
            isMovie = true;
        }

        matchesClean.put("is_movie", String.valueOf(isMovie));

        if (titleEnd <= titleStart) {
            titleEnd = rawTitle.length();
        }

        String cleanTitle = rawTitle.substring(titleStart, titleEnd).trim();

        cleanTitle = replaceIfAll(cleanTitle, ".");
        cleanTitle = replaceIfAll(cleanTitle, "_");

        matchesClean.put("title", cleanTitle.trim());

        return matchesClean;
    }

    private static String replaceIfAll(String title, String character){
        if (!title.contains(" ") && title.contains(character)) {
            title = title.replace(character, " ");
        }

        return title;
    }
}

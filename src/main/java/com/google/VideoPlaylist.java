package com.google;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to represent a Playlist
 */
class VideoPlaylist {

    private final String name;
    private final List<Video> videos;

    public VideoPlaylist(String name) {
        this.name = name;
        this.videos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Video> getVideos() {
        return videos;
    }

    private Video findVideo(String videoId) {
        return videos.stream()
                .filter(a -> a.getVideoId().equals(videoId))
                .findFirst()
                .orElse(null);
    }

    public boolean addVideo(String videoId, Video newVideo) {
        Video video = findVideo(videoId);
        if (video == null) {
            videos.add(newVideo);
            return true;
        }
        return false;
    }

    public boolean removeVideo(String videoId) {
        Video video = findVideo(videoId);

        if (video == null)
            return false;
        videos.remove(video);
        return true;
    }
}

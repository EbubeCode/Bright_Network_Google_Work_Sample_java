package com.google;

import java.util.Collections;
import java.util.List;

/**
 * A class used to represent a video.
 */
class Video {

    private final String title;
    private final String videoId;
    private final List<String> tags;
    private String flag;
    private boolean isFlagged;

    Video(String title, String videoId, List<String> tags) {
        this.title = title;
        this.videoId = videoId;
        this.tags = Collections.unmodifiableList(tags);
        isFlagged = false;
    }

    /**
     * Returns the title of the video.
     */
    String getTitle() {
        return title;
    }

    /**
     * Returns the video id of the video.
     */
    String getVideoId() {
        return videoId;
    }

    /**
     * Returns a readonly collection of the tags of the video.
     */
    List<String> getTags() {
        return tags;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        if (flag == null)
            this.flag = "Not supplied";
        else
            this.flag = flag;
        isFlagged = true;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void removeFlag() {
        isFlagged = false;
        this.flag = null;
    }
}

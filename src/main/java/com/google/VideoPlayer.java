package com.google;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoPlayer {

    private final VideoLibrary videoLibrary;
    private Video playingVideo;
    private boolean isVideoPaused;
    private List<VideoPlaylist> playlists;
    private Pattern searchPattern;
    private Pattern tagPattern;
    private List<Video> unFlaggedVids;


    public VideoPlayer() {
        this.videoLibrary = new VideoLibrary();
        isVideoPaused = false;
        playlists = new ArrayList<>();
        searchPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        tagPattern = Pattern.compile("(#[a-z]+)", Pattern.CASE_INSENSITIVE);
        unFlaggedVids = videoLibrary.getVideos();

    }

    public void numberOfVideos() {
        System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
    }

    public void showAllVideos() {
        List<Video> videos = videoLibrary.getVideos();
        videos.sort(Comparator.comparing(Video::getTitle));
        System.out.println("Here's a list of all available videos:");

        for (Video video : videos) {
            StringBuilder buildString = (!video.isFlagged()) ? buildString(video) :
                    buildString(video).append(" - FLAGGED (reason: " + video.getFlag() + ")");
            System.out.println(buildString);
        }
    }

    public void playVideo(String videoId) {
        Video tempVideo = videoLibrary.getVideo(videoId);

        if (tempVideo == null)
            System.out.println("Cannot play video: Video does not exist");
        else if (tempVideo.isFlagged())
            System.out.println("Cannot play video: Video is currently flagged (reason: "
                    + tempVideo.getFlag() + ")");
        else {
            if (playingVideo != null)
                stopVideo();
            System.out.println("Playing video: " + tempVideo.getTitle());
            playingVideo = tempVideo;
        }
    }

    public void stopVideo() {
        if (playingVideo != null) {
            System.out.println("Stopping video: " + playingVideo.getTitle());
            playingVideo = null;
            isVideoPaused = false;
        } else {
            System.out.println("Cannot stop video: No video is currently playing");
        }
    }

    public void playRandomVideo() {
        if (playingVideo != null) {
            stopVideo();
        }
        if (unFlaggedVids.isEmpty())
            System.out.println("No videos available");
        else {
            String id = unFlaggedVids.get((int) Math.floor(Math.random() * unFlaggedVids.size())).getVideoId();
            playVideo(id);
        }
    }

    public void pauseVideo() {
        if (playingVideo != null) {
            if (isVideoPaused)
                System.out.println("Video already paused: " + playingVideo.getTitle());
            else {
                System.out.println("Pausing video: " + playingVideo.getTitle());
                isVideoPaused = true;
            }
        } else
            System.out.println("Cannot pause video: No video is currently playing");
    }

    public void continueVideo() {
        if (playingVideo == null)
            System.out.println("Cannot continue video: No video is currently playing");

        else if (isVideoPaused) {
            isVideoPaused = false;
            System.out.println("Continuing video: " + playingVideo.getTitle());
        }
        else
            System.out.println("Cannot continue video: Video is not paused");
    }

    public void showPlaying() {
        if (playingVideo == null)
            System.out.println("No video is currently playing");
        else {
            StringBuilder showPlayingString = new StringBuilder("Currently playing: ")
            .append(buildString(playingVideo));

            if (isVideoPaused) {
                System.out.println(showPlayingString + " - PAUSED");
            } else
                System.out.println(showPlayingString);
        }
    }

    private StringBuilder buildString(Video video) {
        StringBuilder showPlayingString = new StringBuilder(video.getTitle() + " "
                + "(" + video.getVideoId() + ")" + " [");
        List<String> tags = video.getTags();

        for (int i = 0; i < tags.size(); i++) {
            if (i == tags.size() - 1)
                showPlayingString.append(tags.get(i));
            else
                showPlayingString.append(tags.get(i) + " ");
        }
        showPlayingString.append("]");
        return showPlayingString;
    }

    public void createPlaylist(String playlistName) {
        VideoPlaylist vidPlaylist = checkVidPlaylistExist(playlistName);

        if (vidPlaylist == null) {
            vidPlaylist = new VideoPlaylist(playlistName);
            System.out.println("Successfully created new playlist: " + playlistName);
            playlists.add(vidPlaylist);
        } else
            System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }

    private VideoPlaylist checkVidPlaylistExist(String playlistName) {
        return playlists.stream()
                .filter(a -> a.getName().equalsIgnoreCase(playlistName))
                .findFirst()
                .orElse(null);
    }

    public void addVideoToPlaylist(String playlistName, String videoId) {
        VideoPlaylist videoPlaylist = checkVidPlaylistExist(playlistName);
        Video newVid = videoLibrary.getVideo(videoId);
        if (videoPlaylist == null) {
            System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
        } else if (newVid == null)
            System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
        else if (newVid.isFlagged()) {
            System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged " +
                    "(reason: " + newVid.getFlag() + ")");
        } else if (videoPlaylist.addVideo(videoId, newVid)) {
            System.out.println("Added video to " + playlistName + ": " + newVid.getTitle());
        } else
            System.out.println("Cannot add video to " + playlistName + ": Video already added");
    }

    public void showAllPlaylists() {
        if (playlists.isEmpty()) {
            System.out.println("No playlists exist yet");
        } else {
            System.out.println("Showing all playlists:");
            playlists.sort(Comparator.comparing(VideoPlaylist::getName));
            for (VideoPlaylist videoPlaylist : playlists)
                System.out.println("\t"+ videoPlaylist.getName());
        }
    }

    public void showPlaylist(String playlistName) {
        VideoPlaylist videoPlaylist = checkVidPlaylistExist(playlistName);
        if (videoPlaylist == null) {
            System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
        } else {
            List<Video> videoList = videoPlaylist.getVideos();
            System.out.println("Showing playlist: " + playlistName);
            if (videoList.isEmpty())
                System.out.println("\tNo videos here yet");
            else
                for (Video video : videoList) {
                    StringBuilder buildString = (!video.isFlagged()) ? buildString(video) :
                            buildString(video).append(" - FLAGGED (reason: " + video.getFlag() + ")");
                    System.out.println("\t" + buildString);
                }
        }
    }

    public void removeFromPlaylist(String playlistName, String videoId) {
        VideoPlaylist videoPlaylist = checkVidPlaylistExist(playlistName);
        Video newVid = videoLibrary.getVideo(videoId);
        if (videoPlaylist == null) {
            System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
        } else if (newVid == null)
            System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
        else if (videoPlaylist.removeVideo(videoId)) {
            System.out.println("Removed video from " + playlistName + ": " + newVid.getTitle());
        } else
            System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
    }

    public void clearPlaylist(String playlistName) {
        VideoPlaylist videoPlaylist = checkVidPlaylistExist(playlistName);
        if (videoPlaylist == null)
            System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
        else {
            videoPlaylist.getVideos().clear();
            System.out.println("Successfully removed all videos from " + playlistName);
        }
    }

    public void deletePlaylist(String playlistName) {
        VideoPlaylist videoPlaylist = checkVidPlaylistExist(playlistName);
        if (videoPlaylist == null)
            System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
        else {
            playlists.remove(videoPlaylist);
            System.out.println("Deleted playlist: " + playlistName);
        }
    }

    public void searchVideos(String searchTerm) {
        Matcher m = searchPattern.matcher(searchTerm);
        if (m.find())
            System.out.println("No search results for " + searchTerm);
        else if(unFlaggedVids.isEmpty()) {
            System.out.println("No videos available");
        }
        else {
            List<Video> searchResult = unFlaggedVids.stream()
                    .filter(a ->
                            Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE)
                                    .matcher(a.getTitle()).find())
                    .collect(Collectors.toList());
            if (searchResult.isEmpty())
                System.out.println("No search results for " + searchTerm);
            else {
                System.out.println("Here are the results for " + searchTerm + ":");
                displaySearchResult(searchResult);
            }
        }
    }

    private void displaySearchResult(List<Video> videos) {
        Scanner readIn = new Scanner(System.in);
        videos.sort(Comparator.comparing(Video::getTitle));

        for (int i = 0; i < videos.size(); i++) {
            System.out.println("\t" + (i + 1) + ") " + buildString(videos.get(i)));
        }
        System.out.println("Would you like to play any of the above? If yes, specify the number of the video.\n" +
                "If your answer is not a valid number, we will assume it's a no.");
        try {
            int response = readIn.nextInt();
            if (response > 0 && response <= videos.size())
                playVideo(videos.get(response - 1).getVideoId());
        } catch (InputMismatchException e) {
        }
    }

    public void searchVideosWithTag(String videoTag) {
        Matcher m = tagPattern.matcher(videoTag);
        if(unFlaggedVids.isEmpty()) {
            System.out.println("No videos available");
        }
        else if (m.matches()) {
            List<Video> searchResult = unFlaggedVids.stream()
                    .filter(a -> {
                        List<String> tags = a.getTags();
                        for (String tag : tags)
                            if (Pattern.compile(Pattern.quote(videoTag), Pattern.CASE_INSENSITIVE)
                                    .matcher(tag).find())
                                return true;
                        return false;
                    })
                    .collect(Collectors.toList());
            if (searchResult.isEmpty())
                System.out.println("No search results for " + videoTag);
            else {
                System.out.println("Here are the results for " + videoTag + ":");
                displaySearchResult(searchResult);
            }
        } else
            System.out.println("No search results for " + videoTag);
    }

    public void flagVideo(String videoId) {
        flagVideo(videoId, null);
    }

    public void flagVideo(String videoId, String reason) {
        Video video = videoLibrary.getVideo(videoId);
        if (video == null)
            System.out.println("Cannot flag video: Video does not exist");
        else if (video.isFlagged())
            System.out.println("Cannot flag video: Video is already flagged");
        else {
            video.setFlag(reason);
            unFlaggedVids.remove(video);
            if(playingVideo != null && playingVideo.getVideoId().equals(video.getVideoId()))
                stopVideo();
            System.out.println("Successfully flagged video: " + video.getTitle() + " (" +
                    "reason: " + video.getFlag() + ")");
        }
    }

    public void allowVideo(String videoId) {
        Video video = videoLibrary.getVideo(videoId);
        if (video == null)
            System.out.println("Cannot remove flag from video: Video does not exist");
        else if (!video.isFlagged())
            System.out.println("Cannot remove flag from video: Video is not flagged");
        else {
            video.removeFlag();
            unFlaggedVids.add(video);
            System.out.println("Successfully removed flag from video: "+ video.getTitle());
        }
    }
}
package com.example.podcatsv2;

// Video.java

public class Video {
    private String videoId;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private String userId;
    private String publicationTime;
    private int likeCount;

    // Default constructor (required for Firebase)
    public Video() {}

    // Getters and Setters
    public String getVideoId() {
        return videoId;
    }
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublicationTime() {
        return publicationTime;
    }
    public void setPublicationTime(String publicationTime) {
        this.publicationTime = publicationTime;
    }

    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}


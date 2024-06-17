package com.example.spike_player.Templates;

public class VideoTemplate {
    private String videotitle, videocategory, videoduration, videoSelectedUrl, imageSelectedUrl, id, userId , timestamp;
    private Integer likes,dislikes;

    public VideoTemplate(){
        // For firebase interactivity
    }
    public VideoTemplate(String videotitle, String videocategory, String videoduration, String videoSelectedUrl, String imageSelectedUrl, String id, String userId,String timestamp) {
        this.videotitle = videotitle;
        this.videocategory = videocategory;
        this.videoduration = videoduration;
        this.videoSelectedUrl = videoSelectedUrl;
        this.imageSelectedUrl = imageSelectedUrl;
        this.id=id;
        this.userId = userId;
        this.timestamp=timestamp;
    }

    public VideoTemplate(String videotitle, String videocategory, String videoduration, String videoSelectedUrl, String imageSelectedUrl, String id, String userId, Integer likes, Integer dislikes) {
        this.videotitle = videotitle;
        this.videocategory = videocategory;
        this.videoduration = videoduration;
        this.videoSelectedUrl = videoSelectedUrl;
        this.imageSelectedUrl = imageSelectedUrl;
        this.id = id;
        this.userId = userId;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideotitle() {
        return videotitle;
    }

    public void setVideotitle(String videotitle) {
        this.videotitle = videotitle;
    }

    public String getVideocategory() {
        return videocategory;
    }

    public void setVideocategory(String videocategory) {
        this.videocategory = videocategory;
    }

    public String getVideoduration() {
        return videoduration;
    }

    public void setVideoduration(String videoduration) {
        this.videoduration = videoduration;
    }

    public String getVideoSelectedUrl() {
        return videoSelectedUrl;
    }

    public void setVideoSelectedUrl(String videoSelectedUrl) {
        this.videoSelectedUrl = videoSelectedUrl;
    }

    public String getId() {
        return id;
    }

    public String getImageSelectedUrl() {
        return imageSelectedUrl;
    }

    public void setImageSelectedUrl(String imageSelectedUrl) {
        this.imageSelectedUrl = imageSelectedUrl;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}

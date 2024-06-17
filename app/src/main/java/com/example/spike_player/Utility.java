package com.example.spike_player;

public class Utility {

    static String currentVideoId,videoPostedBy;
     public String getCurrentVideoId(){
        return currentVideoId;
    }
     public void setCurrentVideoId(String id){
        currentVideoId=id;
    }

    public static String getVideoPostedBy() {
        return videoPostedBy;
    }

    public static void setVideoPostedBy(String videoPostedBy) {
        Utility.videoPostedBy = videoPostedBy;
    }
}

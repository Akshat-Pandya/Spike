package com.example.spike_player.Templates;

public class CommentTemplate {
    String commentId,comment,timestamp;

    String postedByName,postedByProfileImage;

    public CommentTemplate() {
    }

    public CommentTemplate(String commentId, String comment, String timestamp,String postedByName,String postedByProfileImage) {
        this.commentId = commentId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.postedByName=postedByName;
        this.postedByProfileImage=postedByProfileImage;
    }

    public CommentTemplate(String commentId, String comment, String timestamp) {
        this.commentId = commentId;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public String getPostedByProfileImage() {
        return postedByProfileImage;
    }

    public void setPostedByProfileImage(String postedByProfileImage) {
        this.postedByProfileImage = postedByProfileImage;
    }
}

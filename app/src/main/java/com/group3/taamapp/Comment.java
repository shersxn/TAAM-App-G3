package com.group3.taamapp;

// This class represents a user's comment on an artifact
// Firebase uses this class to process JSON data
public class Comment {
    private String commentId;
    private String username;
    private String text;

    // Firebase requires an empty constructor
    public Comment(){

    }
    public Comment(String commentId, String username, String text){
        this.commentId = commentId;
        this.username = username;
        this.text = text;
    }

    public String getCommentId(){
        return commentId;
    }

    public void setCommentId(String commentId){
        this.commentId = commentId;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

}
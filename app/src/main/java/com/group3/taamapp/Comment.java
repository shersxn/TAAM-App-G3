package com.group3.taamapp;

public class Comment {
    private String commentId;
    private String username;
    private String text;

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
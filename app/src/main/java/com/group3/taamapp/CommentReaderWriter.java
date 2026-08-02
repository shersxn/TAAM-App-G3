package com.group3.taamapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Helper class for managing Firebase comments
public class CommentReaderWriter {

    private DatabaseReference data;

    public CommentReaderWriter() {
        this.data = FirebaseDatabase.getInstance().getReference();
    }

    public boolean writeComment(Comment newComment, String lotNumber) {
        String commentId = newComment.getCommentId();

        data.child("Artifacts").child(lotNumber).child("comments")
                .child(commentId).setValue(newComment);

        return true;
    }

    public boolean removeComment(String commentId, String lotNumber) {
        data = FirebaseDatabase.getInstance().getReference();
        data.child("Artifacts").child(lotNumber).child("comments")
                .child(commentId).removeValue();

        return true;
    }
}
package com.group3.taamapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CommentAdapter extends FirebaseRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {

    private boolean isAdmin;
    private String lotNumber;
    private CommentReaderWriter commentWriter;

    public CommentAdapter(@NonNull FirebaseRecyclerOptions<Comment> options, boolean isAdmin,
                          String lotNumber) {
        super(options);
        this.isAdmin = isAdmin;
        this.lotNumber = lotNumber;
        this.commentWriter = new CommentReaderWriter();
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position,
                                    @NonNull Comment model) {
        holder.commenterTextView.setText(model.getUsername());
        holder.commentTextView.setText(model.getText());

        if (isAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean success = commentWriter.removeComment(model.getCommentId(), lotNumber);

                    if (success) {
                        Toast.makeText(v.getContext(), "Comment deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_comment_adapter, parent, false);
        return new CommentViewHolder(v);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commenterTextView;
        TextView commentTextView;
        Button deleteButton;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commenterTextView = itemView.findViewById(R.id.commenter);
            commentTextView = itemView.findViewById(R.id.comment);
            deleteButton = itemView.findViewById(R.id.delete_comment);
        }
    }
}
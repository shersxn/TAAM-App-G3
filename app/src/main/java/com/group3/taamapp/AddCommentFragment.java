package com.group3.taamapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCommentFragment extends Fragment {

    private EditText commentInput;
    private Button submitButton;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter adapter;
    private CommentReaderWriter commentWriter;

    private String currentLotNumber;
    private String currentUsername;
    private boolean isUserAdmin;

    public static AddCommentFragment newInstance(String lotNumber, String username,
                                                 boolean isAdmin) {
        AddCommentFragment fragment = new AddCommentFragment();
        Bundle args = new Bundle();
        args.putString("LOT_NUMBER", lotNumber);
        args.putString("USERNAME", username);
        args.putBoolean("IS_ADMIN", isAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentLotNumber = getArguments().getString("LOT_NUMBER");
            currentUsername = getArguments().getString("USERNAME");
            isUserAdmin = getArguments().getBoolean("IS_ADMIN");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_comment, container, false);

        commentInput = view.findViewById(R.id.comment);
        submitButton = view.findViewById(R.id.submit_comment_btn);
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentWriter = new CommentReaderWriter();

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference()
                .child("Artifacts").child(currentLotNumber).child("comments");

        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(commentsRef, Comment.class).build();

        adapter = new CommentAdapter(options, isUserAdmin, currentLotNumber);
        commentsRecyclerView.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = commentInput.getText().toString().trim();

                if (!text.isEmpty()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child("Artifacts").child(currentLotNumber)
                            .child("comments").push();
                    String newCommentId = ref.getKey();

                    Comment newComment = new Comment(newCommentId, currentUsername, text);
                    boolean success = commentWriter.writeComment(newComment, currentLotNumber);

                    if (success) {
                        Toast.makeText(getContext(), "Comment posted!", Toast.LENGTH_SHORT)
                                .show();
                        commentInput.setText("");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
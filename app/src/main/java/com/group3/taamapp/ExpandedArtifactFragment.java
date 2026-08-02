package com.group3.taamapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.group3.taamapp.Bases.BaseFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpandedArtifactFragment extends BaseFragment {

    public static final String ARG_EMAIL = "email";
    public static final String ARG_LOT_NUMBER = "lotNumber";

    private static final String NODE_ARTIFACTS = "Artifacts";
    private static final String NODE_USERS = "Users";
    private static final String FIELD_COLLECTIONS = "collections";

    private MaterialToolbar artifactToolbar;
    private ImageView artifactImageView;

    private TextView artifactNameTextView;
    private TextView dynastyPeriodTextView;
    private TextView descriptionTextView;
    private TextView lotNumberTextView;
    private TextView categoryTextView;
    private TextView materialTextView;
    private TextView likeCountTextView;

    private Chip categoryChip;
    private Chip materialChip;

    private MaterialButton likeButton;
    private MaterialButton saveButton;

    private String currentUserEmail;
    private String lotNumber;

    private DatabaseReference artifactReference;
    private DatabaseReference collectionsReference;

    private boolean isSaved;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_expanded_artifact;
    }

    @Override
    protected void setUIComponents(View view) {
        artifactToolbar = view.findViewById(R.id.artifactToolbar);
        artifactImageView = view.findViewById(R.id.artifactImageView);

        artifactNameTextView =
                view.findViewById(R.id.artifactNameTextView);

        dynastyPeriodTextView =
                view.findViewById(R.id.dynastyPeriodTextView);

        descriptionTextView =
                view.findViewById(R.id.descriptionTextView);

        lotNumberTextView =
                view.findViewById(R.id.lotNumberTextView);

        categoryTextView =
                view.findViewById(R.id.categoryTextView);

        materialTextView =
                view.findViewById(R.id.materialTextView);

        likeCountTextView =
                view.findViewById(R.id.likeCountTextView);

        categoryChip = view.findViewById(R.id.categoryChip);
        materialChip = view.findViewById(R.id.materialChip);

        likeButton = view.findViewById(R.id.likeButton);
        saveButton = view.findViewById(R.id.saveButton);

        readArguments();
        configureToolbar();

        if (!hasRequiredArguments()) {
            return;
        }

        artifactReference = FirebaseDatabase.getInstance()
                .getReference(NODE_ARTIFACTS)
                .child(lotNumber);

        loadArtifact();
        configureSaveFeature();
        configureComments();
    }

    @Override
    protected void setEvents() {
        // Like/Unlike to be done.
    }

    @Override
    protected void setPresenter() {

    }

    private void readArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        currentUserEmail = arguments.getString(ARG_EMAIL);
        lotNumber = arguments.getString(ARG_LOT_NUMBER);
    }

    private boolean hasRequiredArguments() {
        if (lotNumber == null || lotNumber.trim().isEmpty()) {
            showMessage("Artifact lot number is missing.");
            return false;
        }

        if (currentUserEmail == null
                || currentUserEmail.trim().isEmpty()) {
            showMessage("Current user email is missing.");
            return false;
        }

        return true;
    }

    private void configureToolbar() {
        artifactToolbar.setNavigationOnClickListener(view ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }

    private void loadArtifact() {
        artifactReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {
                        if (!snapshot.exists()) {
                            showMessage("Artifact not found.");
                            return;
                        }

                        String firebaseLotNumber = snapshot.getKey();
                        if (firebaseLotNumber == null
                                || firebaseLotNumber.trim().isEmpty()) {
                            showMessage("Artifact lot number is missing.");
                            return;
                        }

                        lotNumber = firebaseLotNumber;
                        displayArtifact(snapshot);
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {
                        showMessage(
                                "Unable to load artifact: "
                                        + error.getMessage()
                        );
                    }
                }
        );
    }

    private void displayArtifact(
            @NonNull DataSnapshot snapshot
    ) {
        String name = getStringValue(snapshot, "name");
        String description =
                getStringValue(snapshot, "description");
        String category =
                getStringValue(snapshot, "category");
        String material =
                getStringValue(snapshot, "material");
        String dynasty =
                getStringValue(snapshot, "dynasty");
        String imageUrl =
                getStringValue(snapshot, "imageUrl");

        artifactNameTextView.setText(name);
        descriptionTextView.setText(description);
        categoryTextView.setText(category);
        materialTextView.setText(material);
        dynastyPeriodTextView.setText(dynasty);
        lotNumberTextView.setText(lotNumber);

        categoryChip.setText(category);
        materialChip.setText(material);

        long likeCount =
                snapshot.child("likes").getChildrenCount();

        likeCountTextView.setText(
                getString(
                        R.string.like_count_format,
                        likeCount
                )
        );

        loadArtifactImage(imageUrl);
    }

    private String getStringValue(
            @NonNull DataSnapshot snapshot,
            @NonNull String key
    ) {
        String value =
                snapshot.child(key).getValue(String.class);

        return value == null ? "" : value;
    }

    private void loadArtifactImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            artifactImageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
            return;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(
                        android.R.drawable.ic_menu_gallery
                )
                .error(
                        android.R.drawable.ic_menu_report_image
                )
                .into(artifactImageView);
    }

    private void configureSaveFeature() {
        saveButton.setEnabled(false);

        String encodedEmail = encodeEmail(currentUserEmail);

        collectionsReference =
                FirebaseDatabase.getInstance()
                        .getReference(NODE_USERS)
                        .child(encodedEmail)
                        .child(FIELD_COLLECTIONS);

        loadSaveState();

        saveButton.setOnClickListener(view ->
                toggleSave()
        );
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void loadSaveState() {
        collectionsReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {
                                isSaved = false;

                                for (DataSnapshot child
                                        : snapshot.getChildren()) {
                                    String savedLotNumber =
                                            child.getValue(
                                                    String.class
                                            );

                                    if (lotNumber.equals(
                                            savedLotNumber
                                    )) {
                                        isSaved = true;
                                        break;
                                    }
                                }

                                updateSaveButton();
                                saveButton.setEnabled(true);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {
                                saveButton.setEnabled(true);
                                showMessage(
                                        "Unable to load saved status."
                                );
                            }
                        }
                );
    }

    private void toggleSave() {
        saveButton.setEnabled(false);

        collectionsReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {
                                if (isSaved) {
                                    removeFromCollections(snapshot);
                                } else {
                                    addToCollections();
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {
                                saveButton.setEnabled(true);
                                showMessage(
                                        "Unable to update collection."
                                );
                            }
                        }
                );
    }

    private void addToCollections() {
        collectionsReference.push()
                .setValue(lotNumber)
                .addOnSuccessListener(unused -> {
                    isSaved = true;
                    saveButton.setEnabled(true);
                    updateSaveButton();
                    showMessage("Artifact saved.");
                })
                .addOnFailureListener(error -> {
                    saveButton.setEnabled(true);
                    showMessage("Failed to save artifact.");
                });
    }

    private void removeFromCollections(
            @NonNull DataSnapshot snapshot
    ) {
        DatabaseReference matchingReference = null;

        for (DataSnapshot child : snapshot.getChildren()) {
            String savedLotNumber =
                    child.getValue(String.class);

            if (lotNumber.equals(savedLotNumber)) {
                matchingReference = child.getRef();
                break;
            }
        }

        if (matchingReference == null) {
            isSaved = false;
            saveButton.setEnabled(true);
            updateSaveButton();
            return;
        }

        matchingReference.removeValue()
                .addOnSuccessListener(unused -> {
                    isSaved = false;
                    saveButton.setEnabled(true);
                    updateSaveButton();
                    showMessage(
                            "Artifact removed from collection."
                    );
                })
                .addOnFailureListener(error -> {
                    saveButton.setEnabled(true);
                    showMessage(
                            "Failed to remove artifact."
                    );
                });
    }

    private void updateSaveButton() {
        saveButton.setText(
                isSaved ? "Unsave" : "Save"
        );
    }

    private void showMessage(String message) {
        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    public String getCurrentLotNumber() {
        return lotNumber;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    private void configureComments() {
        String encodedEmail = encodeEmail(currentUserEmail);

        DatabaseReference userReference =
                FirebaseDatabase.getInstance()
                        .getReference(NODE_USERS)
                        .child(encodedEmail);

        userReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {
                        String username =
                                snapshot.child("username")
                                        .getValue(String.class);

                        Boolean adminValue =
                                snapshot.child("admin")
                                        .getValue(Boolean.class);

                        if (username == null
                                || username.trim().isEmpty()) {
                            username = currentUserEmail;
                        }

                        boolean isAdmin =
                                adminValue != null && adminValue;

                        displayCommentFragment(
                                username,
                                isAdmin
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {
                        displayCommentFragment(
                                currentUserEmail,
                                false
                        );
                    }
                }
        );
    }

    private void displayCommentFragment(
            String username,
            boolean isAdmin
    ) {
        AddCommentFragment commentFragment =
                AddCommentFragment.newInstance(
                        lotNumber,
                        username,
                        isAdmin
                );

        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.commentFragmentContainer,
                        commentFragment
                )
                .commit();
    }
}
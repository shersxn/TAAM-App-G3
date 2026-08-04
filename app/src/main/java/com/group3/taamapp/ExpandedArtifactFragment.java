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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ExpandedArtifactFragment extends BaseFragment {

    // two bundle keys
    public static final String ARG_EMAIL = "email";
    public static final String ARG_LOT_NUMBER = "lotNumber";

    // firebase node names
    private static final String NODE_ARTIFACTS = "Artifacts";
    private static final String NODE_USERS = "Users";
    private static final String FIELD_COLLECTIONS = "collections";

    // page control variables
    private MaterialToolbar artifactToolbar;
    private ImageView artifactImageView;

    private TextView artifactNameTextView;
    private TextView dynastyPeriodTextView;
    private TextView dynastyPeriodDetailTextView;
    private TextView descriptionTextView;
    private TextView lotNumberTextView;
    private TextView categoryTextView;
    private TextView materialTextView;
    private TextView likeCountTextView;

    private Chip categoryChip;
    private Chip materialChip;

    private MaterialButton likeButton;
    private MaterialButton saveButton;

    //variables for the status of current page
    private String currentUserEmail;
    private String lotNumber;

    private DatabaseReference artifactReference;
    private DatabaseReference collectionsReference;
    private DatabaseReference likesReference;
    private boolean isLiked;
    private boolean isSaved;

    // admin control variables
    private View adminControlsLayout;
    private MaterialButton editArtifactButton;
    private MaterialButton deleteArtifactButton;
    private boolean isCurrentUserAdmin;

    // Related Artifact Variables
    private RecyclerView relatedRecyclerView;
    private ViewCardAdapter relatedAdapter;
    private ArrayList<ExpandedArtifact> relatedArtifactsList;

    // initialize the page
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_expanded_artifact;
    }

    @Override
    protected void setUIComponents(View view) {
        // connect admin controls to their corresponding XML views
        adminControlsLayout = view.findViewById(R.id.adminControlsLayout);
        editArtifactButton = view.findViewById(R.id.editArtifactButton);
        deleteArtifactButton = view.findViewById(R.id.deleteArtifactButton);
        // connect the toolbar, image, and artifact information views
        artifactToolbar = view.findViewById(R.id.artifactToolbar);
        artifactImageView = view.findViewById(R.id.artifactImageView);
        artifactNameTextView = view.findViewById(R.id.artifactNameTextView);
        dynastyPeriodTextView = view.findViewById(R.id.dynastyPeriodTextView);
        dynastyPeriodDetailTextView = view.findViewById(R.id.dynastyPeriodDetailTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        lotNumberTextView = view.findViewById(R.id.lotNumberTextView);
        categoryTextView = view.findViewById(R.id.categoryTextView);
        materialTextView = view.findViewById(R.id.materialTextView);
        likeCountTextView = view.findViewById(R.id.likeCountTextView);
        // connect the category and material chips
        categoryChip = view.findViewById(R.id.categoryChip);
        materialChip = view.findViewById(R.id.materialChip);
        // connect like and save buttons
        likeButton = view.findViewById(R.id.likeButton);
        saveButton = view.findViewById(R.id.saveButton);

        readArguments();
        configureToolbar();

        // stop initialization if required args are missing
        if (!hasRequiredArguments()) {
            return;
        }

        // create a firebase reference for current artifact and its likes
        artifactReference = FirebaseDatabase.getInstance()
                .getReference(NODE_ARTIFACTS)
                .child(lotNumber);
        likesReference = FirebaseDatabase.getInstance()
                .getReference(NODE_ARTIFACTS)
                .child(lotNumber)
                .child("likes");

        // load the artifact and configure the page features
        loadArtifact();
        configureSaveFeature();
        configureComments();
        configureAdminControls();
        configureLikeFeature();

        // Setup Related Artifacts Bonus
        relatedRecyclerView = view.findViewById(R.id.related_recycler_view);
        relatedArtifactsList = new ArrayList<>();

        // Make the list scroll sideways
        relatedRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));

        relatedAdapter = new ViewCardAdapter(requireContext(), relatedArtifactsList, null);
        relatedRecyclerView.setAdapter(relatedAdapter);
    }

    // two methods required by BaseFragment
    @Override
    protected void setEvents() {
    }

    @Override
    protected void setPresenter() {

    }

    // read current user email and artifact lot number
    private void readArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        currentUserEmail = arguments.getString(ARG_EMAIL);
        lotNumber = arguments.getString(ARG_LOT_NUMBER);
    }

    // check if lot number and user email exists
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

    // set up the return button on the tool bar
    private void configureToolbar() {
        artifactToolbar.setNavigationOnClickListener(view ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }

    // read artifact data from firebase once
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

    // display firebase data on xml
    private void displayArtifact(
            @NonNull DataSnapshot snapshot
    ) {
        String name = getStringValue(snapshot, "artifactName");
        String description =
                getStringValue(snapshot, "description");
        String category =
                getStringValue(snapshot, "category");
        String material =
                getStringValue(snapshot, "material");
        String dynasty =
                getStringValue(snapshot, "dynastyPeriod");
        String imageUrl =
                getStringValue(snapshot, "imageUrl");

        artifactNameTextView.setText(name);
        descriptionTextView.setText(description);
        categoryTextView.setText(category);
        materialTextView.setText(material);
        dynastyPeriodTextView.setText(dynasty);
        dynastyPeriodDetailTextView.setText(dynasty);
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

        loadRelatedArtifacts(category, lotNumber);
    }

    // a helper method to avoid reading firebase fields repeatedly
    private String getStringValue(
            @NonNull DataSnapshot snapshot,
            @NonNull String key
    ) {
        String value =
                snapshot.child(key).getValue(String.class);

        return value == null ? "" : value;
    }

    // load artifact images using Glide
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

    // configure the feature for saving artifacts
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

    // method for liking artifacts
    private void toggleLike(String encodedEmail) {
        likeButton.setEnabled(false);

        if (isLiked) {
            // Remove the like
            likesReference.child(encodedEmail).removeValue()
                    .addOnSuccessListener(unused -> {
                        // Ask the database for the new total after unliking
                        likesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!isAdded()) {
                                    return;
                                }
                                long newLikeCount = snapshot.getChildrenCount();
                                likeCountTextView.setText(getString(R.string.like_count_format, newLikeCount));
                                likeButton.setEnabled(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                likeButton.setEnabled(true);
                            }
                        });
                    })
                    .addOnFailureListener(error -> {
                        likeButton.setEnabled(true);
                        showMessage("Failed to unlike artifact.");
                    });
        } else {
            // Add the like
            likesReference.child(encodedEmail).setValue(true)
                    .addOnSuccessListener(unused -> {
                        // Ask the database for the new total after liking
                        likesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!isAdded()) {
                                    return;
                                }
                                long newLikeCount = snapshot.getChildrenCount();
                                likeCountTextView.setText(getString(R.string.like_count_format, newLikeCount));
                                likeButton.setEnabled(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                likeButton.setEnabled(true);
                            }
                        });
                    })
                    .addOnFailureListener(error -> {
                        likeButton.setEnabled(true);
                        showMessage("Failed to like artifact.");
                    });
        }
    }

    private void updateLikeButton() {
        // Update like button text based on if the user already liked or hasn't
        likeButton.setText(isLiked ? "Unlike" : "Like");
    }
    private void configureLikeFeature() {
        likeButton.setEnabled(false);
        String encodedEmail = encodeEmail(currentUserEmail);
        likesReference.child(encodedEmail).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // If the user's email exists, they liked it
                        isLiked = snapshot.exists();
                        updateLikeButton();
                        likeButton.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        likeButton.setEnabled(true);
                        showMessage("Unable to load like status.");
                    }
                }
        );
        likeButton.setOnClickListener(view -> toggleLike(encodedEmail));
    }
    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    // read the current user's saved list and check if the current
    // artifact is in the list
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

    // save/unsave based on reading from the save list
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
        // create a firebase key and save lot number in it
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

    // traverse the user's save list and search for the current artifact
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

    // update the save button based on current save state
    private void updateSaveButton() {
        saveButton.setText(
                isSaved ? "Unsave" : "Save"
        );
    }

    // show a message at the bottom to indicate successful operation
    private void showMessage(String message) {
        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // two methods to let other classes to read user email and lot number
    public String getCurrentLotNumber() {
        return lotNumber;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    private void configureComments() {
        String encodedEmail = encodeEmail(currentUserEmail);

        // get current user information
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

                        if (username == null
                                || username.trim().isEmpty()) {
                            username = currentUserEmail;
                        }

                        // get the admin status
                        boolean isAdmin =
                                Boolean.TRUE.equals(
                                        snapshot.child("admin").getValue(Boolean.class)
                                );

                        // save the admin status
                        isCurrentUserAdmin = isAdmin;
                        // show admin control buttons
                        adminControlsLayout.setVisibility(
                                isAdmin ? View.VISIBLE : View.GONE
                        );

                        displayCommentFragment(username, isAdmin);

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

    // create the comment fragment and pass in lot number, user email, and admin status
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

    private void configureAdminControls() {
        editArtifactButton.setOnClickListener(view -> {
            // double check the admin status
            if (!isCurrentUserAdmin) {
                showMessage("Admin access required.");
                return;
            }

            // admin edit button
            loadFragment(
                    new AddEditArtifactFragment(),
                    bundle -> {
                        bundle.putString("userEmail", currentUserEmail);
                        bundle.putString("artifactId", lotNumber);
                    }
            );
        });

        // admin delete feature
        deleteArtifactButton.setOnClickListener(view -> {
            if (!isCurrentUserAdmin) {
                showMessage("Admin access required.");
                return;
            }

            showDeleteConfirmation();
        });
    }

    // method for users to confirm before deleting an artifact
    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete artifact?")
                .setMessage(
                        "This action cannot be undone. "
                                + "Are you sure you want to delete this artifact?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteCurrentArtifact()
                )
                .show();
    }

    // method for deleting
    private void deleteCurrentArtifact() {
        deleteArtifactButton.setEnabled(false);

        artifactReference.removeValue()
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) {
                        return;
                    }

                    showMessage("Artifact deleted.");

                    getParentFragmentManager()
                            .popBackStack();
                })
                .addOnFailureListener(error -> {
                    if (!isAdded()) {
                        return;
                    }

                    deleteArtifactButton.setEnabled(true);
                    showMessage("Failed to delete artifact.");
                });
    }

    // This method loads the first 5 related artifacts
    private void loadRelatedArtifacts(String category, String currentLotNumber) {
        if (category == null || category.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(NODE_ARTIFACTS);

        // Find the first 5 artifacts with the exact same category
        // Set the limit to 6 in case one of the artifacts is the current one
        ref.orderByChild("category").equalTo(category).limitToFirst(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        relatedArtifactsList.clear();

                        Iterable<DataSnapshot> relatedItemsList = snapshot.getChildren();
                        for (DataSnapshot child : relatedItemsList) {
                            String lotNum = child.getKey();

                            // This is to make sure the user doesn't see the same artifact as the
                            // one they are currently looking at
                            if (lotNum != null && !lotNum.equals(currentLotNumber)) {
                                ExpandedArtifact artifact = child.getValue(ExpandedArtifact.class);
                                if (artifact != null) {
                                    artifact.setLotNumber(lotNum);
                                    relatedArtifactsList.add(artifact);
                                }
                            }

                            if (relatedArtifactsList.size() == 5) {
                                break;
                            }
                        }
                        relatedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // This method does nothing, It's just here so that the related artifacts
                        // section remains blank
                    }
                });
    }
}
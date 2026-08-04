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
    private DatabaseReference likesReference;
    private boolean isLiked;
    private boolean isSaved;
    private View adminControlsLayout;
    private MaterialButton editArtifactButton;
    private MaterialButton deleteArtifactButton;
    private boolean isCurrentUserAdmin;

    // Related Artifact Variables
    private RecyclerView relatedRecyclerView;
    private ViewCardAdapter relatedAdapter;
    private ArrayList<ExpandedArtifact> relatedArtifactsList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_expanded_artifact;
    }

    @Override
    protected void setUIComponents(View view) {
        adminControlsLayout = view.findViewById(R.id.adminControlsLayout);
        editArtifactButton = view.findViewById(R.id.editArtifactButton);
        deleteArtifactButton = view.findViewById(R.id.deleteArtifactButton);
        artifactToolbar = view.findViewById(R.id.artifactToolbar);
        artifactImageView = view.findViewById(R.id.artifactImageView);

        artifactNameTextView = view.findViewById(R.id.artifactNameTextView);

        dynastyPeriodTextView = view.findViewById(R.id.dynastyPeriodTextView);

        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        lotNumberTextView = view.findViewById(R.id.lotNumberTextView);

        categoryTextView = view.findViewById(R.id.categoryTextView);

        materialTextView = view.findViewById(R.id.materialTextView);

        likeCountTextView = view.findViewById(R.id.likeCountTextView);

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
        likesReference = FirebaseDatabase.getInstance()
                .getReference(NODE_ARTIFACTS)
                .child(lotNumber)
                .child("likes");
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

    @Override
    protected void setEvents() {
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

        loadRelatedArtifacts(category, lotNumber);
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
                                Boolean.TRUE.equals(
                                        snapshot.child("admin").getValue(Boolean.class)
                                );

                        isCurrentUserAdmin = isAdmin;
                        adminControlsLayout.setVisibility(
                                isAdmin ? View.VISIBLE : View.GONE
                        );

                        displayCommentFragment(username, isAdmin);

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

    private void configureAdminControls() {
        editArtifactButton.setOnClickListener(view -> {
            if (!isCurrentUserAdmin) {
                showMessage("Admin access required.");
                return;
            }

            loadFragment(
                    new AddEditArtifactFragment(),
                    bundle -> {
                        bundle.putString("userEmail", currentUserEmail);
                        bundle.putString("artifactId", lotNumber);
                    }
            );
        });

        deleteArtifactButton.setOnClickListener(view -> {
            if (!isCurrentUserAdmin) {
                showMessage("Admin access required.");
                return;
            }

            showDeleteConfirmation();
        });
    }

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
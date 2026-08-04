package com.group3.taamapp.SavedArtifactPage;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.card.MaterialCardView;
import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Bases.BundleInitializer;
import com.group3.taamapp.Contract.SavedArtifactContract;
import com.group3.taamapp.ExpandedArtifact;
import com.group3.taamapp.ExpandedArtifactFragment;
import com.group3.taamapp.Model.SavedArtifactModelFirebase;
import com.group3.taamapp.R;

/**
 * Displays the user's saved artifacts and handles the empty collection state.
 */
public class SavedArtifactFragment extends BaseFragment
        implements SavedArtifactContract.View {

    /**
     * Email passed into this fragment by the previous screen.
     */
    public static final String ARG_EMAIL = "email";

    // Presenter handles saved-artifact logic
    private SavedArtifactContract.Presenter presenter;

    // Main saved artifact interface
    private RecyclerView recyclerView;
    private LinearLayout collectionHeading;
    private MaterialCardView collectionCard;

    // Displayed when the user has no saved artifacts
    private LinearLayout emptyStateLayout;

    private SavedArtifactAdapter adapter;
    private final List<ExpandedArtifact> savedArtifacts = new ArrayList<>();

    private String currentUserEmail;

    @Override
    protected int getLayoutId() {
        return R.layout.saved_collection;
    }

    @Override
    protected void setUIComponents(View view) {
        // Connect the fragment fields to their XML views
        recyclerView = view.findViewById(R.id.rv_saved_artifacts);
        emptyStateLayout = view.findViewById(R.id.layout_empty_state);
        collectionHeading = view.findViewById(R.id.layout_collection_heading);
        collectionCard = view.findViewById(R.id.card_artifact_collection);

        // Get the current user's email from the fragment arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentUserEmail = arguments.getString(ARG_EMAIL);
        }

        // Set up the saved artifact list
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SavedArtifactAdapter(
                savedArtifacts,
                new SavedArtifactAdapter.OnArtifactActionListener() {
                    @Override
                    public void onUnsave(ExpandedArtifact artifact) {
                        presenter.unsaveArtifact(artifact);
                    }

                    @Override
                    public void onOpenArtifact(ExpandedArtifact artifact) {
                        presenter.openArtifact(artifact);
                    }
                }
        );

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setEvents() {
        // Card actions are handled through the RecyclerView adapter
    }

    @Override
    protected void setPresenter() {
        // Create the presenter and load the user's saved artifacts
        presenter = new SavedArtifactPresenter(
                this,
                new SavedArtifactModelFirebase(getContext())
        );

        presenter.loadSavedArtifacts();
    }

    @Override
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    @Override
    public void showSavedArtifacts(List<ExpandedArtifact> savedArtifacts) {
        // Replace the current list with the newly loaded artifacts
        this.savedArtifacts.clear();
        this.savedArtifacts.addAll(savedArtifacts);
        adapter.notifyDataSetChanged();

        // Show the saved artifact collection
        collectionHeading.setVisibility(View.VISIBLE);
        collectionCard.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        // Hide the empty state
        emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState() {
        // Clear the list before showing the empty state
        savedArtifacts.clear();
        adapter.notifyDataSetChanged();

        collectionHeading.setVisibility(View.GONE);
        collectionCard.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeArtifactFromList(ExpandedArtifact artifact) {
        int index = savedArtifacts.indexOf(artifact);

        // Stop if the artifact is not currently displayed
        if (index == -1) {
            return;
        }

        // Remove the artifact and update the RecyclerView
        savedArtifacts.remove(index);
        adapter.notifyItemRemoved(index);

        // Show the empty state when the final artifact is removed
        if (savedArtifacts.isEmpty()) {
            showEmptyState();
        }
    }

    @Override
    public void toExpandedArtifact(ExpandedArtifact artifact) {
        // Open the selected artifact and pass its required information
        loadFragment(new ExpandedArtifactFragment(), new BundleInitializer() {
            @Override
            public void initBundle(Bundle bundle) {
                bundle.putString(
                        ExpandedArtifactFragment.ARG_EMAIL,
                        currentUserEmail
                );

                bundle.putString(
                        ExpandedArtifactFragment.ARG_LOT_NUMBER,
                        artifact.getLotNumber()
                );
            }
        });
    }
}
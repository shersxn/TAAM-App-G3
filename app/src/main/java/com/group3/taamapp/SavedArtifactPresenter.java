package com.group3.taamapp;

import java.util.List;

import com.group3.taamapp.Contract.SavedArtifactContract;
import com.group3.taamapp.ExpandedArtifact;
import com.group3.taamapp.Model.SavedArtifactModel;

/**
 * Handles the logic for loading, removing, and opening saved artifacts.
 */
public class SavedArtifactPresenter implements SavedArtifactContract.Presenter {

    private final SavedArtifactContract.View view;
    private final SavedArtifactModel model;

    public SavedArtifactPresenter(SavedArtifactContract.View view,
                                  SavedArtifactModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void loadSavedArtifacts() {
        String userEmail = view.getCurrentUserEmail();

        // Saved artifacts can only be loaded for a logged in user
        if (userEmail == null || userEmail.isEmpty()) {
            view.toastMakeText(
                    "You must be logged in to view your saved artifacts."
            );
            view.showEmptyState();
            return;
        }

        // Get the user's saved artifacts from the model
        model.getSavedArtifacts(
                userEmail,
                new SavedArtifactModel.SavedArtifactsCallback() {
                    @Override
                    public void onSuccess(
                            List<ExpandedArtifact> savedArtifacts
                    ) {
                        // Display either the saved collection or empty state
                        if (savedArtifacts == null
                                || savedArtifacts.isEmpty()) {
                            view.showEmptyState();
                        } else {
                            view.showSavedArtifacts(savedArtifacts);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        view.toastMakeText("Error: " + errorMessage);
                        view.showEmptyState();
                    }
                }
        );
    }

    @Override
    public void unsaveArtifact(ExpandedArtifact artifact) {
        // Stop if no valid artifact was provided
        if (artifact == null) {
            return;
        }

        String userEmail = view.getCurrentUserEmail();

        // A logged in user is required to modify the collection
        if (userEmail == null || userEmail.isEmpty()) {
            view.toastMakeText(
                    "You must be logged in to modify your saved artifacts."
            );
            return;
        }

        // Remove the selected artifact from the user's saved collection
        model.unsaveArtifact(
                userEmail,
                artifact.getLotNumber(),
                new SavedArtifactModel.UnsaveCallback() {
                    @Override
                    public void onSuccess() {
                        view.removeArtifactFromList(artifact);
                        view.toastMakeText(
                                "Removed from your collection."
                        );
                    }

                    @Override
                    public void onError(String errorMessage) {
                        view.toastMakeText("Error: " + errorMessage);
                    }
                }
        );
    }

    @Override
    public void openArtifact(ExpandedArtifact artifact) {
        // Open the expanded page for the selected artifact
        if (artifact != null) {
            view.toExpandedArtifact(artifact);
        }
    }
}
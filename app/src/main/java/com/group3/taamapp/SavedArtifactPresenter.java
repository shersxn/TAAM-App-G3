package com.group3.taamapp.SavedArtifactPage;

import java.util.List;

import com.group3.taamapp.Contract.SavedArtifactContract;
import com.group3.taamapp.ExpandedArtifact;
import com.group3.taamapp.Model.SavedArtifactModel;

public class SavedArtifactPresenter implements SavedArtifactContract.Presenter {
    private final SavedArtifactContract.View view;
    private final SavedArtifactModel model;

    public SavedArtifactPresenter(SavedArtifactContract.View view, SavedArtifactModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void loadSavedArtifacts() {
        String userEmail = view.getCurrentUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            view.toastMakeText("You must be logged in to view your saved artifacts.");
            view.showEmptyState();
            return;
        }

        model.getSavedArtifacts(userEmail, new SavedArtifactModel.SavedArtifactsCallback() {
            @Override
            public void onSuccess(List<ExpandedArtifact> savedArtifacts) {
                if (savedArtifacts == null || savedArtifacts.isEmpty()) {
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
        });
    }

    @Override
    public void unsaveArtifact(ExpandedArtifact artifact) {
        if (artifact == null) {
            return;
        }
        String userEmail = view.getCurrentUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            view.toastMakeText("You must be logged in to modify your saved artifacts.");
            return;
        }

        model.unsaveArtifact(userEmail, artifact.getLotNumber(), new SavedArtifactModel.UnsaveCallback() {
            @Override
            public void onSuccess() {
                view.removeArtifactFromList(artifact);
                view.toastMakeText("Removed from your collection.");
            }

            @Override
            public void onError(String errorMessage) {
                view.toastMakeText("Error: " + errorMessage);
            }
        });
    }

    @Override
    public void openArtifact(ExpandedArtifact artifact) {
        if (artifact == null) {
            return;
        }
        view.toExpandedArtifact(artifact);
    }
}

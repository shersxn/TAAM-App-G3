package com.group3.taamapp.Model;

import java.util.List;

import com.group3.taamapp.ExpandedArtifact;

public interface SavedArtifactModel {

    public static interface SavedArtifactsCallback {
        public abstract void onSuccess(List<ExpandedArtifact> savedArtifacts);

        public abstract void onError(String errorMessage);
    }

    public static interface UnsaveCallback {
        public abstract void onSuccess();

        public abstract void onError(String errorMessage);
    }

    public static interface SaveCallback {
        public abstract void onSuccess();

        public abstract void onError(String errorMessage);
    }

    // Retrieves the full artifact records the given user has saved
    public abstract void getSavedArtifacts(String userEmail, SavedArtifactsCallback callback);

    // Saves an artifact (by lot number) to the given user's collection
    public abstract void saveArtifact(String userEmail, String lotNumber, SaveCallback callback);

    // Removes an artifact (by lot number) from the given user's collection
    public abstract void unsaveArtifact(String userEmail, String lotNumber, UnsaveCallback callback);
}

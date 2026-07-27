package com.group3.taamapp.Contract;

import java.util.List;

import com.group3.taamapp.Bases.BaseViewContract;
import com.group3.taamapp.ExpandedArtifact;

public interface SavedArtifactContract {

    public static interface Presenter {
        // Fetches the current user's saved artifacts and pushes them to the view
        public abstract void loadSavedArtifacts();

        // Removes an artifact from the current user's saved collection
        public abstract void unsaveArtifact(ExpandedArtifact artifact);

        // Requests navigation to the expanded view for the given artifact
        public abstract void openArtifact(ExpandedArtifact artifact);
    }

    public static interface View extends BaseViewContract {
        // The logged-in user's email, used as the key for looking up saved artifacts
        public abstract String getCurrentUserEmail();

        // Renders the full list of saved artifacts
        public abstract void showSavedArtifacts(List<ExpandedArtifact> savedArtifacts);

        // Shown when the user has no saved artifacts
        public abstract void showEmptyState();

        // Removes a single artifact card from the currently displayed list (after unsaving)
        public abstract void removeArtifactFromList(ExpandedArtifact artifact);

        // Navigates to the expanded artifact view
        public abstract void toExpandedArtifact(ExpandedArtifact artifact);
    }
}

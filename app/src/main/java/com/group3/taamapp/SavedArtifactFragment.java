package com.group3.taamapp.SavedArtifactPage;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Bases.BundleInitializer;
import com.group3.taamapp.Contract.SavedArtifactContract;
import com.group3.taamapp.ExpandedArtifact;
import com.group3.taamapp.ExpandedArtifactFragment;
import com.group3.taamapp.Model.SavedArtifactModelFirebase;
import com.group3.taamapp.R;

/**
 * Implementation of SavedArtifactContract.View, as a Fragment (was SavedArtifactActivity.java, which
 * is now SavedArtifactPresenter)
 */
public class SavedArtifactFragment extends BaseFragment implements SavedArtifactContract.View {

    /**
     * Whichever fragment navigates here must pass this 
     */
    public static final String ARG_EMAIL = "email";

    private SavedArtifactContract.Presenter presenter;

    private RecyclerView recyclerView;
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
        recyclerView = view.findViewById(R.id.rv_saved_artifacts);
        emptyStateLayout = view.findViewById(R.id.layout_empty_state);

        // Getting data that was sent to this fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentUserEmail = arguments.getString(ARG_EMAIL);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavedArtifactAdapter(savedArtifacts, new SavedArtifactAdapter.OnArtifactActionListener() {
            @Override
            public void onUnsave(ExpandedArtifact artifact) {
                presenter.unsaveArtifact(artifact);
            }

            @Override
            public void onOpenArtifact(ExpandedArtifact artifact) {
                presenter.openArtifact(artifact);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setEvents() {
        // No buttons on this screen besides what's inside each card
    }

    @Override
    protected void setPresenter() {
        presenter = new SavedArtifactPresenter(this, new SavedArtifactModelFirebase(getContext()));
        presenter.loadSavedArtifacts();
    }

    @Override
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    @Override
    public void showSavedArtifacts(List<ExpandedArtifact> savedArtifacts) {
        this.savedArtifacts.clear();
        this.savedArtifacts.addAll(savedArtifacts);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState() {
        savedArtifacts.clear();
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeArtifactFromList(ExpandedArtifact artifact) {
        int index = savedArtifacts.indexOf(artifact);
        if (index == -1) return;
        savedArtifacts.remove(index);
        adapter.notifyItemRemoved(index);
        if (savedArtifacts.isEmpty()) {
            showEmptyState();
        }
    }

    @Override
    public void toExpandedArtifact(ExpandedArtifact artifact) {
        // Go to another fragment, pass data to the fragment we're going to, we override
        // initBundle() to fill in exactly what ExpandedArtifactFragment expects
        loadFragment(new ExpandedArtifactFragment(), new BundleInitializer() {
            @Override
            public void initBundle(Bundle bundle) {
                bundle.putString(ExpandedArtifactFragment.ARG_EMAIL, currentUserEmail);
                bundle.putString(ExpandedArtifactFragment.ARG_LOT_NUMBER, artifact.getLotNumber());
            }
        });
    }
}
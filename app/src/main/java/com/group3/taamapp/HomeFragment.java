package com.group3.taamapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Bases.BundleInitializer;

public class HomeFragment extends BaseFragment {

    public static final String ARG_EMAIL = "email";

    private ArrayList<ExpandedArtifact> viewCards = new ArrayList<>();
    private ViewCardAdapter adapter;
    private View homePage;
    private SearchView searchArtifact;
    private RecyclerView recyclerView;
    private ImageButton logout;
    private String currentUserEmail;

    @Override
    protected int getLayoutId() {
        return R.layout.artifacts_view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setUIComponents(View view) {
        readArguments();
        // Connect fragment fields to their XML views
        homePage = view.findViewById(R.id.homePage);
        recyclerView = view.findViewById(R.id.artefactsRecycle);
        searchArtifact = view.findViewById(R.id.searchView);
        logout = view.findViewById(R.id.logoutBtn);
        // Set up view artifacts list
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ViewCardAdapter(requireContext(), viewCards,
                new ViewCardAdapter.OnArtifactActionListener() {
                    @Override
                    public void onOpenArtifact(ExpandedArtifact artifact) {
                        toExpandedArtifact(artifact);
                    }
                });
        recyclerView.setAdapter(adapter);
        // Display view artifacts list
        setUpViewCards();
    }

    // Set up event listeners
    @Override
    protected void setEvents() {
        // Filter artifacts as the user types in the search bar
        searchArtifact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // Open the logout page
        logout.setOnClickListener(item -> {
            loadFragment(new LogoutFragment(), null);
        });

        // Unfocus the search bar when user clicks anywhere on the home page
        homePage.setOnClickListener(item -> {
            homePage.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(searchArtifact.getWindowToken(), 0);
            }
        });

        // Unfocus the search bar when user clicks on RecyclerView
        recyclerView.setOnTouchListener((v, event) -> {
            homePage.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(searchArtifact.getWindowToken(), 0);
            }
            return false;
        });
    }

    // Check whether any field of the artifact mathes the search keyword
    private boolean isMatch(ExpandedArtifact card, String newText) {
        String[] fields = {
                card.getLotNumber(),
                card.getArtifactName(),
                card.getDescription(),
                card.getCategory(),
                card.getMaterial(),
                card.getDynastyPeriod(),
                card.getCulturalOrigin(),
                card.getDimensions(),
                card.getConditionReport(),
                card.getCurrentLocation(),
                card.getAcquisitionMethod(),
                card.getProvenance(),
                card.getAccessionNumber(),
                card.getNotes()
        };
        String keyword = newText.toLowerCase();

        for (String field : fields) {
            if (field != null && field.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Display cards matching the search keyword
    private void filter(String newText) {
        ArrayList<ExpandedArtifact> filteredList = new ArrayList<>();
        for (ExpandedArtifact card : viewCards) {
            if (isMatch(card, newText)) {
                filteredList.add(card);
            }
        }

        adapter.filterList(filteredList);
    }

    // Loads artifacts from the database and displays them in RecyclerView
    private void setUpViewCards() {
        // Load artifacts from the database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/");
        DatabaseReference artifactRef = db.getReference("Artifacts");

        artifactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the current list before loading the new data
                viewCards.clear();

                // Convert data from the database into an ExpandedArtifact objects
                for (DataSnapshot child : snapshot.getChildren()) {
                    ExpandedArtifact artifact = child.getValue(ExpandedArtifact.class);
                    String lotNum = child.getKey();
                    // Add the artifact to the RecyclerView list for display
                    if (artifact != null) {
                        artifact.setLotNumber(lotNum);
                        viewCards.add(artifact);
                    }
                }
                // Update the RecyclerView display cards
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Show an error message if artifacts did not load from the database
                Toast.makeText(requireContext(),
                        "Failed to load artifacts.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    @Override
    protected void setPresenter() {}

    // Read current user email
    private void readArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        currentUserEmail = arguments.getString(ARG_EMAIL);
    }

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
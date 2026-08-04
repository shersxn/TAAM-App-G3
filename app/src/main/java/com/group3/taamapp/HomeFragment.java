package com.group3.taamapp;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Bases.BundleInitializer;
import com.group3.taamapp.LoginPage.LoginFragment;
//import com.group3.taamapp.SavedArtifactFragment;

public class HomeFragment extends BaseFragment {

    public static final String ARG_EMAIL = "email";
    ArrayList <ExpandedArtifact> viewCards = new ArrayList<>();
    ViewCardAdapter adapter;
    //int[] cardImages = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};
    SearchView searchArtifact;
    private String currentUserEmail;

    @Override
    protected int getLayoutId() {return R.layout.artifacts_view;}
    @Override
    protected void setUIComponents(View view) {
        readArguments();
        RecyclerView recyclerView = view.findViewById(R.id.artefactsRecycle);
        ViewCardAdapter.OnArtifactActionListener listener =
        adapter = new ViewCardAdapter(requireContext(), viewCards,
                new ViewCardAdapter.OnArtifactActionListener() {
                    @Override
                    public void onOpenArtifact(ExpandedArtifact artifact) {
                        toExpandedArtifact(artifact);
                    }
                });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        searchArtifact = view.findViewById(R.id.searchView);
        setUpViewCards();

    }

    @Override
    protected void setEvents() {
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
    }

    private void filter(String newText) {
        ArrayList<ExpandedArtifact> filteredList = new ArrayList<>();
        for (ExpandedArtifact card : viewCards) {
            if (card.getArtifactName().toLowerCase().startsWith(newText.toLowerCase())) {
                filteredList.add(card);
            }
        }

        adapter.filterList(filteredList);
    }

    private void setUpViewCards() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/");
        DatabaseReference artifactRef = db.getReference("Artifacts");

        artifactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewCards.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    ExpandedArtifact card = child.getValue(ExpandedArtifact.class);
                    viewCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void setPresenter() {
        //will implement
    }

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

//    private void setUpViewCards() {
//
//        String[] artefactNames = getResources().getStringArray(R.array.artefact_names);
//        String[] artefactPeriods = getResources().getStringArray(R.array.periods);
//        String[] artefactDescriptions = getResources().getStringArray(R.array.descriptions);
//
//        for (int i = 0; i < artefactNames.length; i++) {
//            viewCards.add(new ViewCard(i, 0, 0, artefactNames[i], artefactDescriptions[i], "None", "wood", artefactPeriods[i], cardImages[i]));
//
//
//        }
//    }
}
package com.group3.taamapp;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewActivity extends AppCompatActivity {

    ArrayList <ExpandedArtifact> viewCards = new ArrayList<>();
    ViewCardAdapter adapter;
    //int[] cardImages = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};
    SearchView searchArtifact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.artifacts_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }   );

        RecyclerView recycleView = findViewById(R.id.artefactsRecycle);


        this.adapter = new ViewCardAdapter(this, viewCards);
        recycleView.setAdapter(adapter);
        recycleView.setLayoutManager(new GridLayoutManager(this, 2));
        setUpViewCards();
        searchArtifact = findViewById(R.id.searchView);
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
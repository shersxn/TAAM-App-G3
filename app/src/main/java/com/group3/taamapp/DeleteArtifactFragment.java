package com.group3.taamapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteArtifactFragment extends Fragment {

    private AutoCompleteTextView autoCompleteArtifacts;
    private Button deleteButton;
    private List<String> artifactDatabase;
    private ArrayAdapter<String> adapter;

    private boolean isCurrentUserAdmin = true;

    public DeleteArtifactFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.delete_artifact, container, false);

        // Bind the UI elements
        autoCompleteArtifacts = view.findViewById(R.id.autoCompleteArtifacts);
        deleteButton = view.findViewById(R.id.delete_button);

        // Using a dummy database for now: Just to test the logic
        artifactDatabase = new ArrayList<>(Arrays.asList(
                "Ancient Vase",
                "Gold Coin",
                "Dinosaur Fossil",
                "Roman Sword",
                "Emerald Crown"
        ));

        // Build and attach the adapter
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                artifactDatabase
        );
        autoCompleteArtifacts.setAdapter(adapter);


        if (!isCurrentUserAdmin) {
            deleteButton.setEnabled(false);
            autoCompleteArtifacts.setEnabled(false); // Optional: Locks the dropdown too
            Toast.makeText(requireContext(), "View-Only Mode: You are not an admin.", Toast.LENGTH_LONG).show();
        }

        // Delete Button Logic
        deleteButton.setOnClickListener(v -> {
            String selectedName = autoCompleteArtifacts.getText().toString().trim();

            // Check if empty
            if (selectedName.isEmpty()) {
                Toast.makeText(requireContext(), "Please search or select an artifact.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the typed item actually exists in our dummy list
            if (!artifactDatabase.contains(selectedName)) {
                Toast.makeText(requireContext(), "Artifact not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate the deletion
            artifactDatabase.remove(selectedName);
            adapter.notifyDataSetChanged();

            // Clean up the UI
            autoCompleteArtifacts.setText("", false);
            autoCompleteArtifacts.clearFocus();

            Toast.makeText(requireContext(), selectedName + " successfully deleted!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
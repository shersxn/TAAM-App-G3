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
import java.util.List;

public class DeleteArtifactFragment extends Fragment {

    private AutoCompleteTextView autoCompleteArtifacts;
    private Button deleteButton;
    private List<String> artifactList;
    private ArrayAdapter<String> adapter;

    private DeleteArtifactHelper dbHelper;
    private String currentUserEmail = "";

    public DeleteArtifactFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_artifact, container, false);
        // Initialize all UI Components
        autoCompleteArtifacts = view.findViewById(R.id.autoCompleteArtifacts);
        deleteButton = view.findViewById(R.id.delete_button);

        dbHelper = new DeleteArtifactHelper();
        artifactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, artifactList);
        autoCompleteArtifacts.setAdapter(adapter);

        setUiEnabled(false);

        // Extract the current user's email from the custom login Bundle
        Bundle args = getArguments();
        if (args != null && args.containsKey("email")) {
            currentUserEmail = args.getString("email");
        }

        // Verify Admin Status
        dbHelper.verifyAdminStatus(currentUserEmail, (isAdmin, message) -> {
            if (isAdmin) {
                setUiEnabled(true);
                loadArtifacts();
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        // Delete Button Logic
        deleteButton.setOnClickListener(v -> {
            String selectedName = autoCompleteArtifacts.getText().toString().trim();

            if (selectedName.isEmpty()) {
                Toast.makeText(requireContext(), "Please search or select an artifact.", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.deleteArtifactByName(selectedName, (success, message) -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (success) {
                    // Update the UI immediately after a successful database deletion
                    artifactList.remove(selectedName);
                    adapter.notifyDataSetChanged();
                    autoCompleteArtifacts.setText("", false);
                    autoCompleteArtifacts.clearFocus();
                }
            });
        });

        return view;
    }

    private void loadArtifacts() {
        dbHelper.fetchArtifactNames((names, error) -> {
            if (names != null) {
                artifactList.clear();
                artifactList.addAll(names);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Error loading artifacts: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUiEnabled(boolean isEnabled) {
        deleteButton.setEnabled(isEnabled);
        autoCompleteArtifacts.setEnabled(isEnabled);
    }
}
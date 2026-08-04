package com.group3.taamapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddEditArtifactFragment extends Fragment {

    private ImageButton artifactImageButton;
    private Button buttonDone;
    private Spinner spinnerCategory, spinnerMaterial, spinnerDynastyPeriod;
    private EditText editTextName, editTextLotNumber, editTextDescription;
    private FirebaseDatabase db;
    private DatabaseReference dataReference;
    private ActivityResultLauncher<PickVisualMediaRequest> imagePicker;
    private Uri artifactImageUri;
    private String artifactImageUrl;
    private boolean isAdding;
    private String editingArtifactId;
    private ExpandedArtifact editingArtifact;
    private String name;
    private String lotNumber;
    private String description;
    private String category;
    private String material;
    private String dynastyPeriod;
    private String userEmail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_artifact, container,
                false);

        // Initialize class fields corresponding to UI components
        db = FirebaseDatabase.getInstance(
                "https://cscb07-group3-taamapp-default-rtdb.firebaseio.com");
        artifactImageButton = view.findViewById(R.id.artifactImageButton);
        buttonDone = view.findViewById(R.id.buttonDone);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerMaterial = view.findViewById(R.id.spinnerMaterial);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextName = view.findViewById(R.id.editTextName);
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        spinnerDynastyPeriod = view.findViewById(R.id.spinnerDynastyPeriod);

        // Set up spinner with categories
        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoriesAdapter);

        // Set up spinner with materials
        ArrayAdapter<CharSequence> materialsAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.materials_array, android.R.layout.simple_spinner_item);
        materialsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(materialsAdapter);

        // Set up spinner with dynastiesPeriods
        ArrayAdapter<CharSequence> dynastiesPeriodsAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.dynastiesPeriods_array,
                android.R.layout.simple_spinner_item);
        dynastiesPeriodsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerDynastyPeriod.setAdapter(dynastiesPeriodsAdapter);

        // Register Android Photo Picker with ImageButton for picking artifact image
        imagePicker = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        artifactImageUri = uri;
                        artifactImageButton.setImageURI(artifactImageUri);
                    }
                });

        // Update isAdding, editingArtifactId, and userEmail with data from the Bundle
        Bundle fragmentBundle = getArguments();
        if (fragmentBundle != null) {
            userEmail = fragmentBundle.getString(/*String: key in bundle based on calling page*/ "userEmail");
            editingArtifactId = fragmentBundle.getString(/*String: key in bundle based on calling page*/ "artifactId");
        }

        if (editingArtifactId == null) {
            isAdding = true;
        }

        // Verify admin status
        if (userEmail != null){
            String emailKeyInFirebase = userEmail.replace(".", ",");
            dataReference = db.getReference("Users").child(emailKeyInFirebase).child(
                    "admin");
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class))) {
                        // Disable access to add or edit artifact if the user is not an admin
                        Toast.makeText(requireContext(),
                                "You do not have access to the Add / Edit Artifact page",
                                Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack(); /*subject to change based on calling page*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Failed to retrieve data from Firebase",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set default inputs when editing an artifact
        if (!isAdding) {
            // Extract ExpandedArtifact object from editingArtifactId in Firebase
            dataReference = db.getReference("Artifacts").child(editingArtifactId);
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    editingArtifact = dataSnapshot.getValue(ExpandedArtifact.class);

                    // Set default inputs with editingArtifact data and disable update on lot number
                    if (editingArtifact != null) {
                        editTextLotNumber.setText(editingArtifact.getLotNumber());
                        editTextLotNumber.setEnabled(false);
                        editTextName.setText(editingArtifact.getArtifactName());
                        editTextDescription.setText(editingArtifact.getDescription());
                        spinnerCategory.setSelection(categoriesAdapter.getPosition(
                                editingArtifact.getCategory()));
                        spinnerMaterial.setSelection(materialsAdapter.getPosition(
                                editingArtifact.getMaterial()));
                        spinnerDynastyPeriod.setSelection(dynastiesPeriodsAdapter.getPosition(
                                editingArtifact.getDynastyPeriod()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Failed to retrieve data from Firebase",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set up ImageButton with Android Photo Picker for artifact image
        artifactImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                imagePicker.launch(new PickVisualMediaRequest.Builder().setMediaType(
                        ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        // Set up Done button
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Store inputted artifact data in the class fields
                name = editTextName.getText().toString().trim();
                lotNumber = editTextLotNumber.getText().toString().trim();
                description = editTextDescription.getText().toString().trim();
                category = spinnerCategory.getSelectedItem().toString();
                material = spinnerMaterial.getSelectedItem().toString();
                dynastyPeriod = spinnerDynastyPeriod.getSelectedItem().toString();

                if (areNonEmptyInputs(name, description, category, material, dynastyPeriod)) {
                    if (isAdding) {
                        // Check whether the inputted lot number exists in Firebase
                        dataReference = db.getReference("Artifacts").child(lotNumber);
                        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(requireContext(),
                                            "Please ensure the lot number is unique",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // Update artifact data in Firebase if the inputted lot number
                                // is unique
                                else {
                                    if (artifactImageUri != null) {
                                        // Upload image to Supabase if an image has been selected
                                        // and updateArtifactData() will be called by uploadImage()
                                        uploadImage();
                                    }

                                    else {
                                        updateArtifactData();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(requireContext(),
                                        "Failed to retrieve data from Firebase",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else {
                        if (artifactImageUri != null) {
                            // Upload image to Supabase if an image has been selected and
                            // updateArtifactData() will be called by uploadImage()
                            uploadImage();
                        }

                        else {
                            updateArtifactData();
                        }
                    }
                }
            }
        });

        return view;
    }

    private boolean areNonEmptyInputs(String name, String description, String category,
                                      String material, String dynastyPeriod) {
        // Return false if any mandatory field is empty
        if (name.isEmpty() || lotNumber.isEmpty() || description.isEmpty() ||
                category.isEmpty() || material.isEmpty() || dynastyPeriod.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Return true otherwise
        return true;
    }

    private void uploadImage() {
        SupabaseImageUploader imageUploader;
        imageUploader = new SupabaseImageUploader(requireContext());
        imageUploader.uploadImage(artifactImageUri, editTextLotNumber.getText().toString().trim(),
                new SupabaseImageUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String publicUrl) {
                        // Store the URL of selected image in the class field
                        artifactImageUrl = publicUrl;

                        updateArtifactData();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateArtifactData() {
        // Add artifact to Firebase when an admin user is adding an artifact
        if (isAdding) {
            dataReference = db.getReference("Artifacts");
            ExpandedArtifact artifact = new ExpandedArtifact(lotNumber, name, description,
                    category, material, dynastyPeriod, artifactImageUrl, 0, null);

            dataReference.child(lotNumber).setValue(artifact).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Artifact added",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to add artifact",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Edit data of existing artifact in Firebase when an admin user is editing an artifact
        if (!isAdding) {
            String existingId = editingArtifactId;
            dataReference = db.getReference("Artifacts").child(existingId);
            ExpandedArtifact artifact = editingArtifact;

            artifact.setArtifactName(name);
            artifact.setDescription(description);
            artifact.setCategory(category);
            artifact.setMaterial(material);
            artifact.setDynastyPeriod(dynastyPeriod);
            if (artifactImageUrl != null) {
                artifact.setImageUrl(artifactImageUrl);
            }

            dataReference.setValue(artifact).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Artifact edited",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to edit artifact",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

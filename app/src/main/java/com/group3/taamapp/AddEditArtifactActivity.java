package com.group3.taamapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddEditArtifactActivity extends AppCompatActivity {

    private ImageButton artifactImageButton;
    private Button buttonDone;
    private Spinner spinnerCategory, spinnerMaterial, spinnerDynastyPeriod;
    private EditText editTextName, editTextLotNumber, editTextDescription;
    private FirebaseDatabase db;
    private DatabaseReference dataReference;
    private Uri artifactImageUri;
    private boolean isAdding; //default: false (i.e. editing)
    private boolean isUniqueLotNumber; //default: false (i.e. not unique)
    private String editingArtifactId;
    private ExpandedArtifact editingArtifact;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_artifact);

        db = FirebaseDatabase.getInstance(
                "https://cscb07-group3-taamapp-default-rtdb.firebaseio.com");
        artifactImageButton = findViewById(R.id.artifactImageButton);
        buttonDone = findViewById(R.id.buttonDone);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerMaterial = findViewById(R.id.spinnerMaterial);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextName = findViewById(R.id.editTextName);
        editTextLotNumber = findViewById(R.id.editTextLotNumber);
        spinnerDynastyPeriod = findViewById(R.id.spinnerDynastyPeriod);

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoriesAdapter);

        ArrayAdapter<CharSequence> materialsAdapter = ArrayAdapter.createFromResource(this,
                R.array.materials_array, android.R.layout.simple_spinner_item);
        materialsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(materialsAdapter);

        ArrayAdapter<CharSequence> dynastiesPeriodsAdapter = ArrayAdapter.createFromResource(
                this, R.array.dynastiesPeriods_array, android.R.layout.simple_spinner_item);
        dynastiesPeriodsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerDynastyPeriod.setAdapter(dynastiesPeriodsAdapter);

        ActivityResultLauncher<PickVisualMediaRequest> imagePicker =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                        uri -> {
            if (uri != null){
                artifactImageUri = uri;
                artifactImageButton.setImageURI(artifactImageUri);
            }
        });

        // set isAdding and set editingArtifactId and editingArtifact
        editingArtifactId = getIntent().getStringExtra(/* string name depending on calling Intent*/"");
        if (editingArtifactId == null){
            isAdding = true;
        }

        // set editText text defaults
        if (isAdding == false){
            /*extract ExpandedArtifact object from existingId below*/
            dataReference = db.getReference("Artifacts").child(editingArtifactId);
            dataReference.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    ExpandedArtifact artifact = dataSnapshot.getValue(ExpandedArtifact.class);
                    editingArtifact = artifact;
                }

                @Override
                public void onCancelled(DatabaseError databaseError){
                    return;
                }
            });

            /*actual data fields for editing artifact below*/
            editTextLotNumber.setText(editingArtifact.lotNumber);
            editTextLotNumber.setEnabled(false);
            editTextName.setText(editingArtifact.artifactName);
            editTextDescription.setText(editingArtifact.description);
            spinnerCategory.setSelection(categoriesAdapter.getPosition(editingArtifact.category));
            spinnerMaterial.setSelection(materialsAdapter.getPosition(editingArtifact.material));
            spinnerDynastyPeriod.setSelection(dynastiesPeriodsAdapter.getPosition(
                    editingArtifact.dynastyPeriod));
        }

        artifactImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                imagePicker.launch(new PickVisualMediaRequest.Builder().setMediaType(
                        ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                updateArtifact();
            }
        });

    }

    private boolean isValidInputs(String name, String lotNumber, String description,
                                  String category, String material, String dynastyPeriod){
        setIsUniqueLotNumber(lotNumber);

        if (name.isEmpty() || lotNumber.isEmpty() || description.isEmpty() ||
                category.isEmpty() || material.isEmpty() || dynastyPeriod.isEmpty()){
            Toast.makeText(this, "Please fill out all fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isUniqueLotNumber == false){
            Toast.makeText(this, "Please ensure the lot number is unique",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setIsUniqueLotNumber(String lotNumber){
        dataReference = db.getReference("Artifacts");
        dataReference.orderByChild(/*field name for lot number in firebase*/"lotNumber").equalTo(
                lotNumber).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if (!(dataSnapshot.exists())){
                    isUniqueLotNumber = true;
                }
                else{
                    Toast.makeText(AddEditArtifactActivity.this,
                            "Lot number is not unique", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                return;
            }
        });
    }

    private void uploadImage(){ /*upload image from image picker to Supabase and obtain image URL for Firebase storage*/
        ...
    }

    private void updateArtifact(){
        String name = editTextName.getText().toString().trim();
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String material = spinnerMaterial.getSelectedItem().toString();
        String dynastyPeriod = spinnerDynastyPeriod.getSelectedItem().toString();
        /*missing String: Image url field here*/

        if (isValidInputs(name, lotNumber, description, category, material, dynastyPeriod)
                == true && isAdding == true){
            dataReference = db.getReference("Artifacts");
            String id = dataReference.push().getKey();
            ExpandedArtifact artifact = new ExpandedArtifact(lotNumber, name, description,
                    category, material, dynastyPeriod, /*String: image URL*/, 0, 0);

            dataReference.child(id).setValue(artifact).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(this, "Artifact added", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Failed to add artifact",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (isValidInputs(name, lotNumber, description, category, material, dynastyPeriod)
                == true && isAdding == false){
            String existingId = editingArtifactId;
            dataReference = db.getReference("Artifacts").child(existingId);

            ExpandedArtifact artifact = editingArtifact;

            artifact.setArtifactName(name);
            artifact.setDescription(description);
            artifact.setCategory(category);
            artifact.setMaterial(material);
            artifact.setDynastyPeriod(dynastyPeriod);
            artifact.setImageUrl(/*String: Image url*/);

            dataReference.setValue(artifact).addOnCompleteListener(
                    task -> {
                if (task.isSuccessful()){
                    Toast.makeText(this, "Artifact edited", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Failed to edit artifact",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

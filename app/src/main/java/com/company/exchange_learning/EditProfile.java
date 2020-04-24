package com.company.exchange_learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    private final int PICK_IMAGE_REQUEST = 71;
    private TextInputEditText titleView,universityView,departmentView,skillsView,overviewView;
    private Button doneBtn;
    private ProgressBar progressBar;
    private Spinner community_spinner;
    private List<String> spinnerArray;
    private UserProfile profile;
    private Uri filePath;
    private CircleImageView profileImage;
    private ImageView editProfileBtn;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        titleView = findViewById(R.id.TitletextInputEditText);
        universityView = findViewById(R.id.UniversityInputEditText);
        departmentView = findViewById(R.id.DepartmenttextInputEditText);
        skillsView = findViewById(R.id.skillstextInputEditText);
        overviewView = findViewById(R.id.overviewInputEditText);
        community_spinner = findViewById(R.id.edit_community_spinner);
        editProfileBtn = findViewById(R.id.editProfileImagebtn);
        profileImage = findViewById(R.id.edit_profile_image);
        doneBtn = findViewById(R.id.editProfileScreenBtn);
        progressBar = findViewById(R.id.edit_prof_progress);
        progressBar.setVisibility(View.GONE);
        populateCommunity();
        populate();
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAllData();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void populateCommunity(){
        String[] spinnerArrayRaw = getResources().getStringArray(R.array.community_list2);
        spinnerArray = Arrays.asList(spinnerArrayRaw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        community_spinner.setAdapter(adapter);
    }

    private void populate(){
        Intent i = getIntent();
        profile =(UserProfile) getIntent().getSerializableExtra("profile");
        if (profile.getMy_overview() != null && !profile.getMy_overview().equals("")){
            overviewView.setText(profile.getMy_overview());
        }
        if(profile.getMy_department() != null && !profile.getMy_department().equals("")){
            departmentView.setText(profile.getMy_department());
        }
        if(profile.getMy_title() != null && !profile.getMy_title().equals("")){
            titleView.setText(profile.getMy_title());
        }
        if (profile.getMy_university() != null && !profile.getMy_university().equals("")){
            universityView.setText(profile.getMy_university());
        }
        if (profile.getMy_skills() != null && !profile.getMy_skills().equals("") ){
            skillsView.setText(profile.getMy_skills());
        }
        if (profile.getUser() != null) {
            int pos = spinnerArray.indexOf(profile.getUser().getCommunity());
            Log.d(TAG,profile.getUser().getCommunity());
            community_spinner.setSelection(pos);
        }
        loadImage();
    }

    private void updateData(String imageURL){
        progressBar.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.GONE);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Profile_Information").child(Constants.uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("my_title", titleView.getText().toString().trim());
        updates.put("my_department", departmentView.getText().toString().trim());
        updates.put("my_skills", skillsView.getText().toString().trim());
        updates.put("my_overview", overviewView.getText().toString().trim());
        updates.put("my_university", universityView.getText().toString().trim());
        if (imageURL != null) {
            updates.put("profile_image", imageURL);
        }
        myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null){
                    Log.d(TAG,"Profile updated successfully");
                    DatabaseReference myRef = database.getReference("User_Information").child(Constants.uid);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("community", community_spinner.getSelectedItem().toString());
                    myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.d(TAG, "Community updated successfully");
                                Toast.makeText(EditProfile.this,"Updated Successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this,"Error updating community",Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Error updating community" + databaseError.getMessage());
                            }
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }
                else{
                    Toast.makeText(EditProfile.this,"Error Updating",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Error updating" + databaseError.getMessage());
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }

    private void uploadAllData() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image... ");
            progressDialog.show();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference ref = storageReference.child("profileImages/"+ Constants.uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Uploaded Image", Toast.LENGTH_SHORT).show();
                            updateData(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Failed To upload Image", Toast.LENGTH_SHORT).show();
                            updateData(null);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else{
            updateData(null);
        }
    }

    private void loadImage(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ Constants.uid);
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .placeholder(R.drawable.default_avatar)
                .into(profileImage);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

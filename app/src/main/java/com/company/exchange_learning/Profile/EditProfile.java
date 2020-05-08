package com.company.exchange_learning.Profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.UserProfile;
import com.company.exchange_learning.utils.DBOperations;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;


import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    public static final int CHOOSE_FROM_GALLERY = 99;

    private static final String TAG = "EditProfile";
    private EditText titleView, universityView, departmentView, skillsView, overviewView, addressView;
    private CardView updateProfileBtn;
    private TextView updateProfileBtnTxt;
    private AVLoadingIndicatorView progressBar;
    private Spinner community_spinner;
    private List<String> spinnerArray;
    private UserProfile profile;
    private Uri filePath;
    private CircleImageView profileImage;
    private ImageView chooseImg;
    private StorageReference storageRef;
    private DatabaseReference dbRef;
    private Toolbar toolbar;
    private TextView exhangeTxt, learningTxt;
    private boolean isProfileImgChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        exhangeTxt = findViewById(R.id.exchange_txt);
        learningTxt = findViewById(R.id.learning_txt);
        learningTxt.setVisibility(View.GONE);
        exhangeTxt.setText("Update Profile");
        titleView = findViewById(R.id.updateProfileTitleEdit);
        universityView = findViewById(R.id.updateProfileUniEdit);
        departmentView = findViewById(R.id.updateProfileDptEdit);
        addressView = findViewById(R.id.updateProfileaddrEdit);
        skillsView = findViewById(R.id.updateProfileSkillEdit);
        overviewView = findViewById(R.id.updateProfileOverViewEdit);
        community_spinner = findViewById(R.id.updateProfileCommSpinner);
        chooseImg = findViewById(R.id.updateProfileChooseImg);
        profileImage = findViewById(R.id.updateProfileProfileImg);
        updateProfileBtn = findViewById(R.id.updateProfileUpdateBtn);
        progressBar = findViewById(R.id.updateProfileProgress);
        updateProfileBtnTxt = findViewById(R.id.updateProfileUpdateBtnTxt);

        populateCommunity();
        populate();

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAllData();
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageToUpload();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageToUpload();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleCancellation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }



    private void chooseImageToUpload() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY);
            }
        } else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY);
        }
    }

    @Override
    public void onBackPressed() {
        handleCancellation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        chooseImageToUpload();
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void populateCommunity() {
        String[] spinnerArrayRaw = getResources().getStringArray(R.array.community_list2);
        spinnerArray = Arrays.asList(spinnerArrayRaw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        community_spinner.setAdapter(adapter);
    }

    private void populate() {
        Intent i = getIntent();
        profile = (UserProfile) getIntent().getSerializableExtra("profile");
        if (profile.getMy_overview() != null && !profile.getMy_overview().equals("")) {
            overviewView.setText(profile.getMy_overview());
        }
        if (profile.getMy_department() != null && !profile.getMy_department().equals("")) {
            departmentView.setText(profile.getMy_department());
        }
        if (profile.getMy_address() != null && !profile.getMy_address().equals("")) {
            addressView.setText(profile.getMy_address());
        }
        if (profile.getMy_title() != null && !profile.getMy_title().equals("")) {
            titleView.setText(profile.getMy_title());
        }
        if (profile.getMy_university() != null && !profile.getMy_university().equals("")) {
            universityView.setText(profile.getMy_university());
        }
        if (profile.getMy_skills() != null && profile.getMy_skills().size() != 0){
            String output = "";
            for (int x = 0 ; x < profile.getMy_skills().size(); x++){
                output += profile.getMy_skills().get(x) + ", ";
            }
            skillsView.setText(output.substring(0,output.length()-2));
        }
        if (profile.getUser() != null) {
            int pos = spinnerArray.indexOf(profile.getUser().getCommunity());
            community_spinner.setSelection(pos);
        }
        loadImage();
    }

    private void handleCancellation() {
        if (!titleView.getText().toString().equalsIgnoreCase(profile.getMy_title()) && !universityView.getText().toString().equalsIgnoreCase(profile.getMy_university())
                && !overviewView.getText().toString().equalsIgnoreCase(profile.getMy_overview()) && !departmentView.getText().toString().equalsIgnoreCase(profile.getMy_department())) {
            showConfirmationDialog();
        } else {
            finish();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard everything?");
        builder.setMessage("Are you sure to exit the editor?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProgress() {
        progressBar.show();
        updateProfileBtnTxt.setText("");
        updateProfileBtn.setEnabled(false);
    }

    private void hideProgress() {
        progressBar.hide();
        updateProfileBtnTxt.setText("UPDATE PROFILE");
        updateProfileBtn.setEnabled(true);
    }

    private boolean check(String title,String dpt, String addr, String uni , String overview, String skill){
        if (title.length() == 0 || dpt.length() == 0 || addr.length() ==0 || uni.length() == 0 || skill.length() ==0 || overview.length() == 0){
            return false;
        }
        return true;
    }

    private void updateData(Uri imageURL) {
        String title = WordUtils.capitalize(titleView.getText().toString().trim());
        String dpt = WordUtils.capitalize(departmentView.getText().toString().trim());
        String addr = WordUtils.capitalize(addressView.getText().toString().trim());
        String uni = WordUtils.capitalize(universityView.getText().toString().trim());
        String overview = overviewView.getText().toString().trim();
        List<String> skills = Arrays.asList(skillsView.getText().toString().trim().split(","));

        if(!check(title,dpt,addr,uni,overview,skillsView.getText().toString().trim())){
            Toast.makeText(EditProfile.this,"Please fill all fields", Toast.LENGTH_SHORT).show();
            hideProgress();
            return;
        }

        if (isProfileImgChanged) {
            dbRef = FirebaseDatabase.getInstance().getReference("Profile_Information").child(Constants.getConstantUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("my_title", title);
            updates.put("my_department", dpt);
            if (skills.size() != 0 && !skills.get(0).equals("")){
                updates.put("my_skills", skills);
            }
            updates.put("my_overview", overview);
            updates.put("my_university", uni);
            updates.put("my_address",addr);
            if (imageURL != null) {
                UserProfileChangeRequest.Builder ii = new UserProfileChangeRequest.Builder();
                ii.setPhotoUri(imageURL);
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(ii.build());
            }
            dbRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("User_Information").child(Constants.getConstantUid());
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("community", community_spinner.getSelectedItem().toString());
                        myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Toast.makeText(EditProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                    finish();
                                } else {
                                    hideProgress();
                                    Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                }
            });
        } else {
            if (titleView.getText().toString().equalsIgnoreCase(profile.getMy_title()) && overviewView.getText().toString().equalsIgnoreCase(profile.getMy_overview())
                    && universityView.getText().toString().equalsIgnoreCase(profile.getMy_university()) && departmentView.getText().toString().equalsIgnoreCase(profile.getMy_department())
                    && skillsView.getText().toString().trim().equalsIgnoreCase(convert()) && addressView.getText().toString().equalsIgnoreCase(profile.getMy_address())
            ) {
                Toast.makeText(EditProfile.this, "No changes made", Toast.LENGTH_SHORT).show();
                hideProgress();
            } else {
                if (overviewView.getText().length() > 30) {
                    dbRef = FirebaseDatabase.getInstance().getReference("Profile_Information").child(Constants.getConstantUid());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("my_title", titleView.getText().toString().trim());
                    updates.put("my_department", departmentView.getText().toString().trim());
                    updates.put("my_address",addressView.getText().toString().trim());
                    List<String> skills2 = Arrays.asList(skillsView.getText().toString().trim().split(","));
                    if (skills2.size() != 0 && !skills2.get(0).equals("")){
                        updates.put("my_skills", skills2);
                    }
                    updates.put("my_overview", overviewView.getText().toString().trim());
                    updates.put("my_university", universityView.getText().toString().trim());
                    if (imageURL != null) {
                        UserProfileChangeRequest.Builder ii = new UserProfileChangeRequest.Builder();
                        ii.setPhotoUri(imageURL);
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(ii.build());
                    }
                    dbRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("User_Information").child(Constants.getConstantUid());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("community", community_spinner.getSelectedItem().toString());
                                myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Toast.makeText(EditProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                            hideProgress();
                                            finish();
                                        } else {
                                            hideProgress();
                                            Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();
                                hideProgress();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditProfile.this, "Minimum 30 Characters required for overview", Toast.LENGTH_LONG).show();
                    hideProgress();
                }
            }
        }
    }



    private void uploadAllData() {
        showProgress();
        if (isProfileImgChanged) {
            if (filePath != null) {
                storageRef = FirebaseStorage.getInstance().getReference().child("profileImages/" + Constants.getConstantUid());
                storageRef.putFile(filePath, DBOperations.getmetaData()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Error While Uploading Profile Image", Toast.LENGTH_LONG).show();
                            hideProgress();
                        }
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            updateData(task.getResult());
                        } else {
                            Toast.makeText(getApplicationContext(), "Error While Uploading Profile Image", Toast.LENGTH_LONG).show();
                            hideProgress();
                        }
                    }
                });
            } else {
                updateData(null);
            }
        } else {
            updateData(null);
        }
    }

    private void loadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/" + Constants.getConstantUid());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }


    private String convert(){
        String output = "";
        if (profile.getMy_skills() != null) {
            for (int x = 0; x < profile.getMy_skills().size(); x++) {
                output += profile.getMy_skills().get(x) + ", ";
            }
            return output.substring(0, output.length() - 2);
        }
        else{
            return output;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
                isProfileImgChanged = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

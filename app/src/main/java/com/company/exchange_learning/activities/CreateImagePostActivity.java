package com.company.exchange_learning.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import androidx.fragment.app.DialogFragment;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.MainActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.PostModel;
import com.company.exchange_learning.utils.DBOperations;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateImagePostActivity extends AppCompatActivity {

    public static final int CHOOSE_FROM_GALLERY = 99;

    private Toolbar toolbar;

    private TextView exchangeTxt, learningTxt, postShareTxtView, tagCommsTxt;
    private ImageView mainImage, removeMainImgBtn;
    private EditText postImgInfo;

    private Spinner typeSpinner;
    private RadioGroup skillGroup;
    private RadioButton checkBtn;
    private AVLoadingIndicatorView avi;
    private CardView multiSpinner;

    private ArrayList<MultiSelectModel> multiArrayList;
    private MultiSelectDialog multiSelectDialog;

    private List<String> mSelectedCommunities;
    private List<Integer> mSelectedCommunitiesIDs;


    private boolean isActionNewPost = true;
    private boolean isCommunitiesChangedOnUpdate = false;
    private boolean isImageLoaded = false;

    DatabaseReference postRef;
    StorageReference storageRef;
    PostModel post;

    private Uri uri;


    private LinearLayout uploadImgLayout;
    CardView postShareBtn;
    private boolean isImageChangedForUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_image_post);
        initUI();
        handleIntent();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        exchangeTxt = findViewById(R.id.exchange_txt);
        learningTxt = findViewById(R.id.learning_txt);
        learningTxt.setVisibility(View.GONE);
        avi = findViewById(R.id.createImgPostProgress);
        mSelectedCommunities = new ArrayList<>();
        mSelectedCommunitiesIDs = new ArrayList<>();

        postImgInfo = findViewById(R.id.createImgPostInfo);
        mainImage = findViewById(R.id.createImgPostMainImage);
        removeMainImgBtn = findViewById(R.id.mainImgRemoveBtn);
        uploadImgLayout = findViewById(R.id.uploadImgLayout);
        skillGroup = findViewById(R.id.createImgPostSkillRg);

        removeMainImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMainImage();
            }
        });

        uploadImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageToUpload();
            }
        });

        typeSpinner = findViewById(R.id.createImgPostTypeSpinner);
        multiSpinner = findViewById(R.id.postImgMultiLayout);
        tagCommsTxt = findViewById(R.id.postImgtagCommTxtView);
        multiSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMultiSpinner();
            }
        });


        postShareBtn = findViewById(R.id.createImgPostShareBtn);
        postShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOrUpdatePost();
            }
        });
        postShareTxtView = findViewById(R.id.createNoImgPostShareBtnTxt);

        populateTypeSpinner();
    }

    private void initMultiSpinner() {
        if (multiArrayList == null) {
            multiArrayList = new ArrayList<>();
            multiArrayList.add(new MultiSelectModel(0, "Architecture"));
            multiArrayList.add(new MultiSelectModel(1, "Bio Sciences"));
            multiArrayList.add(new MultiSelectModel(2, "Chemical Engineering"));
            multiArrayList.add(new MultiSelectModel(3, "Chemistry"));
            multiArrayList.add(new MultiSelectModel(4, "Civil Engineering"));
            multiArrayList.add(new MultiSelectModel(5, "Computer Science"));
            multiArrayList.add(new MultiSelectModel(6, "Department of Biotechnology"));
            multiArrayList.add(new MultiSelectModel(7, "Development Studies"));
            multiArrayList.add(new MultiSelectModel(8, "Earth Sciences"));
            multiArrayList.add(new MultiSelectModel(9, "Economics"));
            multiArrayList.add(new MultiSelectModel(10, "Electrical and Computer Engineering"));
            multiArrayList.add(new MultiSelectModel(11, "Environmental Sciences"));
            multiArrayList.add(new MultiSelectModel(12, "Health Informatics"));
            multiArrayList.add(new MultiSelectModel(13, "Humanities"));
            multiArrayList.add(new MultiSelectModel(14, "Management Sciences"));
            multiArrayList.add(new MultiSelectModel(15, "Mathematics"));
            multiArrayList.add(new MultiSelectModel(16, "Mechanical Engineering"));
            multiArrayList.add(new MultiSelectModel(17, "Meteorology"));
            multiArrayList.add(new MultiSelectModel(18, "Pharmacy"));
            multiArrayList.add(new MultiSelectModel(19, "Physics"));
            multiArrayList.add(new MultiSelectModel(20, "Statistics"));
        }

        multiSelectDialog = new MultiSelectDialog().title("Tag Communities").titleSize(30).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1).setMaxSelectionLimit(21).multiSelectList(multiArrayList)
                .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                    @Override
                    public void onSelected(ArrayList<Integer> arrayList, ArrayList<String> arrayList1, String s) {
                        mSelectedCommunities.clear();
                        mSelectedCommunities.addAll(arrayList1);
                        if (mSelectedCommunities.size() > 0) {
                            tagCommsTxt.setText(mSelectedCommunities.size() + " communities tagged");
                        } else {
                            tagCommsTxt.setText("Tag Communities");
                        }
                        if (!isActionNewPost) {
                            isCommunitiesChangedOnUpdate = true;
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });
        multiSelectDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MultiSelectDialogStyle);
        multiSelectDialog.preSelectIDsList((ArrayList<Integer>) mSelectedCommunitiesIDs);
        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
    }

    private void shareOrUpdatePost() {
        final String imgInfo = postImgInfo.getText().toString().trim();
        final String showSkill = getSkillStatus();
        final String postType = typeSpinner.getSelectedItem().toString();

        if (imgInfo.length() != 0) {
            if (isImageLoaded && uri != null) {
                if (isActionNewPost) {
                    if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                        if (imgInfo.length() > 20) {
                            showProgress();
                            storageRef = FirebaseStorage.getInstance().getReference().child("post-images/").child(Constants.getConstantUid() + "/" + Constants.getConstantUid() + "_image" + DateTimeUtils.getCurrentDateTime());
                            storageRef.putFile(uri,DBOperations.getmetaData()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    return storageRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downUri = task.getResult();
                                        post = PostModel.getPostMode(2);
                                        post.setPost_image_info(imgInfo);
                                        post.setPost_image(downUri.toString());
                                        post.setPost_date(getTimeDate());
                                        post.setUser_id(Constants.getConstantUid());
                                        post.getTagged_communities().addAll(mSelectedCommunities);
                                        post.setShow_skills(showSkill);
                                        post.setPost_type(postType);

                                        postRef = FirebaseDatabase.getInstance().getReference("Posts_Table");
                                        postRef.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    hideProgress();
                                                    Toast.makeText(getApplicationContext(), "Post Shared Successfully", Toast.LENGTH_LONG).show();
                                                    gotoMainActivity();
                                                } else {
                                                    hideProgress();
                                                    Toast.makeText(getApplicationContext(), "Error sharing the post", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        hideProgress();
                                        Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(this, "Image info should be at least 20 characters", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    } else {
                        Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (imgInfo.equalsIgnoreCase(post.getPost_image_info()) && postType.equalsIgnoreCase(post.getPost_type())
                            && showSkill.equalsIgnoreCase(post.getShow_skills()) && !isImageChangedForUpdate && !isCommunitiesChangedOnUpdate) {
                        Toast.makeText(this, "No changes detected", Toast.LENGTH_LONG).show();
                    } else {
                        if (isImageChangedForUpdate) {
                            if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                                if (imgInfo.length() >  20) {
                                    showProgress();
                                    storageRef = FirebaseStorage.getInstance().getReference().child("post-images/").child(Constants.getConstantUid() + "/" + Constants.getConstantUid() + "_image" + DateTimeUtils.getCurrentDateTime());
                                    storageRef.putFile(uri,DBOperations.getmetaData()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                            return storageRef.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                final Uri downUri = task.getResult();
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(post.getPost_image());
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        postRef = FirebaseDatabase.getInstance().getReference("Posts_Table").child(post.getPost_id());
                                                        Map<String, Object> updates = new HashMap<>();
                                                        updates.put("post_image_info", imgInfo);
                                                        updates.put("post_image", downUri.toString());
                                                        updates.put("post_type", postType);
                                                        updates.put("show_skills", showSkill);
                                                        updates.put("user_id", post.getUser_id());
                                                        updates.put("post_date", post.getPost_date());
                                                        updates.put("tagged_communities", mSelectedCommunities);

                                                        postRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                                if (databaseError == null) {
                                                                    hideProgress();
                                                                    Toast.makeText(getApplicationContext(), "Post updated Successfully", Toast.LENGTH_LONG).show();
                                                                    gotoMainActivity();
                                                                } else {
                                                                    hideProgress();
                                                                    Toast.makeText(getApplicationContext(), "Error updating the post", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        hideProgress();
                                                        Toast.makeText(getApplicationContext(), "Error updating the post", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            } else {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Image info should be at least 20 characters", Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                            } else {
                                Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                                if (imgInfo.length() > 20) {
                                    postRef = FirebaseDatabase.getInstance().getReference("Posts_Table").child(post.getPost_id());
                                    showProgress();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("post_image_info", imgInfo);
                                    updates.put("post_image", post.getPost_image());
                                    updates.put("post_type", postType);
                                    updates.put("show_skills", showSkill);
                                    updates.put("user_id", post.getUser_id());
                                    updates.put("tagged_communities", mSelectedCommunities);
                                    updates.put("post_date", post.getPost_date());

                                    postRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Post updated Successfully", Toast.LENGTH_LONG).show();
                                                gotoMainActivity();
                                            } else {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Error updating the post", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Image info should be at least 20 characters", Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                            } else {
                                Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else {
                hideProgress();
                Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
            }
        } else {
            hideProgress();
            Toast.makeText(this, "Image info is required", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            loadImageIntoView(uri);
            if (!isActionNewPost) {
                isImageChangedForUpdate = true;
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        chooseImageToUpload();
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showProgress() {
        avi.show();
        postShareTxtView.setText("");
        postShareBtn.setEnabled(false);
    }

    private void hideProgress() {
        postShareTxtView.setText(isActionNewPost ? "Share Post" : "Update Post");
        postShareBtn.setEnabled(true);
        avi.hide();
    }

    private void gotoMainActivity() {
        startActivity(new Intent(CreateImagePostActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
    }

    private int getCommunityID(String comm) {
        if (multiArrayList == null) {
            multiArrayList = new ArrayList<>();
            multiArrayList.add(new MultiSelectModel(0, "Architecture"));
            multiArrayList.add(new MultiSelectModel(1, "Bio Sciences"));
            multiArrayList.add(new MultiSelectModel(2, "Chemical Engineering"));
            multiArrayList.add(new MultiSelectModel(3, "Chemistry"));
            multiArrayList.add(new MultiSelectModel(4, "Civil Engineering"));
            multiArrayList.add(new MultiSelectModel(5, "Computer Science"));
            multiArrayList.add(new MultiSelectModel(6, "Department of Biotechnology"));
            multiArrayList.add(new MultiSelectModel(7, "Development Studies"));
            multiArrayList.add(new MultiSelectModel(8, "Earth Sciences"));
            multiArrayList.add(new MultiSelectModel(9, "Economics"));
            multiArrayList.add(new MultiSelectModel(10, "Electrical and Computer Engineering"));
            multiArrayList.add(new MultiSelectModel(11, "Environmental Sciences"));
            multiArrayList.add(new MultiSelectModel(12, "Health Informatics"));
            multiArrayList.add(new MultiSelectModel(13, "Humanities"));
            multiArrayList.add(new MultiSelectModel(14, "Management Sciences"));
            multiArrayList.add(new MultiSelectModel(15, "Mathematics"));
            multiArrayList.add(new MultiSelectModel(16, "Mechanical Engineering"));
            multiArrayList.add(new MultiSelectModel(17, "Meteorology"));
            multiArrayList.add(new MultiSelectModel(18, "Pharmacy"));
            multiArrayList.add(new MultiSelectModel(19, "Physics"));
            multiArrayList.add(new MultiSelectModel(20, "Statistics"));
        }
        for (int i = 0; i < multiArrayList.size(); i++) {
            if (multiArrayList.get(i).getName().toLowerCase().equalsIgnoreCase(comm.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    private void removeMainImage() {
        isImageLoaded = false;
        isImageChangedForUpdate = false;
        uri = null;
        mainImage.setVisibility(View.GONE);
        removeMainImgBtn.setVisibility(View.GONE);
        uploadImgLayout.setVisibility(View.VISIBLE);
    }

    private void loadImageIntoView(Uri mUri) {
        isImageLoaded = true;
        uri = mUri;
        Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).into(mainImage);
        mainImage.setVisibility(View.VISIBLE);
        removeMainImgBtn.setVisibility(View.VISIBLE);
        uploadImgLayout.setVisibility(View.GONE);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getStringExtra("actionType").equalsIgnoreCase("editPost")) {
            isActionNewPost = false;
            postShareTxtView.setText("UPDATE POST");
            exchangeTxt.setText("Edit Post");
            post = (PostModel) intent.getSerializableExtra("post");
            postImgInfo.setText(post.getPost_image_info());
            if (post.getTagged_communities() != null && post.getTagged_communities().size() != 0) {
                tagCommsTxt.setText(post.getTagged_communities().size() + " communities tagged");
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    int id = getCommunityID(post.getTagged_communities().get(i));
                    if (id != -1) {
                        mSelectedCommunities.add(multiArrayList.get(id).getName());
                        mSelectedCommunitiesIDs.add(id);
                    }
                }
            }
            loadImageIntoView(Uri.parse(post.getPost_image()));
            if (post.getShow_skills().toLowerCase().equalsIgnoreCase("yes")) {
                checkBtn = findViewById(R.id.createImgPostYesBtn);
                checkBtn.setChecked(true);
            } else {
                checkBtn = findViewById(R.id.createImgPostNoBtn);
                checkBtn.setChecked(true);
            }
            if (post.getPost_type().equalsIgnoreCase("@Exchange")) {
                typeSpinner.setSelection(0);
            } else if (post.getPost_type().equalsIgnoreCase("@Price")) {
                typeSpinner.setSelection(1);
            } else {
                typeSpinner.setSelection(2);
            }
        } else {
            isActionNewPost = true;
            removeMainImage();
            checkBtn = findViewById(R.id.createImgPostYesBtn);
            checkBtn.setChecked(true);
            exchangeTxt.setText("Create Post");
        }
    }

    private String getSkillStatus() {
        try {
            int selectedId = skillGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        post = null;
        multiArrayList = null;
        mSelectedCommunitiesIDs = null;
        mSelectedCommunities = null;
        super.onDestroy();
    }

    private void handleCancellation() {
        if (isActionNewPost) {
            if (postImgInfo.getText().toString().length() != 0 || isImageLoaded) {
                showConfirmationDialog();
            } else {
                finish();
            }
        } else {
            if (!postImgInfo.getText().toString().equalsIgnoreCase(post.getPost_image_info()) || isImageChangedForUpdate) {
                showConfirmationDialog();
            } else {
                finish();
            }
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

    private void populateTypeSpinner() {
        String[] spinnerArray = {"@Exchange", "@Price", "@Help"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        handleCancellation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleCancellation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

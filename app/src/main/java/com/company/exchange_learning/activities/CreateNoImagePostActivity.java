package com.company.exchange_learning.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import androidx.fragment.app.DialogFragment;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.MainActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.PostModel;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNoImagePostActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView exchangeTxt, learningTxt, postShareTxtView, tagCommsTxt;
    private EditText titleEditTxt, bodyEditText;

    private Spinner typeSpinner;

    private RadioGroup skillGroup;
    private RadioButton checkBtn;
    private AVLoadingIndicatorView avi;
    private CardView multiSpinner;

    private ArrayList<MultiSelectModel> multiArrayList;
    private MultiSelectDialog multiSelectDialog;

    private List<String> mSelectedCommunities;
    private List<Integer> mSelectedCommunitiesIDs;

    CardView postShareBtn;

    private boolean isActionNewPost = true;
    private boolean isCommunitiesChangedOnUpdate = false;

    DatabaseReference postRef;
    PostModel post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_no_image_post);
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
        mSelectedCommunities = new ArrayList<>();
        mSelectedCommunitiesIDs = new ArrayList<>();

        exchangeTxt = findViewById(R.id.exchange_txt);
        learningTxt = findViewById(R.id.learning_txt);
        learningTxt.setVisibility(View.GONE);

        titleEditTxt = findViewById(R.id.createNoImgPostTitle);
        bodyEditText = findViewById(R.id.createNoImgPostBody);
        skillGroup = findViewById(R.id.createNoImgPostSkillRg);

        typeSpinner = findViewById(R.id.createNoImgPostTypeSpinner);
        avi = findViewById(R.id.createNoImgPostProgress);

        postShareBtn = findViewById(R.id.createNoImgPostShareBtn);
        postShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOrUpdatePost();
            }
        });
        postShareTxtView = findViewById(R.id.createNoImgPostShareBtnTxt);

        multiSpinner = findViewById(R.id.multiLayout);
        tagCommsTxt = findViewById(R.id.tagCommTxtView);

        multiSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMultiSpinner();
            }
        });

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

    private void shareOrUpdatePost() {
        String postTitle = titleEditTxt.getText().toString().trim();
        String postBody = bodyEditText.getText().toString().trim();
        String skillShow = getSkillStatus();
        String postType = typeSpinner.getSelectedItem().toString().trim();

        if (isActionNewPost) {
            if (postTitle.length() == 0 || postBody.length() == 0) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (mSelectedCommunities != null) {
                    if (mSelectedCommunities.size() == 0) {
                        Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                    } else {
                        if (postTitle.length() > 9 && postBody.length() > 59) {
                            postRef = FirebaseDatabase.getInstance().getReference("Posts_Table");
                            showProgress();
                            post = PostModel.getPostMode(1);
                            post.setPost_title(postTitle);
                            post.setPost_body(postBody);
                            post.setPost_type(postType);
                            post.setShow_skills(skillShow);
                            post.setUser_id(Constants.getConstantUid());
                            post.setPost_date(getTimeDate());
                            post.getTagged_communities().addAll(mSelectedCommunities);

                            postRef.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideProgress();
                                        Toast.makeText(getApplicationContext(), "Post shared successfully", Toast.LENGTH_LONG).show();
                                        gotoMainActivity();
                                    } else {
                                        hideProgress();
                                        Toast.makeText(getApplicationContext(), "Error sharing the post", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            hideProgress();
                            Toast.makeText(this, "Title and body should be at least 10 and 60 characters respectively", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (postTitle.length() != 0 || postBody.length() != 0) {
                if (postTitle.equalsIgnoreCase(post.getPost_title()) && postBody.equalsIgnoreCase(post.getPost_body())
                        && postType.equalsIgnoreCase(post.getPost_type()) && skillShow.equalsIgnoreCase(post.getShow_skills()) && !isCommunitiesChangedOnUpdate) {
                    Toast.makeText(getApplicationContext(), "No changes made", Toast.LENGTH_SHORT).show();
                } else {
                    if (mSelectedCommunities != null) {
                        if (mSelectedCommunities.size() == 0) {
                            Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                        } else {
                            if (postBody.length() > 59 && postTitle.length() > 9) {
                                postRef = FirebaseDatabase.getInstance().getReference("Posts_Table").child(post.getPost_id());
                                showProgress();
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("post_title", postTitle);
                                updates.put("post_body", postBody);
                                updates.put("post_type", postType);
                                updates.put("show_skills", skillShow);
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
                            } else {
                                hideProgress();
                                Toast.makeText(this, "Title and body should be at least 10 and 60 characters respectively", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoMainActivity() {
        startActivity(new Intent(CreateNoImagePostActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
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

    private void populateTypeSpinner() {
        String[] spinnerArray = {"@Exchange", "@Price", "@Help"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
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

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getStringExtra("actionType").equalsIgnoreCase("editPost")) {
            isActionNewPost = false;
            postShareTxtView.setText("UPDATE POST");
            exchangeTxt.setText("Edit Post");
            post = (PostModel) intent.getSerializableExtra("post");
            titleEditTxt.setText(post.getPost_title());
            bodyEditText.setText(post.getPost_body());
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
            if (post.getShow_skills().equalsIgnoreCase("Yes")) {
                checkBtn = findViewById(R.id.createNoImgPostYesBtn);
                checkBtn.setChecked(true);
            } else {
                checkBtn = findViewById(R.id.createNoImgPostNoBtn);
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
            checkBtn = findViewById(R.id.createNoImgPostYesBtn);
            checkBtn.setChecked(true);
            exchangeTxt.setText("Create Post");
        }
    }

    private void handleCancellation() {
        if (isActionNewPost) {
            if (titleEditTxt.getText().toString().length() != 0 || bodyEditText.getText().toString().length() != 0) {
                showConfirmationDialog();
            } else {
                finish();
            }
        } else {
            if (!titleEditTxt.getText().toString().equalsIgnoreCase(post.getPost_title()) || !bodyEditText.getText().toString().equalsIgnoreCase(post.getPost_body())) {
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

    @Override
    protected void onDestroy() {
        post = null;
        multiArrayList = null;
        mSelectedCommunitiesIDs = null;
        mSelectedCommunities = null;
        super.onDestroy();
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

package com.company.exchange_learning.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.FollowersActivity;
import com.company.exchange_learning.model.BasicUser;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.BasicUser;
import com.company.exchange_learning.model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;


import org.apache.commons.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.WordUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivityTAG";
    private String uid = Constants.getConstantUid();
    private boolean mode = true;
    private UserProfile profile = null;
    private BasicUser basicUser = null;
    private CardView editBtn, followBtn, followerBtn;
    private CircleImageView profileImage;
    private LinearLayout followersButtons;
    private LinearLayout aboutContent;
    private CardView singularFollowFollowingButton;
    private LinearLayout singularFollowFollowingButtonLayout;
    private ImageView follow_follower_indicator_view;
    private TextView follow_follower_indicator_view_text;
    private RelativeLayout titleContainer, universityContainer, departmentContainer, communityContainer, location_container, skill_container, email_container,addressContainer;
    private TextView titleView, universityView, departmentView, communityView, locationView, skillView, emailView, nameView, overviewView, titleUpper,addressView;
    private LinearLayout data_holder;
    private AVLoadingIndicatorView progressBar;
    private FrameLayout header;
    private ImageView backBtn;

    private TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loadViews();
        if (getIntent() != null && getIntent().getStringExtra("uid") != null) {
            uid = getIntent().getStringExtra("uid");
            if (!uid.equals(Constants.getConstantUid())) {
                mode = false;
            }
        }
        loadData(mode);
    }


    public void loadViews() {
        toolbarTitle = findViewById(R.id.profile_user_name);
        editBtn = findViewById(R.id.editProfilebtn);
        profileImage = findViewById(R.id.profile_image);
        nameView = findViewById(R.id.name_view);
        aboutContent = findViewById(R.id.aboutContent);
        overviewView = findViewById(R.id.overview_view);
        titleView = findViewById(R.id.title_view);
        titleContainer = findViewById(R.id.title_container);
        universityContainer = findViewById(R.id.university_container);
        universityView = findViewById(R.id.university_text);
        departmentContainer = findViewById(R.id.department_container);
        departmentView = findViewById(R.id.department_text);
        addressContainer = findViewById(R.id.address_container);
        addressView = findViewById(R.id.address_text);
        communityContainer = findViewById(R.id.community_container);
        communityView = findViewById(R.id.community_text);
        location_container = findViewById(R.id.location_container);
        locationView = findViewById(R.id.location_text);
        skill_container = findViewById(R.id.skills_container);
        skillView = findViewById(R.id.skills_text);
        email_container = findViewById(R.id.email_container);
        emailView = findViewById(R.id.email_text);
        data_holder = findViewById(R.id.profile_data_holder);
        progressBar = findViewById(R.id.profile_progress_bar);
        header = findViewById(R.id.frameLayout);
        backBtn = findViewById(R.id.backBtn);
        titleUpper = findViewById(R.id.titleViewUpper);
        singularFollowFollowingButton = findViewById(R.id.singularFollowFollowingButton);
        singularFollowFollowingButtonLayout = findViewById(R.id.singularFollowFollowingButtonLayout);
        follow_follower_indicator_view = findViewById(R.id.follow_following_indicator);
        follow_follower_indicator_view_text = findViewById(R.id.follow_following_indicator_text);
        followersButtons = findViewById(R.id.followLayout);
        followBtn = findViewById(R.id.followBtn);
        followerBtn = findViewById(R.id.followersBtn);

        followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, FollowersActivity.class);
                i.putExtra("uid",uid);
                i.putExtra("mode",0);
                startActivity(i);
            }
        });
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, FollowersActivity.class);
                i.putExtra("uid",uid);
                i.putExtra("mode",1);
                startActivity(i);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void populateViews(final UserProfile profile, boolean mode) {
        toolbarTitle.setText(WordUtils.capitalize(profile.getUser().getName()));
        Log.i("TESTABC", String.valueOf(mode));
        if (!mode) {
            editBtn.setVisibility(View.INVISIBLE);
            followersButtons.setVisibility(View.INVISIBLE);
            singularFollowFollowingButtonLayout.setVisibility(View.VISIBLE);
            setFollowButtonText();
        }
        else{
            editBtn.setVisibility(View.VISIBLE);
            followersButtons.setVisibility(View.VISIBLE);
            singularFollowFollowingButtonLayout.setVisibility(View.INVISIBLE);
        }
        singularFollowFollowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow_follower_indicator_view_text.getText().equals("Follow")){
                    followerAdded();
                }
                else{
                    followerRemoved();
                }
            }
        });
        editBtn.setClickable(true);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, EditProfile.class);
                i.putExtra("profile", profile);
                startActivity(i);
            }
        });
        if (profile.getUser() != null) {
            communityContainer.setVisibility(View.VISIBLE);
            email_container.setVisibility(View.VISIBLE);
            String location = profile.getUser().getCity_name() + "," + profile.getUser().getCountry_name();
            locationView.setText(location);
            communityView.setText(profile.getUser().getCommunity());
            emailView.setText(profile.getUser().getEmail());
            nameView.setText(WordUtils.capitalize(profile.getUser().getName()));
        } else {
            communityContainer.setVisibility(View.GONE);
            email_container.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
        }
        if (profile.getMy_overview() != null && !profile.getMy_overview().equals("")) {
            aboutContent.setVisibility(View.VISIBLE);
            overviewView.setVisibility(View.VISIBLE);
            overviewView.setText(profile.getMy_overview());
        } else {
            aboutContent.setVisibility(View.INVISIBLE);
            overviewView.setVisibility(View.GONE);
        }
        if (profile.getMy_address() != null && !profile.getMy_address().equals("")){
            addressContainer.setVisibility(View.VISIBLE);
            addressView.setText(profile.getMy_address());
        }
        else{
            addressContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_department() != null && !profile.getMy_department().equals("")) {
            departmentContainer.setVisibility(View.VISIBLE);
            departmentView.setText(profile.getMy_department());
        } else {
            departmentContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_title() != null && !profile.getMy_title().equals("")) {
            titleContainer.setVisibility(View.VISIBLE);
            titleUpper.setVisibility(View.VISIBLE);
            titleView.setText(WordUtils.capitalize(profile.getMy_title()));
            titleUpper.setText(WordUtils.capitalize(profile.getMy_title()));
        } else {
            titleUpper.setVisibility(View.INVISIBLE);
            titleContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_university() != null && !profile.getMy_university().equals("")) {
            universityContainer.setVisibility(View.VISIBLE);
            universityView.setText(WordUtils.capitalize(profile.getMy_university()));
        } else {
            universityContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_skills() != null && profile.getMy_skills().size() != 0 && !profile.getMy_skills().get(0).equals("")){
            skill_container.setVisibility(View.VISIBLE);
            String output = "";
            for (int x = 0 ; x < profile.getMy_skills().size(); x++){
                output += profile.getMy_skills().get(x) + ", ";
            }
            skillView.setText(output.substring(0,output.length()-2));
        }
        else{
            skill_container.setVisibility(View.GONE);
        }
        loadImage();
    }

    private void loadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/" + uid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(profileImage);
                hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideProgress();
            }
        });
    }

    private void loadData(final boolean mode) {
        showProgress();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                basicUser = dataSnapshot.getValue(BasicUser.class);
                if (basicUser != null) {
                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef2 = database2.getReference("Profile_Information").child(uid);
                    myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            profile = dataSnapshot.getValue(UserProfile.class);
                            if (profile != null) {
                                profile.setUser(basicUser);
                            } else {
                                profile = new UserProfile();
                                profile.setUser(basicUser);
                            }
                            populateViews(profile, mode);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            profile = new UserProfile();
                            profile.setUser(basicUser);
                            populateViews(profile, mode);
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                    hideProgress();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(uid);
        myRef.addListenerForSingleValueEvent(listener);
    }

    private void showProgress() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        progressBar.show();
        data_holder.setVisibility(View.INVISIBLE);
        backBtn.setVisibility(View.INVISIBLE);
        toolbarTitle.setVisibility(View.INVISIBLE);
        header.setVisibility(View.INVISIBLE);
        nameView.setVisibility(View.INVISIBLE);
        profileImage.setVisibility(View.INVISIBLE);
        titleUpper.setVisibility(View.INVISIBLE);
        overviewView.setVisibility(View.INVISIBLE);
        followBtn.setVisibility(View.INVISIBLE);
        followerBtn.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void hideProgress() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.drawer_orange));
        progressBar.hide();
        data_holder.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.VISIBLE);
        titleUpper.setVisibility(View.VISIBLE);
        followBtn.setVisibility(View.VISIBLE);
        followerBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setVisibility(View.VISIBLE);

        if (!mode) {
            editBtn.setVisibility(View.INVISIBLE);
            followersButtons.setVisibility(View.INVISIBLE);
            singularFollowFollowingButtonLayout.setVisibility(View.VISIBLE);
            setFollowButtonText();
        }
        else{
            editBtn.setVisibility(View.VISIBLE);
            followersButtons.setVisibility(View.VISIBLE);
            singularFollowFollowingButtonLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void followerAdded(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Followers").child(Constants.getConstantUid()).child("following");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List following = dataSnapshot.getValue(t);
                if (following == null){
                    following = new ArrayList();
                }
                following.add(uid);
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Followers").child(Constants.getConstantUid()).child("following");
                myRef.setValue(following, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Log.i(TAG,"Added following");
                            follow_follower_indicator_view_text.setText("Following");
                            follow_follower_indicator_view.setVisibility(View.VISIBLE);
                        }
                        else{
                            Log.i(TAG," Error Added following");
                            Log.e(TAG, databaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG," Error Added following");
                Log.e(TAG,databaseError.getMessage());
            }
        });



        myRef = database.getReference("Followers").child(uid).child("followers");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List following = dataSnapshot.getValue(t);
                if (following == null){
                    following = new ArrayList();
                }
                following.add(Constants.getConstantUid());
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Followers").child(uid).child("followers");
                myRef.setValue(following, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Log.i(TAG,"Added follower");
                        }
                        else{
                            Log.i(TAG," Error Added follower");
                            Log.e(TAG, databaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG," Error Added follower");
                Log.e(TAG,databaseError.getMessage());
            }
        });
    }

    private void followerRemoved(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.unfollow_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        CardView unfollow = view.findViewById(R.id.unfollow);
        TextView cancel = view.findViewById(R.id.cancelDialog);
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Followers").child(Constants.getConstantUid()).child("following");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List following = dataSnapshot.getValue(t);
                        if (following != null) {
                            following.remove(uid);
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Followers").child(Constants.getConstantUid()).child("following");
                            myRef.setValue(following, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Log.i(TAG, "Removed following");
                                        follow_follower_indicator_view_text.setText("Follow");
                                        follow_follower_indicator_view.setVisibility(View.GONE);
                                        dialog.dismiss();

                                    } else {
                                        Log.i(TAG, " Error removing following");
                                        Log.e(TAG, databaseError.getMessage());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG," Error removing following");
                        Log.e(TAG,databaseError.getMessage());
                    }
                });



                myRef = database.getReference("Followers").child(uid).child("followers");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List following = dataSnapshot.getValue(t);
                        if (following != null) {
                            following.remove(Constants.getConstantUid());
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Followers").child(uid).child("followers");
                            myRef.setValue(following, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Log.i(TAG, "removed follower");
                                    } else {
                                        Log.i(TAG, " Error removing follower");
                                        Log.e(TAG, databaseError.getMessage());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG," Error removing follower");
                        Log.e(TAG,databaseError.getMessage());
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void setFollowButtonText(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Followers").child(Constants.getConstantUid()).child("following");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List following = dataSnapshot.getValue(t);
                if (following != null && following.contains(uid)){
                    follow_follower_indicator_view_text.setText("Following");
                    follow_follower_indicator_view.setVisibility(View.VISIBLE);
                }
                else{
                    follow_follower_indicator_view_text.setText("Follow");
                    follow_follower_indicator_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(mode);
    }
}

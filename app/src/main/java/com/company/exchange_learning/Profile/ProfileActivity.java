package com.company.exchange_learning.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivityTAG";
    private String uid;
    private boolean mode = false;
    private UserProfile profile = null;
    private BasicUser basicUser = null;
    private CardView editBtn, followBtn, followerBtn;
    private CircleImageView profileImage;
    private RelativeLayout titleContainer, universityContainer, departmentContainer, communityContainer, location_container, skill_container, email_container;
    private TextView titleView, universityView, departmentView, communityView, locationView, skillView, emailView, nameView, overviewView, titleUpper;
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


        uid = getIntent().getStringExtra("uid");
        if (uid.equals(Constants.uid)) {
            mode = true;
        }
        loadData(mode);
    }


    public void loadViews() {
        toolbarTitle = findViewById(R.id.profile_user_name);
        editBtn = findViewById(R.id.editProfilebtn);
        profileImage = findViewById(R.id.profile_image);
        nameView = findViewById(R.id.name_view);
        overviewView = findViewById(R.id.overview_view);
        titleView = findViewById(R.id.title_view);
        titleContainer = findViewById(R.id.title_container);
        universityContainer = findViewById(R.id.university_container);
        universityView = findViewById(R.id.university_text);
        departmentContainer = findViewById(R.id.department_container);
        departmentView = findViewById(R.id.department_text);
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
        followBtn = findViewById(R.id.followBtn);
        followerBtn = findViewById(R.id.followersBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void populateViews(final UserProfile profile, boolean mode) {
        toolbarTitle.setText(StringUtils.capitalize(profile.getUser().getName()));
        if (!mode) {
            editBtn.setVisibility(View.GONE);
        }
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
            nameView.setText(StringUtils.capitalize(profile.getUser().getName()));
        } else {
            communityContainer.setVisibility(View.GONE);
            email_container.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
        }
        if (profile.getMy_overview() != null && !profile.getMy_overview().equals("")) {
            overviewView.setVisibility(View.VISIBLE);
            overviewView.setText(profile.getMy_overview());
        } else {
            overviewView.setVisibility(View.GONE);
        }
        if (profile.getMy_department() != null && !profile.getMy_department().equals("")) {
            departmentContainer.setVisibility(View.VISIBLE);
            departmentView.setText(profile.getMy_department());
        } else {
            departmentContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_title() != null && !profile.getMy_title().equals("")) {
            titleContainer.setVisibility(View.VISIBLE);
            titleView.setText(StringUtils.capitalize(profile.getMy_title()));
            titleUpper.setText(StringUtils.capitalize(profile.getMy_title()));
        } else {
            titleUpper.setVisibility(View.GONE);
            titleContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_university() != null && !profile.getMy_university().equals("")) {
            universityContainer.setVisibility(View.VISIBLE);
            universityView.setText(StringUtils.capitalize(profile.getMy_university()));
        } else {
            universityContainer.setVisibility(View.GONE);
        }

        //TODO:GET SKILLS AND SHOW THEM

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
        editBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

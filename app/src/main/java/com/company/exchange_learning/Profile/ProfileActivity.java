package com.company.exchange_learning.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.model.BasicUser;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivityTAG";
    private String uid;
    private boolean mode = false;
    private UserProfile profile = null;
    private BasicUser basicUser = null;
    private ImageView editBtn;
    private CircleImageView profileImage;
    private ImageView genderImage;
    private TextView nameView;
    private TextView overviewView;
    private RelativeLayout titleContainer,universityContainer,departmentContainer,communityContainer,location_container,skill_container,email_container;
    private TextView titleView,universityView,departmentView,communityView,locationView,skillView,emailView;
    private LinearLayout data_holder, follow_followers_buttons;
    private AppBarLayout app_bar;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout toolbar;
    private Button followersButton, followingButton;
    private TextView followButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        toolbar = findViewById(R.id.toolbar_layout);
        loadViews();
        data_holder.setVisibility(View.GONE);
        app_bar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        uid = getIntent().getStringExtra("uid");
        if(uid.equals(Constants.uid)){
            mode = true;
        }
        loadData(mode);
    }

    public void loadViews(){
        editBtn = findViewById(R.id.editProfilebtn);
        profileImage = findViewById(R.id.profile_image);
        genderImage = findViewById(R.id.gender_view);
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
        app_bar = findViewById(R.id.app_bar);
        progressBar = findViewById(R.id.profile_progress_bar);
    }

    public void populateViews(final UserProfile profile, boolean mode){
        data_holder.setVisibility(View.VISIBLE);
        app_bar.setVisibility(View.VISIBLE);
        toolbar.setTitle(profile.getUser().getName());
        progressBar.setVisibility(View.GONE);
        if (!mode){
            editBtn.setVisibility(View.GONE);
            follow_followers_buttons.setVisibility(View.GONE);
            followButton.setVisibility(View.VISIBLE);
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
        if (profile.getUser() != null ) {
            communityContainer.setVisibility(View.VISIBLE);
            email_container.setVisibility(View.VISIBLE);
            nameView.setVisibility(View.VISIBLE);
            genderImage.setVisibility(View.VISIBLE);
            String location = profile.getUser().getCity_name()+","+profile.getUser().getCountry_name();
            locationView.setText(location);
            communityView.setText(profile.getUser().getCommunity());
            emailView.setText(profile.getUser().getEmail());
            nameView.setText(profile.getUser().getName());
            if (profile.getUser().getGender().equals("Male")) {
                genderImage.setImageDrawable(getDrawable(R.drawable.male_icon_24));
            } else {
                genderImage.setImageDrawable(getDrawable(R.drawable.female_icon_24));
            }
        }
        else{
            communityContainer.setVisibility(View.GONE);
            email_container.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            genderImage.setVisibility(View.GONE);
        }
        if (profile.getMy_overview() != null && !profile.getMy_overview().equals("")){
            overviewView.setVisibility(View.VISIBLE);
            overviewView.setText(profile.getMy_overview());
        }
        else{
            overviewView.setVisibility(View.GONE);
        }
        if(profile.getMy_department() != null && !profile.getMy_department().equals("")){
            departmentContainer.setVisibility(View.VISIBLE);
            departmentView.setText(profile.getMy_department());
        }
        else{
            departmentContainer.setVisibility(View.GONE);
        }
        if(profile.getMy_title() != null && !profile.getMy_title().equals("")){
            titleContainer.setVisibility(View.VISIBLE);
            titleView.setText(profile.getMy_title());
        }
        else{
            titleContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_university() != null && !profile.getMy_university().equals("")){
            universityContainer.setVisibility(View.VISIBLE);
            universityView.setText(profile.getMy_university());
        }
        else{
            universityContainer.setVisibility(View.GONE);
        }
        if (profile.getMy_skills() != null && !profile.getMy_skills().equals("")){
            skill_container.setVisibility(View.VISIBLE);
            skillView.setText(profile.getMy_skills());
        }
        else{
            skill_container.setVisibility(View.GONE);
        }
        loadImage();
    }

    private void loadImage(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ uid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG,"Starting Load");
                String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG,"Error Loading Image");
            }
        });
    }

    private void loadData(final boolean mode) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                basicUser = dataSnapshot.getValue(BasicUser.class);
                if (basicUser!= null) {
                    Log.d(TAG, "User ------" + basicUser.toString());
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
                            Log.d(TAG, "loadUser:onCancelled", databaseError.toException());
                            profile = new UserProfile();
                            profile.setUser(basicUser);
                            populateViews(profile, mode);
                        }
                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this,"Error fetching profile",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"Error fetching profile",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "loadProfile:onCancelled", databaseError.toException());
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(uid);
        myRef.addListenerForSingleValueEvent(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(mode);
    }
}

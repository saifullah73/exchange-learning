package com.company.exchange_learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.Login.loginActivity;
import com.company.exchange_learning.Profile.ProfileActivity;
import com.company.exchange_learning.adapters.PostsAdapter;
import com.company.exchange_learning.model.BasicUser;
import com.company.exchange_learning.model.PostModel;
import com.company.exchange_learning.model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    CircleImageView drawerProfileImagView;
    NavigationView navigationView;
    TextView userCommunityTxtView, showAllPostsBtn, showMyCommunityPostsBtn;
    CircleImageView profileImagePost;
    TextView drawerUserName,postUserName;
    BasicUser basicUser;


    RecyclerView recyclerView;
    List<PostModel> mPosts;
    PostsAdapter mAdapter;
    LinearLayout goToProfile,logout;

    LinearLayout emptyMsgBtn, createPostBtn, uploadImgBtn;

    DatabaseReference postDataRef, userInfoRef;

    AVLoadingIndicatorView aviLoadingView;

    CardView mainHeader;
    CardView postSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        showAllPostsBtn = findViewById(R.id.main_all_post_btn);
        showMyCommunityPostsBtn = findViewById(R.id.main_community_post_btn);
        emptyMsgBtn = findViewById(R.id.empty_msg);
        createPostBtn = findViewById(R.id.main_create_post_btn);
        uploadImgBtn = findViewById(R.id.main_upload_image_btn);
        aviLoadingView = findViewById(R.id.avi);
        goToProfile = findViewById(R.id.drawer_porfile);
        logout = findViewById(R.id.drawer_logout);
        mainHeader = findViewById(R.id.main_header);
        postSwitchBtn = findViewById(R.id.postSelectorLayout);
        drawerUserName = findViewById(R.id.drawer_user_name);
        postUserName = findViewById(R.id.main_welcome_txtView);
        profileImagePost = findViewById(R.id.main_user_img_imgView);
        loadUserDetails();

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("uid",Constants.uid);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences prefs = getSharedPreferences("loginDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", "none");
                editor.putString("password", "none");
                editor.apply();
                Intent i = new Intent(MainActivity.this,loginActivity.class);
                startActivity(i);
                finish();
            }
        });

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        showAllPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllPostsBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.postSelectorBtnColor));
                showAllPostsBtn.setTextColor(Color.WHITE);
                showMyCommunityPostsBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.btnUnselectedColor));
                showMyCommunityPostsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
            }
        });

        showMyCommunityPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyCommunityPostsBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.postSelectorBtnColor));
                showMyCommunityPostsBtn.setTextColor(Color.WHITE);
                showAllPostsBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.btnUnselectedColor));
                showAllPostsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkGray));
            }
        });

        drawerProfileImagView = navigationView.findViewById(R.id.drawer_profile_img);
        userCommunityTxtView = navigationView.findViewById(R.id.drawer_user_community);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initRecyclerView();
        subscribeToPosts();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.posts_recylerview);
        mPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(mPosts, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void loadUserDetails(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ Constants.uid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG,"Starting Load");
                String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(drawerProfileImagView);
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(profileImagePost);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG,"Error Loading Image");
            }
        });
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                basicUser = dataSnapshot.getValue(BasicUser.class);
                if (basicUser!= null) {
                    Log.d(TAG, "User ------" + basicUser.toString());
                    drawerUserName.setText(basicUser.getName());
                    postUserName.setText("Welcome, "+basicUser.getName() + "!");
                    userCommunityTxtView.setText(basicUser.getCommunity());
                    updateDrawerProfileImageBorder(basicUser.getCommunity());
                }
                else{
                    Log.d(TAG, "Error fetching user details");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "loadProfile:onCancelled", databaseError.toException());
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(Constants.uid);
        myRef.addListenerForSingleValueEvent(listener);

    }

    private void fetchPostUserInfo(final PostModel post, final long totalPosts) {
        userInfoRef = FirebaseDatabase.getInstance().getReference("User_Information");
        userInfoRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        post.setPostUserPostedName(dataSnapshot.child("name").getValue().toString());
                    } else {
                        post.setPostUserPostedName("No Name");
                    }
                    mPosts.add(post);
                    if (mPosts.size() == totalPosts) {
                        hideProgressBar();
                        PostModel post = new PostModel(null, null, null, null, "NoMorePost", null, null, null, null, null);
                        mPosts.add(post);
                        mAdapter.notifyDataSetChanged();
                        Log.d("datadebug", "onDataChange: VIEW: " + "Showing now");
                        emptyMsgBtn.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        postSwitchBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("datadebug", "onDataChange: VIEW: " + "Still hiding");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error While Loading Posts", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgressBar() {
        aviLoadingView.show();
    }

    private void hideProgressBar() {
        aviLoadingView.hide();
    }

    private void subscribeToPosts() {
        showProgressBar();
        postDataRef = FirebaseDatabase.getInstance().getReference("Posts_Table");
        postDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChildren()) {
                        long totalPosts = dataSnapshot.getChildrenCount();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            PostModel post = dsp.getValue(PostModel.class);
                            assert post != null;
                            fetchPostUserInfo(post, totalPosts);
                        }
                    } else {
                        hideProgressBar();
                        emptyMsgBtn.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    hideProgressBar();
                    emptyMsgBtn.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error While Loading Posts", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void updateDrawerProfileImageBorder(String community) {
        Log.d(TAG,"Community ="+  community);
        if (community.equals("Architecture")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_arch));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_arch));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_arch), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Biosciences")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_bioSci));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_bioSci));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_bioSci), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Chemical Engineering")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_chemEng));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_chemEng));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_chemEng), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Chemistry")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_chem));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_chem));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_chem), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Civil Engineering")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_civil));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_civil));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_civil), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Computer Science")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_cs));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_cs));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Department of Biotechnology")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_dobt));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_dobt));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_dobt), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Development Studies")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_devStd));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_devStd));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_devStd), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Earth Sciences")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_es));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_es));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_es), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Economics")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_economics));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_economics));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_economics), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Electrical and Computer Engineering")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_ece));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_ece));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_ece), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Environmental Sciences")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_env_s));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_env_s));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_env_s), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Health Informatics")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_health));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_health));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_health), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Humanities")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_hum));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_hum));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_hum), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Management Sciences")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_ms));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_ms));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_ms), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Mathematics")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_math));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_math));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_math), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Mechanical Engineering")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_me));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_me));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_me), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Meteorology")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_met));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_met));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_met), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Pharmacy")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_pharm));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_pharm));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_pharm), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Physics")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_physics));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_physics));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_physics), PorterDuff.Mode.SRC_ATOP);
        }
        else if (community.equals("Statistics")) {
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_stats));
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_stats));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_stats), PorterDuff.Mode.SRC_ATOP);
        }
        else{
            drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_stats));
            profileImagePost.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_stats));
            userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_stats), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
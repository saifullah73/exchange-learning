package com.company.exchange_learning;

import android.graphics.Color;
import android.graphics.PorterDuff;
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

import com.company.exchange_learning.adapters.PostsAdapter;
import com.company.exchange_learning.model.PostModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    CircleImageView drawerProfileImagView;
    NavigationView navigationView;
    TextView userCommunityTxtView, showAllPostsBtn, showMyCommunityPostsBtn;

    RecyclerView recyclerView;
    List<PostModel> mPosts;
    PostsAdapter mAdapter;

    LinearLayout emptyMsgBtn, createPostBtn, uploadImgBtn;

    DatabaseReference postDataRef, userInfoRef;

    AVLoadingIndicatorView aviLoadingView;

    CardView mainHeader;
    CardView postSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidThreeTen.init(getApplicationContext());
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
        mainHeader = findViewById(R.id.main_header);
        postSwitchBtn = findViewById(R.id.postSelectorLayout);

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

        updateDrawerProfileImageBorder();
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


    private void updateDrawerProfileImageBorder() {
        drawerProfileImagView.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.category_cs));
        userCommunityTxtView.getBackground().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
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

package com.company.exchange_learning.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.Proposals.ProposalListActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.Notification;
import com.company.exchange_learning.model.PostModel;
import com.company.exchange_learning.model.Proposal;
import com.company.exchange_learning.model.Report;
import com.company.exchange_learning.model.UserProfile;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.text.WordUtils;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "PostDetailActivity";

    Toolbar toolbar;
    CircleImageView userImage;
    TextView userName, postType, postTimeDate, postTitle, postBody, exchangeTxt, learningTxt, submitProposalButtonText;
    LinearLayout firstCategory, secondCategory, thirdCategory, firstSkills, secondSkills, thirdSkills, skillsHolder, communityHolder;
    ImageView postMainImage, moreBtn;
    CardView submitProposalBtn, retryBtn;
    UserProfile profile;
    private Menu menu;

    PostModel post;

    DatabaseReference mProposalRef;
    DatabaseReference mProfileRef;
    boolean shouldHideEditBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPostType();
    }

    private void checkPostType() {
        post = (PostModel) getIntent().getSerializableExtra("PostObject");
        if (post != null) {
            showGeneralLoadingLayout();
            if (post.getShow_skills().equalsIgnoreCase("Yes")) {
                fetchSkills(post);
            } else {
                checkProposalStatus(post);
            }
        }
    }

    private void showErrorLayout() {
        setContentView(R.layout.error_layout);
        retryBtn = findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSkills(post);
            }
        });
    }

    private void fetchSkills(final PostModel post) {
        mProfileRef = FirebaseDatabase.getInstance().getReference("Profile_Information").child(post.getUser_id());
        mProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChildren()) {
                        profile = dataSnapshot.getValue(UserProfile.class);
                        checkProposalStatus(post);
                    } else {
                        checkProposalStatus(post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorLayout();
            }
        });
    }

    private void checkProposalStatus(final PostModel post) {
        if (!post.getUser_id().equals(Constants.getConstantUid())) {
            shouldHideEditBtn = true;
            invalidateOptionsMenu();
            mProposalRef = FirebaseDatabase.getInstance().getReference("Post_Proposal_Table");
            mProposalRef.child(post.getPost_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChildren()) {
                            boolean isProposalSubmitted = false;
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                String sId = dsp.child("submitter_id").getValue().toString();
                                if (sId.equals(Constants.getConstantUid())) {
                                    isProposalSubmitted = true;
                                    break;
                                }
                            }
                            if (post.getPost_image() == null || post.getPost_image().equals("")) {
                                handlePostWithNoImage(post, isProposalSubmitted,false);
                            } else {
                                handlePostWithImage(post, isProposalSubmitted,false);
                            }
                        } else {
                            if (post.getPost_image() == null || post.getPost_image().equals("")) {
                                handlePostWithNoImage(post, false,false);
                            } else {
                                handlePostWithImage(post, false,false);
                            }
                        }
                    } else {
                        if (post.getPost_image() == null || post.getPost_image().equals("")) {
                            handlePostWithNoImage(post, false,false);
                        } else {
                            handlePostWithImage(post, false,false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (post.getPost_image() == null || post.getPost_image().equals("")) {
                        handlePostWithNoImage(post, false,false);
                    } else {
                        handlePostWithImage(post, false,false);
                    }
                }
            });
        } else {
            shouldHideEditBtn = false;
            invalidateOptionsMenu();
            if (post.getPost_image() == null || post.getPost_image().equals("")) {
                handlePostWithNoImage(post, true, true);
            } else {
                handlePostWithImage(post, true,true);
            }
        }
    }

    private void handlePostWithImage(final PostModel post, boolean isProposalSubmitted, boolean isCurrentUser) {
        setContentView(R.layout.activity_image_post_detail);
        initToolbar();
        exchangeTxt.setText("Post Detail");
        userImage = findViewById(R.id.postImageDetailUserImgView);
        userName = findViewById(R.id.postImageDetailPostedUserName);
        postType = findViewById(R.id.postImageDetailPostType);
        postTimeDate = findViewById(R.id.postImageDetailPostTime);
        postBody = findViewById(R.id.postImageDetailPostInfo);
        postMainImage = findViewById(R.id.postImageDetailPostMainImage);
        firstCategory = findViewById(R.id.postImageDetailFirstCategory);
        secondCategory = findViewById(R.id.postImageDetailSecondCategory);
        thirdCategory = findViewById(R.id.postImageDetailThirdCategory);
        moreBtn = findViewById(R.id.postImageDetailMoreBtn);
        if (!isCurrentUser) {
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(view);
                }
            });
        }else{
            moreBtn.setVisibility(View.GONE);
        }

        firstSkills = findViewById(R.id.postImageDetailFirstSkills);
        secondSkills = findViewById(R.id.postImageDetailSecondSkills);
        thirdSkills = findViewById(R.id.postImageDetailThirdSkills);

        skillsHolder = findViewById(R.id.footer2);
        communityHolder = findViewById(R.id.footer);

        postMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostDetailActivity.this, ImageDetailActivity.class).putExtra("imageUrl", post.getPost_image()));
            }
        });
        submitProposalBtn = findViewById(R.id.postImageSubmitProposalBtn);
        submitProposalButtonText = findViewById(R.id.submit_proposal_button_text_image);

        Log.i("TESTBC", isProposalSubmitted + " " + isCurrentUser);
        if (!isProposalSubmitted) {
            submitProposalBtn.setVisibility(View.VISIBLE);
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSAL");
            } else {
                submitProposalButtonText.setText("SUBMIT PROPOSAL");
            }
        }else {
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSAL");
                submitProposalBtn.setVisibility(View.VISIBLE);
            } else {
                submitProposalBtn.setVisibility(View.GONE);
            }
        }
        submitProposalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitProposalButtonText.getText().equals("SUBMIT PROPOSAL")) {
                    showSendProposalDialog();
                }else{
                    Intent i = new Intent(PostDetailActivity.this, ProposalListActivity.class);
                    i.putExtra("id",post.post_id);
                    i.putExtra("mode","Post");
                    startActivity(i);
                }
            }
        });

        userName.setText(post.getPost_user_posted_name() == null ? "NoName" : WordUtils.capitalize(post.getPost_user_posted_name()));
        postType.setText(post.getPost_type());
        postTimeDate.setText(post.getPost_date());
        postBody.setText(post.getPost_image_info());
        Glide.with(this).load(post.getPost_user_posted_image()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.main_user_profile_avatar).into(userImage);

        Glide.with(this).load(post.getPost_image()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.main_post_image_avatart).into(postMainImage);

        populateCommunities(post);
        populateSkills();
    }

    private void showReportMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.inflate(R.menu.report_menu);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    private void handlePostWithNoImage(final PostModel post, boolean isProposalSubmitted, boolean isCurrentUser) {
        setContentView(R.layout.activity_no_image_post_detail);
        initToolbar();
        exchangeTxt.setText("Post Detail");
        userImage = findViewById(R.id.noPostImageDetailUserImgView);
        userName = findViewById(R.id.noPostImageDetailPostedUserName);
        postType = findViewById(R.id.noPostImageDetailPostType);
        postTimeDate = findViewById(R.id.noPostImageDetailPostTime);
        postTitle = findViewById(R.id.noPostImageDetailPostTitle);
        postBody = findViewById(R.id.noPostImageDetailPostBody);
        submitProposalBtn = findViewById(R.id.postNoImageSubmitProposalBtn);
        submitProposalButtonText = findViewById(R.id.submit_proposal_button_text_no_image);
        firstCategory = findViewById(R.id.noPostImageDetailFirstCategory);
        secondCategory = findViewById(R.id.noPostImageDetailSecondCategory);
        thirdCategory = findViewById(R.id.noPostImageDetailThirdCategory);
        moreBtn = findViewById(R.id.noPostImageDetailMoreBtn);
        firstSkills = findViewById(R.id.noPostImageDetailFirstSkills);
        secondSkills = findViewById(R.id.noPostImageDetailSecondSkills);
        thirdSkills = findViewById(R.id.noPostImageDetailThirdSkills);

        skillsHolder = findViewById(R.id.footer2);
        communityHolder = findViewById(R.id.footer);
        if (!isCurrentUser) {
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReportMenu(view);
                }
            });
        }else{
            moreBtn.setVisibility(View.GONE);
        }
        Log.i("TESTBC", isProposalSubmitted + " " + isCurrentUser);
        if (!isProposalSubmitted) {
            submitProposalBtn.setVisibility(View.VISIBLE);
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSAL");
            } else {
                submitProposalButtonText.setText("SUBMIT PROPOSAL");
            }
        }else {
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSAL");
                submitProposalBtn.setVisibility(View.VISIBLE);
            } else {
                submitProposalBtn.setVisibility(View.GONE);
            }
        }
        submitProposalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitProposalButtonText.getText().equals("SUBMIT PROPOSAL")) {
                    showSendProposalDialog();
                }else{
                    Intent i = new Intent(PostDetailActivity.this, ProposalListActivity.class);
                    i.putExtra("id",post.post_id);
                    i.putExtra("mode","Post");
                    startActivity(i);
                }
            }
        });

        userName.setText(post.getPost_user_posted_name() == null ? "NoName" : WordUtils.capitalize(post.getPost_user_posted_name()));
        postType.setText(post.getPost_type());
        postTimeDate.setText(post.getPost_date());
        postTitle.setText(post.getPost_title());
        postBody.setText(post.getPost_body());
        Glide.with(this).load(post.getPost_user_posted_image()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.main_user_profile_avatar).into(userImage);

        populateCommunities(post);
        populateSkills();
    }

    private void showSendProposalDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.send_proposal_layout, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.show();
        final EditText proposal_text = view.findViewById(R.id.sp_proposal_field);
        final TextView submit = view.findViewById(R.id.send_proposal);
        final TextView cancel = view.findViewById(R.id.cancel_proposal_dialog);
        final AVLoadingIndicatorView progress = view.findViewById(R.id.submit_proposal_loader);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m_Text = proposal_text.getText().toString().trim();
                if ( m_Text.length() > 49) {
                    submit.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    progress.show();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Post_Proposal_Table").child(post.getPost_id());
                    Proposal proposal = new Proposal(m_Text,getTimeDate(),Constants.getConstantUid());
                    myRef.push().setValue(proposal, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.i("TASTA",databaseReference.getKey());
                                DatabaseReference myRef2 = database.getReference("Notification_Table/Proposal").child(post.getUser_id());
                                Notification notif = new Notification(getTimeDate(),post.getPost_id(),databaseReference.getKey(),"exchangelearning");
                                myRef2.push().setValue(notif, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@androidx.annotation.Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null){
                                            Toast.makeText(PostDetailActivity.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            submitProposalBtn.setVisibility(View.GONE);
                                        }else{
                                            submit.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(View.VISIBLE);
                                            progress.hide();
                                            Toast.makeText(PostDetailActivity.this, "Some Error occurred while submitting", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Log.i("TASTA",databaseError.getMessage());
                                submit.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                progress.hide();
                                Toast.makeText(PostDetailActivity.this, "Some Error occurred while submitting", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(PostDetailActivity.this, "Proposal must be atleast 50 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
    }

    private void populateCommunities(PostModel post) {
        if (post.getTagged_communities().size() > 0) {
            communityHolder.setVisibility(View.VISIBLE);
            int n = post.getTagged_communities().size();
            if (n <= 2) {
                secondCategory.setVisibility(View.GONE);
                thirdCategory.setVisibility(View.GONE);
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    firstCategory.addView(textView);
                }
            } else {
                int dist = 0;
                if (n <= 6) {
                    secondCategory.setVisibility(View.VISIBLE);
                    dist = 2;
                } else {
                    thirdCategory.setVisibility(View.VISIBLE);
                    dist = 3;
                }
                int len = 0;
                int layoutToAdd = 1;
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    if (dist == 2) {
                        if (layoutToAdd == 1) {
                            firstCategory.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else {
                            secondCategory.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 1;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        }
                    } else {
                        if (layoutToAdd == 1) {
                            firstCategory.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else if (layoutToAdd == 2) {
                            secondCategory.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 3;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        } else {
                            thirdCategory.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 3;
                            }
                        }
                    }
                }
            }
        } else {
            communityHolder.setVisibility(View.GONE);
        }
    }

    private void populateSkills() {
        if (profile != null) {
            if (profile.getMy_skills() != null && profile.getMy_skills().size() > 0) {
                skillsHolder.setVisibility(View.VISIBLE);
                int n = profile.getMy_skills().size();
                if (n <= 2) {
                    secondSkills.setVisibility(View.GONE);
                    thirdSkills.setVisibility(View.GONE);
                    for (int i = 0; i < profile.getMy_skills().size(); i++) {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(profile.getMy_skills().get(i));
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(14);
                        textView.setBackgroundResource(R.drawable.background_search);
                        textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMarginStart(16);
                        textView.setLayoutParams(lp);
                        textView.setPadding(8, 4, 8, 4);
                        firstSkills.addView(textView);
                    }
                } else {
                    int dist = 0;
                    if (n <= 6) {
                        secondSkills.setVisibility(View.VISIBLE);
                        dist = 2;
                    } else {
                        thirdSkills.setVisibility(View.VISIBLE);
                        dist = 3;
                    }
                    int len = 0;
                    int layoutToAdd = 1;
                    for (int i = 0; i < profile.getMy_skills().size(); i++) {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(profile.getMy_skills().get(i));
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(14);
                        textView.setBackgroundResource(R.drawable.background_search);
                        textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMarginStart(16);
                        textView.setLayoutParams(lp);
                        textView.setPadding(8, 4, 8, 4);
                        if (dist == 2) {
                            if (layoutToAdd == 1) {
                                firstSkills.addView(textView);
                                len += profile.getMy_skills().get(i).length();
                                if (len > 35) {
                                    layoutToAdd = 2;
                                    len = 0;
                                } else {
                                    layoutToAdd = 1;
                                }
                            } else {
                                secondSkills.addView(textView);
                                len += profile.getMy_skills().get(i).length();
                                if (len > 35) {
                                    layoutToAdd = 1;
                                    len = 0;
                                } else {
                                    layoutToAdd = 2;
                                }
                            }
                        } else {
                            if (layoutToAdd == 1) {
                                firstSkills.addView(textView);
                                len += profile.getMy_skills().get(i).length();
                                if (len > 35) {
                                    layoutToAdd = 2;
                                    len = 0;
                                } else {
                                    layoutToAdd = 1;
                                }
                            } else if (layoutToAdd == 2) {
                                secondSkills.addView(textView);
                                len += profile.getMy_skills().get(i).length();
                                if (len > 35) {
                                    layoutToAdd = 3;
                                    len = 0;
                                } else {
                                    layoutToAdd = 2;
                                }
                            } else {
                                thirdSkills.addView(textView);
                                len += profile.getMy_skills().get(i).length();
                                if (len > 35) {
                                    layoutToAdd = 2;
                                    len = 0;
                                } else {
                                    layoutToAdd = 3;
                                }
                            }
                        }
                    }
                }
            } else {
                skillsHolder.setVisibility(View.GONE);
            }
        } else {
            skillsHolder.setVisibility(View.GONE);
        }
    }

    private int randomizeColor() {
        int[] colors = {R.color.category_bioSci, R.color.category_chem, R.color.category_chemEng, R.color.category_hum, R.color.category_met
                , R.color.category_civil, R.color.category_cs, R.color.category_devStd, R.color.category_math, R.color.category_pharm};
        return ContextCompat.getColor(getApplicationContext(), colors[new Random().nextInt(colors.length)]);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        exchangeTxt = findViewById(R.id.exchange_txt);
        learningTxt = findViewById(R.id.learning_txt);
        learningTxt.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.detail_activitiy_menu, menu);
        MenuItem item = menu.findItem(R.id.editPostBtn);
        if (!shouldHideEditBtn) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editPostBtn:
                editPost();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editPost() {
        if (post.getPost_image() == null || post.getPost_image().equals("")) {
            startActivity(new Intent(PostDetailActivity.this, CreateNoImagePostActivity.class).putExtra("actionType", "editPost").putExtra("post", post));
        } else {
            startActivity(new Intent(PostDetailActivity.this, CreateImagePostActivity.class).putExtra("actionType", "editPost").putExtra("post", post));
        }
    }

    private void showGeneralLoadingLayout() {
        setContentView(R.layout.loading_layout);
    }

    private void reportProposal(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.report_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        TextView msg = view.findViewById(R.id.msg);
        msg.setText("Are you sure you want to report this post");
        AVLoadingIndicatorView loader = view.findViewById(R.id.report_loader);
        CardView send = view.findViewById(R.id.report_dialog_button);
        TextView cancel = view.findViewById(R.id.report_dialog_cancel);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref;
                ref = database.getReference("Reports").child("post_report").child("exchangelearning");
                Report report = new Report(post.getUser_id(),Constants.getConstantUid(),post.getPost_id());
                ref.push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Toast.makeText(PostDetailActivity.this, "Reported Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(PostDetailActivity.this, "Unexpected error while reporting", Toast.LENGTH_SHORT).show();
                            send.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);
                        }
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

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.report:
                reportProposal();
                return true;
            default:
                return false;
        }
    }
}



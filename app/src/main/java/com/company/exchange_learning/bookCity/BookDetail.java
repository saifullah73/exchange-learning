package com.company.exchange_learning.bookCity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.Proposals.ProposalListActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.ImageDetailActivity;
import com.company.exchange_learning.activities.PostDetailActivity;
import com.company.exchange_learning.model.Book;
import com.company.exchange_learning.model.Notification;
import com.company.exchange_learning.model.Proposal;
import com.company.exchange_learning.model.Report;
import com.company.exchange_learning.model.UserProfile;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDetail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    Toolbar toolbar;
    CircleImageView userImage;
    TextView userName, bookType,bookAddr,bookPrice, bookTitle, bookDesc, exchangeTxt, learningTxt, submitProposalButtonText;
    LinearLayout firstCategory, secondCategory, thirdCategory,communityHolder;
    ImageView bookMainImage, moreBtn;
    CardView submitProposalBtn, retryBtn;
    private Menu menu;
    public Book book;

    DatabaseReference mProposalRef;
    boolean shouldHideEditBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBookType();
    }

    private void checkBookType() {
        book = (Book) getIntent().getSerializableExtra("book");
        if (book != null) {
            showGeneralLoadingLayout();
            checkProposalStatus(book);
        }
    }

    private void showErrorLayout() {
        setContentView(R.layout.error_layout);
        retryBtn = findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProposalStatus(book);
            }
        });
    }


    private void checkProposalStatus(final Book book) {
        if (!book.getUser_id().equals(Constants.getConstantUid())) {
            shouldHideEditBtn = true;
            invalidateOptionsMenu();
            mProposalRef = FirebaseDatabase.getInstance().getReference("Book_Proposal_Table");
            mProposalRef.child(book.getBook_id()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            handleBookWithImage(book, isProposalSubmitted,false);
                        } else {
                            handleBookWithImage(book, false,false);
                        }
                    } else {
                        handleBookWithImage(book, false,false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    handleBookWithImage(book, false,false);
                }
            });
        } else {
            shouldHideEditBtn = false;
            invalidateOptionsMenu();
            handleBookWithImage(book, true,true);
        }
    }

    private void handleBookWithImage(final Book book, boolean isProposalSubmitted, boolean isCurrentUser) {
        setContentView(R.layout.view_book_details);
        initToolbar();
        exchangeTxt.setText("Book Detail");
        userImage = findViewById(R.id.bookImageDetailUserImgView);
        userName = findViewById(R.id.bookImageDetailPostedUserName);
        bookType = findViewById(R.id.bookImageDetailbookType);
        bookPrice = findViewById(R.id.bookImageDetailBookPrice);
        bookTitle = findViewById(R.id.bookImagetitle);
        bookAddr = findViewById(R.id.bookImageAddr);
        bookDesc = findViewById(R.id.bookImageDesc);
        bookMainImage = findViewById(R.id.bookImageDetailBookMainImage);
        firstCategory = findViewById(R.id.bookImageDetailFirstCategory);
        secondCategory = findViewById(R.id.bookImageDetailSecondCategory);
        thirdCategory = findViewById(R.id.bookImageDetailThirdCategory);
        moreBtn = findViewById(R.id.bookImageDetailMoreBtn);
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
        communityHolder = findViewById(R.id.footer);
        bookMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookDetail.this, ImageDetailActivity.class).putExtra("imageUrl", book.getCover_photo()));
            }
        });
        submitProposalBtn = findViewById(R.id.bookImageSubmitProposalBtn);
        submitProposalButtonText = findViewById(R.id.submit_proposal_button_text_image);

        Log.i("TESTBC",String.valueOf(isProposalSubmitted) + " " + String.valueOf(isCurrentUser));
        if (!isProposalSubmitted) {
            submitProposalBtn.setVisibility(View.VISIBLE);
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSALS");
            } else {
                submitProposalButtonText.setText("SUBMIT PROPOSALS");
            }
        }else {
            if (isCurrentUser) {
                submitProposalButtonText.setText("VIEW PROPOSALS");
                submitProposalBtn.setVisibility(View.VISIBLE);
            } else {
                submitProposalBtn.setVisibility(View.GONE);
            }
        }
        submitProposalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitProposalButtonText.getText().equals("SUBMIT PROPOSALS")) {
                    showSendProposalDialog();
                }else{
                    Intent i = new Intent(BookDetail.this, ProposalListActivity.class);
                    i.putExtra("id",book.getBook_id());
                    i.putExtra("mode","Book");
                    startActivity(i);
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(book.getUser_id()).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot == null ? "NoName" : WordUtils.capitalize(dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userName.setText("Username");
            }
        });
        bookTitle.setText(book.getBook_title());
        bookType.setText(book.getBook_type());
        if (book.getBook_price() != null && !book.getBook_price().equals("")) {
            bookPrice.setText(book.getBook_price() + "RS/-");
        }else{
            bookPrice.setVisibility(View.INVISIBLE);
        }
        bookDesc.setText(book.getBook_description());
        bookAddr.setText(book.getBook_address());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/" + book.getUser_id());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(userImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


        Glide.with(this).load(book.getCover_photo()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.main_post_image_avatart).into(bookMainImage);

        populateCommunities(book);
    }

    private void showReportMenu(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.inflate(R.menu.report_menu);
        menu.setOnMenuItemClickListener(this);
        menu.show();
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
                    DatabaseReference myRef = database.getReference("Book_Proposal_Table").child(book.getBook_id());
                    Proposal proposal = new Proposal(m_Text,getTimeDate(),Constants.getConstantUid());
                    myRef.push().setValue(proposal, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.i("TASTA",databaseReference.getKey());
                                DatabaseReference myRef2 = database.getReference("Notification_Table/Proposal").child(book.getUser_id());
                                Notification notif = new Notification(getTimeDate(),book.getBook_id(),databaseReference.getKey(),"bookcity");
                                myRef2.push().setValue(notif, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@androidx.annotation.Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null){
                                            Toast.makeText(BookDetail.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            submitProposalBtn.setVisibility(View.GONE);
                                        }else{
                                            submit.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(View.VISIBLE);
                                            progress.hide();
                                            Toast.makeText(BookDetail.this, "Some Error occurred while submitting", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Log.i("TASTA",databaseError.getMessage());
                                submit.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                progress.hide();
                                Toast.makeText(BookDetail.this, "Some Error occurred while submitting", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(BookDetail.this, "Proposal must be atleast 50 characters", Toast.LENGTH_SHORT).show();
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

    private void populateCommunities(Book book) {
        if (book.getTagged_communities().size() > 0) {
            communityHolder.setVisibility(View.VISIBLE);
            int n = book.getTagged_communities().size();
            if (n <= 2) {
                secondCategory.setVisibility(View.GONE);
                thirdCategory.setVisibility(View.GONE);
                for (int i = 0; i < book.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(book.getTagged_communities().get(i));
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
                for (int i = 0; i < book.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(book.getTagged_communities().get(i));
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
                            len += book.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else {
                            secondCategory.addView(textView);
                            len += book.getTagged_communities().get(i).length();
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
                            len +=book.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else if (layoutToAdd == 2) {
                            secondCategory.addView(textView);
                            len += book.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 3;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        } else {
                            thirdCategory.addView(textView);
                            len += book.getTagged_communities().get(i).length();
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editPost() {
        startActivity(new Intent(BookDetail.this, UploadBookActivity.class).putExtra("actionType", "editBook").putExtra("book", book));
    }

    private void showGeneralLoadingLayout() {
        setContentView(R.layout.loading_layout);
    }

    private void reportBook(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.report_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        TextView msg = view.findViewById(R.id.msg);
        msg.setText("Are you sure you want to report this book");
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
                ref = database.getReference("Reports").child("post_report").child("bookcity");
                Report report = new Report(book.getUser_id(), Constants.getConstantUid(),book.getBook_id());
                ref.push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Toast.makeText(BookDetail.this, "Reported Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(BookDetail.this, "Unexpected error while reporting", Toast.LENGTH_SHORT).show();
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
                reportBook();
                return true;
            default:
                return false;
        }
    }
}

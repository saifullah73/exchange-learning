package com.company.exchange_learning.Proposals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.Proposal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProposalActivity extends AppCompatActivity {
    private static final String TAG = "MyViewProposalActivity";
    private Proposal proposal;
    private CircleImageView proposalImage;
    private TextView proposalDate,proposalName,proposalData,proposalPostTitle,titleLabel;
    private CardView proposalAcceptBtn, proposalResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_proposal_activity);
        proposal = (Proposal) getIntent().getSerializableExtra("proposal");
        getSupportActionBar().setTitle("Proposal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(5);
        setView();
    }

    private void setView(){
        proposalImage = findViewById(R.id.proposal_image);
        proposalDate = findViewById(R.id.proposal_date);
        proposalName = findViewById(R.id.proposal_name);
        proposalData = findViewById(R.id.proposal_data);
        proposalPostTitle = findViewById(R.id.proposal_post_title);
        proposalAcceptBtn = findViewById(R.id.proposal_accept_layout);
        proposalResponse = findViewById(R.id.proposal_response_layout);
        titleLabel = findViewById(R.id.proposal_post_title_label);


        proposalDate.setText(proposal.getProposal_date());
        proposalData.setText(proposal.getProposal_data());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(proposal.getSubmitter_id()).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                proposalName.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                proposalName.setText(proposal.getSubmitter_id());
            }
        });
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ proposal.getSubmitter_id());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(ViewProposalActivity.this)
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(proposalImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG,"Error Loading Image");
            }
        });
        if (!proposal.getAccepted()){
            proposalAcceptBtn.setVisibility(View.VISIBLE);
            proposalResponse.setVisibility(View.GONE);
        }
        else{
            proposalAcceptBtn.setVisibility(View.GONE);
            proposalResponse.setVisibility(View.VISIBLE);
        }

        if (proposal.getNotif().getPlatform().equals("exchangelearning")){
            titleLabel.setText("Post Title");
            DatabaseReference myRef2 = database.getReference("Posts_Table").child(proposal.getNotif().getPost_id()).child("post_title");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String title = dataSnapshot.getValue(String.class);
                    if(title != null &&!title.equals("")) {
                        proposalPostTitle.setText(dataSnapshot.getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    proposalPostTitle.setText("No title");
                }
            });
        }else{
            titleLabel.setText("Book Title");
            DatabaseReference myRef2 = database.getReference("Books_City").child(proposal.getNotif().getPost_id()).child("book_title");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    proposalPostTitle.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    proposalPostTitle.setText("No title");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_proposal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.report_proposal:
                return true;
            case R.id.delete_proposal:
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

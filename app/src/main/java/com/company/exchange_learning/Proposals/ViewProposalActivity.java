package com.company.exchange_learning.Proposals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.ChatActivity;
import com.company.exchange_learning.model.Proposal;
import com.company.exchange_learning.model.Report;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProposalActivity extends AppCompatActivity {
    private static final String TAG = "MyViewProposalActivity";
    private static final int GO_TO_BOOK_CHAT = 0;
    private static final int GO_TO_POST_CHAT = 1;
    private Proposal proposal;
    private CircleImageView proposalImage;
    private TextView proposalDate,proposalName,proposalData,proposalPostTitle,titleLabel;
    private CardView proposalAcceptBtn, proposalResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_proposal_activity);
        proposal = (Proposal) getIntent().getSerializableExtra("proposal");
        markAsRead();
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
        proposalAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptProposal();
            }
        });
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
                reportProposal();
                return true;
            case R.id.delete_proposal:
                deleteProposal();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reportProposal(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.report_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        TextView msg = view.findViewById(R.id.msg);
        msg.setText("Are you sure you want to report this proposal");
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
                ref = database.getReference("Reports").child("proposal_report").child(proposal.getNotif().getPlatform());
                Report report = new Report(proposal.getSubmitter_id(),Constants.getConstantUid(),proposal.getNotif().getProposal_id());
                ref.push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            DatabaseReference myRef2;
                            Toast.makeText(ViewProposalActivity.this, "Reported Successfully", Toast.LENGTH_SHORT).show();
                            if (proposal.getNotif().getPlatform().equals("bookcity")) {
                                myRef2 = database.getReference("Book_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id()).child("reported");
                            }else{
                                myRef2 = database.getReference("Post_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id()).child("reported");
                            }
                            myRef2.setValue(true);
                            ProposalListActivity.getInstance().startRead();
                            finish();
                        }
                        else{
                            Toast.makeText(ViewProposalActivity.this, "Unexpected error while reporting", Toast.LENGTH_SHORT).show();
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

    private void deleteProposal(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewProposalActivity.this);
        builder1.setMessage("Are you sure you want to delete this proposal");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG,"Deleting Proposal");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref;
                        if (proposal.getNotif().getPlatform().equals("bookcity")) {
                            ref = database.getReference("Book_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id());
                        }
                        else{
                            ref = database.getReference("Post_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id());
                        }
                        ref.removeValue();
                        ProposalListActivity.getInstance().startRead();
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void markAsRead(){
        Log.i(TAG,"Notif id "+  proposal.getNotif().getNotification_id());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Notification_Table/Proposal").child(Constants.getConstantUid()).child(proposal.getNotif().getNotification_id()).child("read_at");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status == null || status.equals("")){
                    ref.setValue(getTimeDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ref.setValue(getTimeDate());
            }
        });
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
    }

    public void acceptProposal(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.accept_proposal_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        AVLoadingIndicatorView loader = view.findViewById(R.id.accept_report_loader);
        CardView accept = view.findViewById(R.id.accept_dialog_button);
        TextView cancel = view.findViewById(R.id.accept_dialog_cancel);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Deleting Proposal");
                accept.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref;
                if (proposal.getNotif().getPlatform().equals("bookcity")) {
                    ref = database.getReference("Book_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id()).child("accepted");
                }else{
                    ref = database.getReference("Post_Proposal_Table").child(proposal.getNotif().getPost_id()).child(proposal.getNotif().getProposal_id()).child("accepted");
                }
                ref.setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Toast.makeText(ViewProposalActivity.this, "Proposal Accepted", Toast.LENGTH_SHORT).show();
                            if (proposal.getNotif().getPlatform().equals("bookcity")) {
                                goToChat(GO_TO_BOOK_CHAT);
                            }else{
                                goToChat(GO_TO_POST_CHAT);
                            }
                        }
                        else{
                            Toast.makeText(ViewProposalActivity.this, "Unexpected error while accepting", Toast.LENGTH_SHORT).show();
                            accept.setVisibility(View.VISIBLE);
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

    private void goToChat(int mode){
        ProposalListActivity.getInstance().startRead();
        Log.i(TAG,String.valueOf(mode));
        if(mode == GO_TO_BOOK_CHAT ){
            Log.i(TAG,String.valueOf("sending to book"));
            Intent i = new Intent(ViewProposalActivity.this, ChatActivity.class);
            i.putExtra("type","book").putExtra("action","welcome").putExtra("uID",proposal.getSubmitter_id());
            startActivity(i);
        }else {
            Log.i(TAG,String.valueOf("sending to post"));
            Intent i = new Intent(ViewProposalActivity.this, ChatActivity.class);
            i.putExtra("type","post").putExtra("action","welcome").putExtra("uID",proposal.getSubmitter_id());
            startActivity(i);
        }
        finish();
    }
}
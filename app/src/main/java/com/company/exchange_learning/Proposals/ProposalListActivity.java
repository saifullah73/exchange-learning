package com.company.exchange_learning.Proposals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.adapters.FollowerAdapter;
import com.company.exchange_learning.adapters.ProposalAdapter;
import com.company.exchange_learning.model.Notification;
import com.company.exchange_learning.model.Proposal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ProposalListActivity extends AppCompatActivity {
    private static final String TAG = "ProposalListActivity";
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private LinearLayoutManager layoutManager;
    private ImageView errorImg;
    private RelativeLayout errorView;
    private CardView retryBtn;
    private TextView errorMsg;
    private String mode = "Post";
    private String key = "post";
    private String id;
    private ProposalAdapter adapter;
    private List<Notification> notifications;
    private List<Proposal> allProposals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_list);
        recyclerView = findViewById(R.id.proposal_recycler_view);
        loadingIndicatorView = findViewById(R.id.proposal_avi);
        errorView = findViewById(R.id.proposal_error);
        errorImg = findViewById(R.id.errImg);
        errorMsg = findViewById(R.id.errMsg);
        retryBtn = findViewById(R.id.retryBtn);
        loadingIndicatorView.findViewById(R.id.follower_avi);
        errorView.setVisibility(View.GONE);
        loadingIndicatorView.show();
        recyclerView.setVisibility(View.GONE);
        notifications = new ArrayList<>();
        mode = getIntent().getStringExtra("mode");
        id = getIntent().getStringExtra("id");



        try {
            if (mode.equals("Post")) {
                getSupportActionBar().setTitle("Post Proposal");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setElevation(5);
                key = "post";
                getData(mode,key,id);
            } else if(mode.equals("Book")) {
                getSupportActionBar().setTitle("Book Proposal");
                getSupportActionBar().setElevation(5);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                key = "bookstore";
                getData(mode,key,id);
            }else{
                getSupportActionBar().setTitle("Notifications");
                getSupportActionBar().setElevation(5);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getDataAll();
            }
        }catch (Exception e){
        }

    }

    public void getData(final String mode,final String key, final String id){
        notifications = new ArrayList<>();
        allProposals = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Notification_Table").child(Constants.uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Notification notif = dsp.getValue(Notification.class);
                        if (notif != null) {
                            if (notif.getPlatform().equals(key)) {
                                notifications.add(notif);
                            }
                        }
                    }
                    if (notifications.size() != 0) {
                        for (final Notification not : notifications) {
                            if (not.getProposal_id().equals(id)) {
                                final DatabaseReference myRef2 = database.getReference(mode + "_Proposal_Table").child(not.post_id).child(not.proposal_id);
                                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Proposal proposal = dataSnapshot.getValue(Proposal.class);
                                            proposal.setNotif(not);
                                            allProposals.add(proposal);
                                            if (allProposals.size() == 1){
                                                setData(allProposals);
                                            }
                                            else{
                                                notifyChanges();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                showErrorLayout("No Proposal Found",0);
                            }
                        }
                    }
                    else{
                        showErrorLayout("No Proposal Found",0);
                    }
                }else{
                    showErrorLayout("No Proposal Found",0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG,"Error occurred fetching data");
                Log.i(TAG,databaseError.getMessage());
                showErrorLayout("Some error occurred while loading data",1);
            }
        });
    }


    public void getDataAll(){
        notifications = new ArrayList<>();
        allProposals = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Notification_Table").child(Constants.uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Notification notif = dsp.getValue(Notification.class);
                        if (notif != null) {
                            notifications.add(notif);
                        }
                    }
                    if (notifications.size() != 0) {
                        for (final Notification not : notifications) {
                            DatabaseReference myRef2;
                            if (not.getPlatform().equals("post")){
                                myRef2 = database.getReference("Post_Proposal_Table").child(not.post_id).child(not.proposal_id);
                            }else{
                                myRef2 = database.getReference("Book_Proposal_Table").child(not.post_id).child(not.proposal_id);
                            }
                            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Proposal proposal = dataSnapshot.getValue(Proposal.class);
                                        proposal.setNotif(not);
                                        allProposals.add(proposal);
                                        if (allProposals.size() == 1){
                                            setData(allProposals);
                                        }
                                        else{
                                            notifyChanges();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    else{
                        showErrorLayout("No Proposal Found",0);
                    }
                }else{
                    showErrorLayout("No Proposal Found",0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG,"Error occurred fetching data");
                Log.i(TAG,databaseError.getMessage());
                showErrorLayout("Some error occurred while loading data",1);
            }
        });
    }

    public void setData(List data){
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicatorView.hide();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new ProposalAdapter(data,getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    public void notifyChanges(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorLayout(String msg,int type) {
        errorView.setVisibility(View.VISIBLE);
        loadingIndicatorView.hide();
        recyclerView.setVisibility(View.GONE);
        errorMsg.setText(msg);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorView.setVisibility(View.GONE);
                loadingIndicatorView.show();
                recyclerView.setVisibility(View.GONE);
                if (key == null){
                    getDataAll();
                }else {
                    getData(mode,key,id);
                }
            }
        });
        if (type == 0){
            retryBtn.setVisibility(View.GONE);
            errorImg.setImageDrawable(getDrawable(R.drawable.drawer_notif_icon));
        }else{
            retryBtn.setVisibility(View.VISIBLE);
            errorImg.setImageDrawable(getDrawable(R.drawable.error_icon));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

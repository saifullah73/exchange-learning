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
import android.widget.Toast;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.PostDetailActivity;
import com.company.exchange_learning.adapters.FollowerAdapter;
import com.company.exchange_learning.adapters.ProposalAdapter;
import com.company.exchange_learning.model.Notification;
import com.company.exchange_learning.model.Proposal;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ProposalListActivity extends AppCompatActivity {
    private static final String TAG = "MyProposalListActivity";
    private static ProposalListActivity instance;
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
    private long notificationLength;
    private long count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_list);
        instance = this;
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
        startRead();

    }

    public void startRead(){
        try {
            if (mode.equals("Post")) {
                getSupportActionBar().setTitle("Post Proposal");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setElevation(5);
                key = "exchangelearning";
                getData(mode,key,id);
            } else if(mode.equals("Book")) {
                getSupportActionBar().setTitle("Book Proposal");
                getSupportActionBar().setElevation(5);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                key = "bookcity";
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

    public static ProposalListActivity getInstance(){
        if (instance == null){
            instance = new ProposalListActivity();
        }
        return instance;
    }

    public void getData(final String mode,final String key, final String id){
        count = 0;
        notifications = new ArrayList<>();
        allProposals = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Notification_Table/Proposal").child(Constants.getConstantUid());
        Log.i(TAG,"Starting Read");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.i(TAG,"Found Children");
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Notification notif = dsp.getValue(Notification.class);
                        notif.setNotification_id(dsp.getKey());
                        Log.i(TAG,notif.toString());
                        notifications.add(notif);
                    }
                    Log.i(TAG,"More than 0 notifications ready for display");
                    Log.i(TAG,"unfiltered = "+ notifications.size());
                    List<Notification> temp = new ArrayList<>();
                    for (final Notification not : notifications) {
                        if (not.getPost_id().equals(id)) {
                            temp.add(not);
                        }
                    }
                    notifications = temp;
                    Log.i(TAG,"filtered = "+ notifications.size());
                    notificationLength = notifications.size();
                    for (final Notification not : notifications) {
                        if (not == null){
                            count+=1;
                            if (count == notificationLength && allProposals.size() == 0 ){
                                showErrorLayout("No Proposal Found",0);
                            }
                            continue;
                        }
                        final DatabaseReference myRef2 = database.getReference(mode + "_Proposal_Table").child(not.post_id).child(not.proposal_id);
                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.i(TAG,"Reading Proposal from " + myRef2.toString());
                                if (dataSnapshot.exists()) {
                                    Proposal proposal = dataSnapshot.getValue(Proposal.class);
                                    proposal.setNotif(not);
                                    if (!proposal.isReported()) {
                                        allProposals.add(proposal);
                                        allProposals = sortByDate(allProposals);
                                        Log.i(TAG,"added");
                                    }else{
                                        Log.i(TAG,"Not added");
                                    }
                                    Log.i(TAG,"All proposal size = "+ allProposals.size());
                                    Log.i(TAG,"Read a proposal");
                                    if (allProposals.size() <= 1){
                                        Log.i(TAG,"Setting data");
                                        setData(allProposals);
                                    }
                                    else{
                                        Log.i(TAG,"Notifying changes");
                                        notifyChanges();
                                    }
                                    count+=1;
                                    if (count == notificationLength && allProposals.size() == 0 ){
                                        showErrorLayout("No Proposal Found",0);
                                    }
                                }else{
                                    count+=1;
                                    Log.i(TAG,"No proposal found at this location");
                                    if (count == notificationLength && allProposals.size() == 0 ){
                                        showErrorLayout("No Proposal Found",0);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (count == notificationLength && allProposals.size() ==0){
                                    showErrorLayout("No Proposal Found",0);
                                }
                                Log.i(TAG,"Cancelled reading proposal" + databaseError.getMessage());
                            }
                        });
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
        count = 0;
        notifications = new ArrayList<>();
        allProposals = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Notification_Table/Proposal").child(Constants.getConstantUid());
        Log.i(TAG,"Starting Read");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.i(TAG,"Found Children");
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Notification notif = dsp.getValue(Notification.class);
                        notif.setNotification_id(dsp.getKey());
                        Log.i(TAG,notif.toString());
                        notifications.add(notif);
                    }
                    notificationLength = notifications.size();
                    Log.i(TAG,"More than 0 notifications ready for display");
                    for (final Notification not : notifications) {
                        if (not == null){
                            count+=1;
                            if (count == notificationLength && allProposals.size() == 0 ){
                                showErrorLayout("No Proposal Found",0);
                            }
                            continue;
                        }
                        final DatabaseReference myRef2;
                        if (not.getPlatform().equals("exchangelearning")){
                            myRef2 = database.getReference("Post_Proposal_Table").child(not.post_id).child(not.proposal_id);
                        }else{
                            myRef2 = database.getReference("Book_Proposal_Table").child(not.post_id).child(not.proposal_id);
                        }
                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.i(TAG,"Reading Proposal from " + myRef2.toString());
                                if (dataSnapshot.exists()) {
                                    Proposal proposal = dataSnapshot.getValue(Proposal.class);
                                    proposal.setNotif(not);
                                    Log.i(TAG,"Read a proposal");
                                    Log.i(TAG,"Reported : "+ proposal.isReported());
                                    if (!proposal.isReported()) {
                                        allProposals.add(proposal);
                                        allProposals = sortByDate(allProposals);
                                        Log.i(TAG,"added");
                                    }else{
                                        Log.i(TAG,"Not added");
                                    }
                                    Log.i(TAG,"All proposal size = "+ allProposals.size());
                                    if (allProposals.size() <= 1){
                                        Log.i(TAG,"Setting data");
                                        setData(allProposals);
                                    }
                                    else{
                                        Log.i(TAG,"Notifying changes");
                                        notifyChanges();
                                    }
                                    count+=1;
                                    if (count == notificationLength && allProposals.size() == 0 ){
                                        showErrorLayout("No Proposal Found",0);
                                    }
                                }else{
                                    count+=1;
                                    Log.i(TAG,"No proposal found at this location");
                                    if (count == notificationLength && allProposals.size() == 0){
                                        showErrorLayout("No Proposal Found",0);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (count == notificationLength & allProposals.size() == 0){
                                    showErrorLayout("No Proposal Found",0);
                                }
                                Log.i(TAG,"Cancelled reading proposal" + databaseError.getMessage());
                            }
                        });
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
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProposalAdapter(data,getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    public void notifyChanges(){
        if (adapter!= null) {
            adapter.notifyDataSetChanged();
        }
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

    private List<Proposal> sortByDate(List<Proposal> list){
        Collections.sort(list, new Comparator<Proposal>() {
            public int compare(Proposal o1, Proposal o2) {
                if (o1.getProposal_date() == null || o2.getProposal_date() == null)
                    return 0;
                LocalDateTime d11 = DateTimeUtils.getDateFromString(allProposals.get(0).getProposal_date());
                LocalDateTime d22 = DateTimeUtils.getDateFromString(allProposals.get(1).getProposal_date());
                Log.i("DATETEST", d11.toString());
                Log.i("DATETEST", d22.toString());
                Log.i("DATETEST","" + d22.compareTo(d11));
                Log.i("DATETEST","" + d11.compareTo(d22));
                return d11.compareTo(d22);
            }
        });
        return list;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}

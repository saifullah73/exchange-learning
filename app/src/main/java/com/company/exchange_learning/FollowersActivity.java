package com.company.exchange_learning;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.exchange_learning.adapters.FollowerAdapter;
import com.company.exchange_learning.model.BasicUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {
    private static final String TAG = "FollowersActivityyy";
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private LinearLayoutManager layoutManager;
    private FollowerAdapter adapter;
    private ImageView errorImg;
    private RelativeLayout errorView;
    private CardView retryBtn;
    private TextView errorMsg;
    private String userid = "";
    private int mode;
    private String key = "followers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recyclerView = findViewById(R.id.follower_recycler_view);
        loadingIndicatorView = findViewById(R.id.follower_avi);
        errorView = findViewById(R.id.followers_error);
        errorImg = findViewById(R.id.errImg);
        errorMsg = findViewById(R.id.errMsg);
        retryBtn = findViewById(R.id.retryBtn);
        loadingIndicatorView.findViewById(R.id.follower_avi);
        errorView.setVisibility(View.GONE);
        loadingIndicatorView.show();
        recyclerView.setVisibility(View.GONE);
        userid = getIntent().getStringExtra("uid");
        mode = getIntent().getIntExtra("mode",0);
        
        try {
            if (mode == 0) {
                getSupportActionBar().setTitle("Followers");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setElevation(5);
                key = "followers";
            } else {
                getSupportActionBar().setTitle("Following");
                getSupportActionBar().setElevation(5);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                key = "following";
            }
        }catch (Exception e){
        }
        getData(key);

    }

    public void getData(final String key){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Followers").child(userid).child(key);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List following = dataSnapshot.getValue(t);
                if (following == null) {
                    showErrorLayout("No " + key +" found",0);
                    following = new ArrayList();
                }
                setData(following);
                
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
        adapter = new FollowerAdapter(data,getApplicationContext());
        recyclerView.setAdapter(adapter);
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

    private void showErrorLayout(String msg, int mode) {
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
                getData(key);
            }
        });
        if (mode == 0){
            retryBtn.setVisibility(View.GONE);
            errorImg.setImageDrawable(getDrawable(R.drawable.user_icon));
        }else{
            retryBtn.setVisibility(View.VISIBLE);
            errorImg.setImageDrawable(getDrawable(R.drawable.error_icon));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

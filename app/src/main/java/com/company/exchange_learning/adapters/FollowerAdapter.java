package com.company.exchange_learning.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.FollowersActivity;
import com.company.exchange_learning.MainActivity;
import com.company.exchange_learning.Profile.ProfileActivity;
import com.company.exchange_learning.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.FollowerViewHolder> {
    private static final String TAG = "FollowerAdapter";
    private List<String> uids;
    private Context context;

    public class FollowerViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameview;
        private CircleImageView imageView;
        private ConstraintLayout layout;

        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameview = itemView.findViewById(R.id.followerList_username);
            imageView = itemView.findViewById(R.id.followerList_item_avatar);
            layout = itemView.findViewById(R.id.followerlist_constraint_layout);
        }
    }

    public FollowerAdapter(List<String> myDataset, Context context) {
        uids = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FollowerViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_list_item, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowerViewHolder holder, final int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(uids.get(position)).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.usernameview.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.usernameview.setText(uids.get(position));
            }
        });
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ uids.get(position));
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG,"Starting Load");
                String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .dontAnimate()
                        .placeholder(R.drawable.main_user_profile_avatar)
                        .into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG,"Error Loading Image");
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("uid", uids.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount() {
        return uids.size();
    }

}

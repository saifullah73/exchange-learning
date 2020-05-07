package com.company.exchange_learning.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.company.exchange_learning.Profile.ProfileActivity;
import com.company.exchange_learning.Proposals.ProposalListActivity;
import com.company.exchange_learning.Proposals.ViewProposalActivity;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProposalAdapter extends RecyclerView.Adapter<ProposalAdapter.ProposalViewHolder> {
    private static final String TAG = "ProposalAdapter";
    private List<Proposal> proposals;
    private Context context;

    public class ProposalViewHolder extends RecyclerView.ViewHolder{

        private TextView usernameview;
        private CircleImageView imageView;
        private ConstraintLayout layout;
        private ImageView typeView;
        private TextView dataView, dateView;

        public ProposalViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameview = itemView.findViewById(R.id.proposalList_username);
            imageView = itemView.findViewById(R.id.proposalList_item_avatar);
            layout = itemView.findViewById(R.id.proposallist_constraint_layout);
            typeView = itemView.findViewById(R.id.proposallist_image_view);
            dataView = itemView.findViewById(R.id.proposalList_text);
            dateView = itemView.findViewById(R.id.proposalList_date);
        }
    }

    public ProposalAdapter(List<Proposal> myDataset, Context context) {
        proposals = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProposalViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.proposal_item_layout, parent, false);
        return new ProposalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProposalViewHolder holder, final int position) {
        holder.dataView.setText(proposals.get(position).getProposal_data());
        holder.dateView.setText(proposals.get(position).getNotif().getCreated_at());
        if (proposals.get(position).getNotif().getPlatform().equals("bookcity")){
            holder.typeView.setImageDrawable(context.getDrawable(R.drawable.icons_book_24));
        }
        else{
            holder.typeView.setImageDrawable(context.getDrawable(R.drawable.icons_post_24));
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(proposals.get(position).getSubmitter_id()).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (proposals.get(position).getNotif().getRead_at().equals("")){
                    holder.usernameview.setTypeface(holder.usernameview.getTypeface(), Typeface.BOLD);
                    holder.dataView.setTextColor(context.getColor(R.color.black));
                }
                holder.usernameview.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (proposals.get(position).getNotif().getRead_at().equals("")){
                    holder.usernameview.setTypeface(holder.usernameview.getTypeface(), Typeface.BOLD);
                    holder.dataView.setTextColor(context.getColor(R.color.black));
                }
                holder.usernameview.setText(proposals.get(position).getSubmitter_id());
            }
        });
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/"+ proposals.get(position).getSubmitter_id());
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
                holder.usernameview.setTypeface(Typeface.DEFAULT);
                holder.dataView.setTextColor(context.getColor(R.color.darkGray));
                Intent i = new Intent(context, ViewProposalActivity.class);
                i.putExtra("proposal", proposals.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount() {
        return proposals.size();
    }

}

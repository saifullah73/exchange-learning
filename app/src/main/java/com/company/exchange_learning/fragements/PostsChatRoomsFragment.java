package com.company.exchange_learning.fragements;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.ChatActivity;
import com.company.exchange_learning.adapters.ChatRoomsAdapter;
import com.company.exchange_learning.listeners.OnChatRoomLongClickListener;
import com.company.exchange_learning.listeners.OnChatRoomMainLayoutClicked;
import com.company.exchange_learning.model.ChatRoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class PostsChatRoomsFragment extends Fragment implements OnChatRoomMainLayoutClicked, OnChatRoomLongClickListener {

    private AVLoadingIndicatorView avi;
    private ImageView chatIcon;
    private TextView emptyMsg;
    private RecyclerView recyclerView;

    private List<ChatRoomModel> mChatRooms;
    private ChatRoomsAdapter mAdapter;

    private DatabaseReference chatsRef;
    private StorageReference storageRef;
    private ValueEventListener eventListener;

    public PostsChatRoomsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_chat_rooms, container, false);
        avi = view.findViewById(R.id.postChatRoomAvi);
        chatIcon = view.findViewById(R.id.postChatRoomChatIcon);
        emptyMsg = view.findViewById(R.id.postChatRoomEmptyMsg);
        recyclerView = view.findViewById(R.id.postChatRoomRecyclerView);
        initRecyclerView();
        try {
            fetchPostsChatRooms();
        } catch (Exception e) {
        }
        return view;
    }

    private void fetchPostsChatRooms() {
        showProgress();
        mChatRooms.clear();
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Posts_Chats");
        eventListener = chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            String chatRoomId = dsp.getKey();
                            if (chatRoomId.contains(Constants.getConstantUid())) {
                                String lastMsg = "";
                                int unSeenCount = 0;
                                boolean isWelcomeMsg = false;
                                for (DataSnapshot dsp2 : dsp.getChildren()) {
                                    lastMsg = dsp2.child("message").getValue().toString();
                                    if (dsp2.child("status").getValue().toString().equalsIgnoreCase("unseen") && !dsp2.child("sender_id").getValue().toString().equalsIgnoreCase(Constants.getConstantUid())) {
                                        unSeenCount++;
                                    }
                                    isWelcomeMsg = dsp2.child("date").getValue().toString().equalsIgnoreCase("welcome");
                                }
                                int pos = mAdapter.getItemPosition(chatRoomId);
                                ChatRoomModel chatRoom;
                                if (pos == -1) {
                                    chatRoom = new ChatRoomModel(chatRoomId, null, null, isWelcomeMsg ? "Proposal Acceptance" : lastMsg, String.valueOf(unSeenCount));
                                    mChatRooms.add(chatRoom);
                                    fetchOtherUserDetails();
                                } else {
                                    chatRoom = mAdapter.getDataSet().get(pos);
                                    chatRoom.setUnseenMsgCount(String.valueOf(unSeenCount));
                                    chatRoom.setLastMsg(lastMsg);
                                    chatRoom.setUserName(chatRoom.getUserName());
                                    chatRoom.setUserImgUrl(chatRoom.getUserImgUrl());
                                    chatRoom.setChatRoomId(chatRoom.getChatRoomId());
                                    mAdapter.notifyItemChanged(pos);
                                }
                            }
                        }

                    } else {
                        hideProgress();
                        showEmptyMsg();
                    }
                } else {
                    hideProgress();
                    showEmptyMsg();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgress();
                showEmptyMsg();
            }
        });
    }


    private void fetchOtherUserDetails() {
        showProgress();
        chatsRef = FirebaseDatabase.getInstance().getReference("User_Information");
        final int[] userFetched = {0};
        String userId = "";
        for (final ChatRoomModel room : mChatRooms) {
            userId = room.getChatRoomId().replace(Constants.getConstantUid(), "");
            chatsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        room.setUserName(dataSnapshot.child("name").getValue().toString());
                        userFetched[0]++;
                        if (userFetched[0] == mChatRooms.size()) {
                            hideProgress();
                            hideEmptyMsg();
                            mAdapter.notifyDataSetChanged();
                            fetchOtherUserImage();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    hideProgress();
                    showEmptyMsg();
                }
            });
        }
    }

    private void fetchOtherUserImage() {
        storageRef = FirebaseStorage.getInstance().getReference();
        String userId = "";
        for (int i = 0; i < mChatRooms.size(); i++) {
            userId = mChatRooms.get(i).getChatRoomId().replace(Constants.getConstantUid(), "");
            final int finalI = i;
            storageRef.child("profileImages/" + userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null && !mChatRooms.isEmpty()) {
                        mChatRooms.get(finalI).setUserImgUrl(uri.toString());
                        mAdapter.notifyItemChanged(finalI);
                    }
                }
            });
        }
    }

    private void removeChatRoom(String id) {
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Posts_Chats");
        chatsRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Conversation deleted", Toast.LENGTH_LONG).show();
                    fetchPostsChatRooms();
                } else {
                    Toast.makeText(getContext(), "Could not remove conversation", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        mChatRooms.clear();
        chatsRef.removeEventListener(eventListener);
        super.onDestroyView();
    }

    public void showChatRoomDeleteConfirmation(final String id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setTitle("Confirm Deletion?");
        builder.setMessage("Are you sure you want to delete this conversation?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgress();
                removeChatRoom(id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDestroy() {
        mChatRooms.clear();
        storageRef = null;
        chatsRef = null;
        super.onDestroy();
    }


    private void initRecyclerView() {
        mChatRooms = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(mChatRooms, this, this);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void hideProgress() {
        avi.hide();
    }

    private void showEmptyMsg() {
        emptyMsg.setVisibility(View.VISIBLE);
        chatIcon.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyMsg() {
        emptyMsg.setVisibility(View.GONE);
        chatIcon.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        recyclerView.setVisibility(View.GONE);
        avi.show();
    }

    @Override
    public void initChat(ChatRoomModel room) {
        startActivity(new Intent(getContext(), ChatActivity.class).putExtra("chatRoom", room).putExtra("type", "post").putExtra("action", "chat"));
    }

    @Override
    public void deleteChatRoom(String id) {
        showChatRoomDeleteConfirmation(id);
    }
}

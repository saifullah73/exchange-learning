package com.company.exchange_learning.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.adapters.ChatMessageAdapter;
import com.company.exchange_learning.listeners.OnMsgLayoutLongClick;
import com.company.exchange_learning.model.ChatMessageModel;
import com.company.exchange_learning.model.ChatRoomModel;
import com.company.exchange_learning.model.Report;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.text.WordUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements OnMsgLayoutLongClick {

    Toolbar toolbar;
    TextView toolbarTxt;
    AVLoadingIndicatorView avi;

    RecyclerView recyclerView;
    List<ChatMessageModel> mMessages;
    ChatMessageAdapter mAdapter;

    AppCompatEditText msgEditText;
    ImageView msgSendBtn;

    ChatRoomModel chatRoom;
    String uID;
    String type;

    DatabaseReference chatRef;
    ChildEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();
        initRecyclerView();
        handleIntent();
    }

    private void subscribeToConversation(String uID, String type) {
        mMessages.clear();
        showProgress();
        if (type.equalsIgnoreCase("post")) {
            chatRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Posts_Chats").child(uID);
        } else if (type.equalsIgnoreCase("book")) {
            chatRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Books_Chats").child(uID);
        } else {
            chatRef = null;
        }
        if (chatRef != null) {
            eventListener = chatRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessageModel message = dataSnapshot.getValue(ChatMessageModel.class);
                    message.setMsgKey(dataSnapshot.getKey());
                    mMessages.add(message);
                    updateAdapter();
                    updateMessageStatus();
                    hideProgress();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessageModel message = dataSnapshot.getValue(ChatMessageModel.class);
                    message.setMsgKey(dataSnapshot.getKey());
                    int pos = mAdapter.getItemPosition(message);
                    mAdapter.getDataSet().get(pos).setMsgKey(message.getMsgKey());
                    mAdapter.getDataSet().get(pos).setDate(message.getDate());
                    mAdapter.getDataSet().get(pos).setMessage(message.getMessage());
                    mAdapter.getDataSet().get(pos).setSender_id(message.getSender_id());
                    mAdapter.getDataSet().get(pos).setStatus(message.getStatus());
                    mAdapter.notifyItemChanged(pos);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateMessageStatus() {
        if (!mMessages.isEmpty()) {
            ChatMessageModel msg = mMessages.get(mMessages.size() - 1);
            if (!msg.getSender_id().equalsIgnoreCase(Constants.getConstantUid())) {
                if (msg.getStatus().equalsIgnoreCase("unseen")) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("date", msg.getDate());
                    updates.put("sender_id", msg.getSender_id());
                    updates.put("status", "seen");
                    updates.put("message", msg.getMessage());
                    chatRef.child(msg.getMsgKey()).updateChildren(updates);
                }
            }
        }
    }

    private void initUI() {
        toolbar = findViewById(R.id.chatToolbar);
        toolbarTxt = findViewById(R.id.chat_user_name);
        msgEditText = findViewById(R.id.chatMsgEditText);
        msgSendBtn = findViewById(R.id.chatMsgSendBtn);
        avi = findViewById(R.id.chatAvi);
        msgSendBtn.setEnabled(false);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        msgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    msgSendBtn.setEnabled(true);
                } else {
                    msgSendBtn.setEnabled(false);
                }
            }
        });
    }

    private void handleIntent() {
        String action = getIntent().getStringExtra("action");
        if (action.equalsIgnoreCase("chat")) {
            chatRoom = (ChatRoomModel) getIntent().getSerializableExtra("chatRoom");
            uID = chatRoom.getChatRoomId();
            type = getIntent().getStringExtra("type");
            toolbarTxt.setText(WordUtils.capitalize(chatRoom.getUserName()));
            subscribeToConversation(uID, type);
        } else if (action.equalsIgnoreCase("welcome")) {
            type = getIntent().getStringExtra("type");
            uID = getIntent().getStringExtra("uID");
            initiateNewChat(uID, type);
        }
    }

    private void initiateNewChat(final String uId, final String type) {
        showProgress();
        chatRef = FirebaseDatabase.getInstance().getReference("User_Information").child(uId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = WordUtils.capitalize(dataSnapshot.child("name").getValue().toString());
                toolbarTxt.setText(name);
                String finalID = uId;
                if (finalID.compareTo(Constants.getConstantUid()) > 0) {
                    finalID = Constants.getConstantUid() + finalID;
                } else {
                    finalID = finalID + Constants.getConstantUid();
                }
                if (type.equalsIgnoreCase("post")) {
                    chatRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Posts_Chats").child(finalID);
                    ChatMessageModel welcomeMsg = new ChatMessageModel("welcome", name + "_" + Constants.getuName(), Constants.getConstantUid(), "unseen");
                    final String finalID1 = finalID;
                    chatRef.push().setValue(welcomeMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                subscribeToConversation(finalID1, type);
                            }
                        }
                    });
                } else {
                    chatRef = FirebaseDatabase.getInstance().getReference("Chats_Table").child("Books_Chats").child(finalID);
                    ChatMessageModel welcomeMsg = new ChatMessageModel("welcome", name + "_" + Constants.getuName(), Constants.getConstantUid(), "unseen");
                    final String finalID1 = finalID;
                    chatRef.push().setValue(welcomeMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                subscribeToConversation(finalID1, type);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMsg() {
        if (msgEditText.getText() != null) {
            if (msgEditText.getText().length() != 0) {
                String key = chatRef.push().getKey();
                ChatMessageModel message = new ChatMessageModel(getTimeDate(), msgEditText.getText().toString().trim(), Constants.getConstantUid(), "unseen");
                chatRef.child(key).setValue(message);
                msgEditText.getText().clear();
            }
        }
    }

    private void showProgress() {
        recyclerView.setVisibility(View.GONE);
        avi.show();
        msgSendBtn.setEnabled(false);
    }

    private void hideProgress() {
        recyclerView.setVisibility(View.VISIBLE);
        avi.hide();
        msgSendBtn.setEnabled(true);
    }

    private void updateAdapter() {
        if (mAdapter.getItemCount() != 0) {
            recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        chatRef.removeEventListener(eventListener);
        mMessages.clear();
        super.onDestroy();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.chatRecyclerView);
        mMessages = new ArrayList<>();
        mAdapter = new ChatMessageAdapter(mMessages, this);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        };

        mAdapter.registerAdapterDataObserver(observer);
        recyclerView.setLayoutManager(lm);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
    }

    @Override
    public void showMsgInfo(ChatMessageModel msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.chat_msg_info_dialog_layout, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.show();
        TextView dateTxt = view.findViewById(R.id.dateTxt);
        TextView statusTxt = view.findViewById(R.id.statusTxt);
        dateTxt.setText(formatDateString(msg.getDate()));
        statusTxt.setText(msg.getStatus().toUpperCase());
        TextView dismissBtn = view.findViewById(R.id.cancelBtn);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        CardView reportBtn = view.findViewById(R.id.reportMsgBtn);
        reportBtn.setVisibility(msg.getSender_id().equalsIgnoreCase(Constants.getConstantUid()) ? View.GONE : View.VISIBLE);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("post")) {
                    reportProposal(true, msg.getMsgKey());
                } else {
                    reportProposal(false, msg.getMsgKey());
                }
            }
        });
    }

    private void reportProposal(boolean isPost, String msgId) {
        String platform = isPost ? "exchangelearning" : "bookcity";
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.report_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = builder.show();
        TextView msg = view.findViewById(R.id.msg);
        msg.setText("Are you sure you want to report this message?");
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
                ref = database.getReference("Reports").child("message_report").child(platform);
                String userId = uID.replace(Constants.getConstantUid(), "");
                Report report = new Report(userId, Constants.getConstantUid(), msgId);
                ref.push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(ChatActivity.this, "Reported Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChatActivity.this, "Unexpected error while reporting", Toast.LENGTH_SHORT).show();
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


    private String formatDateString(String postDate) {
        try {
            LocalDateTime date1 = DateTimeUtils.getDateFromString(DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime()));
            LocalDateTime date2 = DateTimeUtils.getDateFromString(postDate);
            long difference = ChronoUnit.DAYS.between(date1.toLocalDate(), date2.toLocalDate());
            if (difference == 0) {
                return "Today at " + date2.toLocalTime();
            } else if (difference == -1) {
                return "Yesterday at " + date2.toLocalTime();
            } else {
                return postDate;
            }
        } catch (Exception e) {
            return postDate;
        }
    }

}

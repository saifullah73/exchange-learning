package com.company.exchange_learning.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.listeners.OnMsgLayoutLongClick;
import com.company.exchange_learning.model.ChatMessageModel;

import org.apache.commons.text.WordUtils;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ChatMessageModel> mMessages;
    OnMsgLayoutLongClick listener;

    public ChatMessageAdapter(List<ChatMessageModel> mMessages, OnMsgLayoutLongClick listener) {
        this.mMessages = mMessages;
        this.listener = listener;
    }

    public static final int SENT_MESSAGE_TYPE = 1;
    public static final int RECEIVED_MESSAGE_TYPE = 2;
    public static final int WELCOME_MESSAGE_TYPE = 3;

    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = mMessages.get(position);
        if (message.getSender_id().equalsIgnoreCase(Constants.getConstantUid()) && !message.getDate().equalsIgnoreCase("welcome")) {
            return SENT_MESSAGE_TYPE;
        } else if (!message.getSender_id().equalsIgnoreCase(Constants.getConstantUid()) && !message.getDate().equalsIgnoreCase("welcome")) {
            return RECEIVED_MESSAGE_TYPE;
        } else if (message.getDate().equalsIgnoreCase("welcome")) {
            return WELCOME_MESSAGE_TYPE;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENT_MESSAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent_message_layout, parent, false);
                return new SentMessageViewHolder(view);
            case RECEIVED_MESSAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received_msg_layout, parent, false);
                return new ReceivedMessageViewHolder(view);
            case WELCOME_MESSAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_welcome_msg_layout, parent, false);
                return new WelcomeMessageViewHolder(view);
        }
        return null;
    }

    public int getItemPosition(ChatMessageModel msg) {
        for (int i = 0; i < mMessages.size(); i++) {
            if (mMessages.get(i).getMsgKey().equalsIgnoreCase(msg.getMsgKey())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessageModel message = mMessages.get(position);
        switch (holder.getItemViewType()) {
            case SENT_MESSAGE_TYPE:
                handleSentMessage(holder, message);
                break;
            case RECEIVED_MESSAGE_TYPE:
                handleReceivedMessage(holder, message);
                break;
            case WELCOME_MESSAGE_TYPE:
                handleWelcomeMessage(holder, message);
                break;
        }
    }

    private void handleWelcomeMessage(ViewHolder holder, ChatMessageModel message) {
        String msg = "";
        String[] names = message.getMessage().split("_");
        if (message.getSender_id().equalsIgnoreCase(Constants.getConstantUid())) {
            msg = "You Accepted " + WordUtils.capitalize(names[0]) + "\'s proposal. Send a message to start conversation";
        } else {
            msg = WordUtils.capitalize(names[1]) + " accepted your proposal. Send a message to start conversation";
        }
        ((WelcomeMessageViewHolder) holder).mainWelcomeMsg.setText(msg);
    }

    private void handleSentMessage(ViewHolder holder, final ChatMessageModel message) {
        ((SentMessageViewHolder) holder).mainSentMsg.setText(message.getMessage());
        ((SentMessageViewHolder) holder).mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.showMsgInfo(message);
                return false;
            }
        });
    }

    private void handleReceivedMessage(ViewHolder holder, final ChatMessageModel message) {
        ((ReceivedMessageViewHolder) holder).mainReceivedMsg.setText(message.getMessage());
        ((ReceivedMessageViewHolder) holder).mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.showMsgInfo(message);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public List<ChatMessageModel> getDataSet() {
        return this.mMessages;
    }

    public void setDataSet(List<ChatMessageModel> msgs) {
        this.mMessages = msgs;
        notifyDataSetChanged();
    }


    public static class SentMessageViewHolder extends ViewHolder {

        private TextView mainSentMsg;
        private RelativeLayout mainLayout;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mainSentMsg = itemView.findViewById(R.id.sent_msg_text_body_textview);
            mainLayout = itemView.findViewById(R.id.chatSentMsgMainLayout);
        }
    }

    public static class ReceivedMessageViewHolder extends ViewHolder {

        private TextView mainReceivedMsg;
        private RelativeLayout mainLayout;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mainReceivedMsg = itemView.findViewById(R.id.received_msg_text_body_textview);
            mainLayout = itemView.findViewById(R.id.chatReceivedMsgLayout);
        }
    }

    public static class WelcomeMessageViewHolder extends ViewHolder {

        private TextView mainWelcomeMsg;

        public WelcomeMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mainWelcomeMsg = itemView.findViewById(R.id.welcome_msg_text_body_textview);
        }
    }
}

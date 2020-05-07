package com.company.exchange_learning.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.R;
import com.company.exchange_learning.listeners.OnChatRoomLongClickListener;
import com.company.exchange_learning.listeners.OnChatRoomMainLayoutClicked;
import com.company.exchange_learning.model.ChatRoomModel;
import com.google.firebase.database.DatabaseReference;

import org.apache.commons.text.WordUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomsViewHolder> {

    private List<ChatRoomModel> mChatRooms;
    private OnChatRoomMainLayoutClicked listener;
    private OnChatRoomLongClickListener longClickListener;

    private DatabaseReference chatRef;

    public ChatRoomsAdapter(List<ChatRoomModel> mChatRooms, OnChatRoomMainLayoutClicked listener, OnChatRoomLongClickListener longClickListener) {
        this.mChatRooms = mChatRooms;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ChatRoomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomsViewHolder holder, int position) {
        final ChatRoomModel chatRoom = mChatRooms.get(position);
        holder.userName.setText(WordUtils.capitalize(chatRoom.getUserName()));
        holder.lastMsg.setText(chatRoom.getLastMsg());
        if (!chatRoom.getUnseenMsgCount().equalsIgnoreCase("0") && chatRoom.getUnseenMsgCount() != null) {
            holder.unseenMsgs.setVisibility(View.VISIBLE);
            holder.unseenMsgs.setText(Integer.parseInt(chatRoom.getUnseenMsgCount()) > 99 ? "99+" : chatRoom.getUnseenMsgCount());
        } else {
            holder.unseenMsgs.setVisibility(View.GONE);
        }
        Glide.with(holder.itemView.getContext()).load(chatRoom.getUserImgUrl()).placeholder(R.drawable.main_user_profile_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userImg);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.initChat(chatRoom);
            }
        });
        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickListener.deleteChatRoom(chatRoom.getChatRoomId());
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    public void setDataSet(List<ChatRoomModel> rooms) {
        this.mChatRooms = rooms;
        notifyDataSetChanged();
    }

    public int getItemPosition(String id) {
        for (int i = 0; i < mChatRooms.size(); i++) {
            if (mChatRooms.get(i).getChatRoomId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<ChatRoomModel> getDataSet() {
        return mChatRooms;
    }

    public static class ChatRoomsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView userName, lastMsg, unseenMsgs;
        CardView mainLayout;

        public ChatRoomsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.chatRoomUserImg);
            userName = itemView.findViewById(R.id.chatRoomUserName);
            lastMsg = itemView.findViewById(R.id.chatRoomLastMsg);
            unseenMsgs = itemView.findViewById(R.id.chatRoomUnseenMsgCounter);
            mainLayout = itemView.findViewById(R.id.chatRoomLayout);
        }
    }
}

package com.company.exchange_learning.model;

import java.io.Serializable;

public class ChatRoomModel implements Serializable {
    private String chatRoomId;
    private String userImgUrl;
    private String userName;
    private String lastMsg;
    private String unseenMsgCount;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, String userImgUrl, String userName, String lastMsg, String unseenMsgCount) {
        this.chatRoomId = chatRoomId;
        this.userImgUrl = userImgUrl;
        this.userName = userName;
        this.lastMsg = lastMsg;
        this.unseenMsgCount = unseenMsgCount;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getUnseenMsgCount() {
        return unseenMsgCount;
    }

    public void setUnseenMsgCount(String unseenMsgCount) {
        this.unseenMsgCount = unseenMsgCount;
    }
}

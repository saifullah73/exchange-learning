package com.company.exchange_learning;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    public static String uid = "";
    public static String uName = "";
    public static String uCommunity = "";

    public static String getConstantUid() {
        if (uid == null || uid.equals("")) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }else{
                uid = "";
            }
        }
        return uid;
    }

    public static String getuName() {
        if (uName == null || uName.equals("")){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                uName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            }else{
                uName = "";
            }
        }
        return uName;
    }

    public static String getuCommunity() {
        return uCommunity;
    }
}

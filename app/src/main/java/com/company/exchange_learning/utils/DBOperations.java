package com.company.exchange_learning.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageMetadata;

//import com.google.android.gms.auth.api.signin.internal.Storage;
//import com.google.cloud.storage.Acl;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import com.google.firebase.storage.StorageMetadata;

public class DBOperations {

    public static StorageMetadata getmetaData() {
//         String projectId = "exchange-learning-2c934";
//         String bucketName = "your-bucket-name";
//         String objectName = "your-object-name";
//        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//        BlobId blobId = BlobId.of(bucketName, objectName);
//        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
//        Log.i("DBOPERATIONS","Object " + objectName + " in bucket " + bucketName + " was made publicly readable");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentLanguage("en")
                .setContentType("image/jpeg")
                .setCacheControl("no-cache")
                .build();
        return metadata;
    }
}

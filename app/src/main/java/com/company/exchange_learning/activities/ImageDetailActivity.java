package com.company.exchange_learning.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imgView;
    FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        imgView = findViewById(R.id.imageView);
        save = findViewById(R.id.save);
        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra("imageUrl");
        Glide.with(getApplicationContext()).load(imgUrl).placeholder(R.drawable.main_post_image_avatart).into(imgView);

        imgView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if (save.getVisibility() == View.VISIBLE) {
                    save.setVisibility(View.INVISIBLE);
                } else {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                saveImage();
                save.setVisibility(View.GONE);
            }
        });
    }

    private void saveImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            } else {
                imgView.buildDrawingCache();
                Bitmap bmp = imgView.getDrawingCache();
                File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String filename = "Exchange-learning-" + LocalDateTime.now();
                File file = new File(storageLoc, filename + ".jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    scanFile(getApplicationContext(), Uri.fromFile(file));
                    Toast.makeText(getApplicationContext(), "Image saved to gallery", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Could not save image to gallery", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            imgView.buildDrawingCache();
            Bitmap bmp = imgView.getDrawingCache();
            File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String filename = "Exchange-learning-" + LocalDateTime.now();
            File file = new File(storageLoc, filename + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                scanFile(getApplicationContext(), Uri.fromFile(file));
                Toast.makeText(getApplicationContext(), "Image saved to gallery", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
    }


}

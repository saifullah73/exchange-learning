package com.company.exchange_learning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
                    save.setVisibility(View.GONE);
                } else {
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
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
                    save.setVisibility(View.GONE);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error saving image to gallery", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
    }


}

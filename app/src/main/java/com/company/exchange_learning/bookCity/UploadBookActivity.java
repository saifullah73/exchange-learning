package com.company.exchange_learning.bookCity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.Book;
import com.company.exchange_learning.model.PostModel;
import com.company.exchange_learning.utils.DBOperations;
import com.company.exchange_learning.utils.DateTimeUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadBookActivity extends AppCompatActivity {

    public static final int CHOOSE_FROM_GALLERY = 99;

    private Toolbar toolbar;

    private TextView bookTxt, cityTxt, bookcreateBtnText, tagCommsTxt;
    private ImageView mainImage, removeMainImgBtn;
    private EditText uploadBookTitle,uploadBookDesc,uploadBookAddr,uploadBookPrice;

    private Spinner typeSpinner;
    private AVLoadingIndicatorView avi;
    private CardView multiSpinner;

    private ArrayList<MultiSelectModel> multiArrayList;
    private MultiSelectDialog multiSelectDialog;

    private List<String> mSelectedCommunities;
    private List<Integer> mSelectedCommunitiesIDs;


    private boolean isActionNewBook = true;
    private boolean isCommunitiesChangedOnUpdate = false;
    private boolean isImageLoaded = false;

    DatabaseReference postRef;
    StorageReference storageRef;

    private Uri uri;
    private Book book;


    private LinearLayout uploadImgLayout;
    CardView uploadBookButton;
    private boolean isImageChangedForUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_book_layout);
        initUI();
        handleIntent();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bookTxt = findViewById(R.id.exchange_txt);
        cityTxt = findViewById(R.id.learning_txt);
        cityTxt.setVisibility(View.GONE);
        avi = findViewById(R.id.uploadBookBtnProgress);
        mSelectedCommunities = new ArrayList<>();
        mSelectedCommunitiesIDs = new ArrayList<>();

        uploadBookTitle = findViewById(R.id.et_UploadBookTitle);
        uploadBookAddr = findViewById(R.id.et_UploadBookAddress);
        uploadBookDesc = findViewById(R.id.et_UploadBookDesc);
        uploadBookPrice = findViewById(R.id.et_UploadBookPrice);
        mainImage = findViewById(R.id.createBookImagecover);
        removeMainImgBtn = findViewById(R.id.mainImgRemoveBtn);
        uploadImgLayout = findViewById(R.id.uploadImgLayout);

        removeMainImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMainImage();
            }
        });

        uploadImgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageToUpload();
            }
        });

        typeSpinner = findViewById(R.id.createBookTypeSpinner);
        multiSpinner = findViewById(R.id.CreateBookMultiLayout);
        tagCommsTxt = findViewById(R.id.createBooktagCommTxtView);
        multiSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMultiSpinner();
            }
        });


        uploadBookButton = findViewById(R.id.uploadBookBtn);
        uploadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareOrUpdatePost();
            }
        });
        bookcreateBtnText = findViewById(R.id.createBookBtnTxt);

        populateTypeSpinner();
    }

    private void initMultiSpinner() {
        if (multiArrayList == null) {
            multiArrayList = new ArrayList<>();
            multiArrayList.add(new MultiSelectModel(0, "Architecture"));
            multiArrayList.add(new MultiSelectModel(1, "Bio Sciences"));
            multiArrayList.add(new MultiSelectModel(2, "Chemical Engineering"));
            multiArrayList.add(new MultiSelectModel(3, "Chemistry"));
            multiArrayList.add(new MultiSelectModel(4, "Civil Engineering"));
            multiArrayList.add(new MultiSelectModel(5, "Computer Science"));
            multiArrayList.add(new MultiSelectModel(6, "Department of Biotechnology"));
            multiArrayList.add(new MultiSelectModel(7, "Development Studies"));
            multiArrayList.add(new MultiSelectModel(8, "Earth Sciences"));
            multiArrayList.add(new MultiSelectModel(9, "Economics"));
            multiArrayList.add(new MultiSelectModel(10, "Electrical and Computer Engineering"));
            multiArrayList.add(new MultiSelectModel(11, "Environmental Sciences"));
            multiArrayList.add(new MultiSelectModel(12, "Health Informatics"));
            multiArrayList.add(new MultiSelectModel(13, "Humanities"));
            multiArrayList.add(new MultiSelectModel(14, "Management Sciences"));
            multiArrayList.add(new MultiSelectModel(15, "Mathematics"));
            multiArrayList.add(new MultiSelectModel(16, "Mechanical Engineering"));
            multiArrayList.add(new MultiSelectModel(17, "Meteorology"));
            multiArrayList.add(new MultiSelectModel(18, "Pharmacy"));
            multiArrayList.add(new MultiSelectModel(19, "Physics"));
            multiArrayList.add(new MultiSelectModel(20, "Statistics"));
        }

        multiSelectDialog = new MultiSelectDialog().title("Tag Communities").titleSize(30).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1).setMaxSelectionLimit(21).multiSelectList(multiArrayList)
                .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                    @Override
                    public void onSelected(ArrayList<Integer> arrayList, ArrayList<String> arrayList1, String s) {
                        mSelectedCommunities.clear();
                        mSelectedCommunities.addAll(arrayList1);
                        if (mSelectedCommunities.size() > 0) {
                            tagCommsTxt.setText(mSelectedCommunities.size() + " communities tagged");
                        } else {
                            tagCommsTxt.setText("Tag Communities");
                        }
                        if (!isActionNewBook) {
                            isCommunitiesChangedOnUpdate = true;
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });
        multiSelectDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.MultiSelectDialogStyle);
        multiSelectDialog.preSelectIDsList((ArrayList<Integer>) mSelectedCommunitiesIDs);
        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
    }

    private void shareOrUpdatePost() {
        final String book_title = uploadBookTitle.getText().toString().trim();
        final String book_addr = uploadBookAddr.getText().toString().trim();
        final String book_desc = uploadBookDesc.getText().toString().trim();
        final String book_price = uploadBookPrice.getText().toString().trim().replaceFirst("^0+(?!$)", "");
        final String book_type = typeSpinner.getSelectedItem().toString();
        try{
        if ((book_type.equals("@Sell")) && !book_price.equals("") && Integer.valueOf(book_price) == 0){
            Toast.makeText(this, "Please enter a price other than 0", Toast.LENGTH_LONG).show();
            return;
        }}catch (Exception e){

        }
        if (book_title.length() != 0 && book_addr.length() != 0 && book_desc.length() != 0 && book_price.length() != 0 && book_type.length()!= 0) {
            if (isImageLoaded && uri != null) {
                if (isActionNewBook) {
                    if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                        if (book_title.length() > 4 && book_desc.length() > 4 && book_price.length() > 0 && book_addr.length() > 4) {
                            showProgress();
                            storageRef = FirebaseStorage.getInstance().getReference().child("book-images/").child(Constants.getConstantUid() + "/" + Constants.getConstantUid() + "_image" + DateTimeUtils.getCurrentDateTime());
                            storageRef.putFile(uri, DBOperations.getmetaData()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    return storageRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downUri = task.getResult();
                                        book = new Book(new ArrayList<String>());
                                        book.setCover_photo(downUri.toString());
                                        book.setBook_title(book_title);
                                        book.setUser_id(Constants.getConstantUid());
                                        book.setBook_address(book_addr);
                                        book.setBook_description(book_desc);
                                        if (Integer.valueOf(book_price) != 0) {
                                            book.setBook_price(book_price);
                                        }
                                        book.getTagged_communities().addAll(mSelectedCommunities);
                                        book.setBook_type(book_type);

                                        postRef = FirebaseDatabase.getInstance().getReference("Books_City");
                                        postRef.push().setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    hideProgress();
                                                    Toast.makeText(getApplicationContext(), "Book uploaded Successfully", Toast.LENGTH_LONG).show();
                                                    goToBookMainActivity();
                                                } else {
                                                    hideProgress();
                                                    Toast.makeText(getApplicationContext(), "Error uploading the post", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        hideProgress();
                                        Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(this, "fields should be atleast 5 character long and price must be greater than 10", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    } else {
                        Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (book_title.equalsIgnoreCase(book.getBook_title()) && book_type.equalsIgnoreCase(book.getBook_type())
                            && book_addr.equalsIgnoreCase(book.getBook_address())
                            && book_desc.equalsIgnoreCase(book.getBook_description())
                            && book_price.equalsIgnoreCase(book.getBook_price())
                            && !isImageChangedForUpdate && !isCommunitiesChangedOnUpdate) {
                        Toast.makeText(this, "No changes detected", Toast.LENGTH_LONG).show();
                    } else {
                        if (isImageChangedForUpdate) {
                            if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                                if (book_title.length() > 4 && book_addr.length() > 4 && book_desc.length() > 4 && book_price.length() > 0) {
                                    showProgress();
                                    storageRef = FirebaseStorage.getInstance().getReference().child("book-images/").child(Constants.getConstantUid() + "/" + Constants.getConstantUid() + "_image" + DateTimeUtils.getCurrentDateTime());
                                    storageRef.putFile(uri,DBOperations.getmetaData()).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                            return storageRef.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                final Uri downUri = task.getResult();
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(book.getCover_photo());
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        postRef = FirebaseDatabase.getInstance().getReference("Books_City").child(book.getBook_id());
                                                        Map<String, Object> updates = new HashMap<>();
                                                        updates.put("book_title", book_title);
                                                        updates.put("cover_photo", downUri.toString());
                                                        updates.put("book_type", book_type);
                                                        updates.put("book_description", book_desc);
                                                        if (Integer.valueOf(book_price) != 0) {
                                                            book.setBook_price(book_price);
                                                        }
                                                        updates.put("book_address", book_addr);
                                                        updates.put("user_id", book.getUser_id());
                                                        updates.put("tagged_communities", mSelectedCommunities);

                                                        postRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                                if (databaseError == null) {
                                                                    hideProgress();
                                                                    Toast.makeText(getApplicationContext(), "Book updated Successfully", Toast.LENGTH_LONG).show();
                                                                    goToBookMainActivity();
                                                                } else {
                                                                    hideProgress();
                                                                    Toast.makeText(getApplicationContext(), "Error updating the book", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        hideProgress();
                                                        Toast.makeText(getApplicationContext(), "Error updating the book", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            } else {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Error while uploading image: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "fields should be atleast 5 character long and price must be greater than 10", Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                            } else {
                                Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (mSelectedCommunities != null && mSelectedCommunities.size() != 0) {
                                if (book_title.length() > 4 && book_desc.length() > 4 && book_price.length() > 0 && book_addr.length() > 4) {
                                    postRef = FirebaseDatabase.getInstance().getReference("Books_City").child(book.getBook_id());
                                    showProgress();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("book_title", book_title);
                                    updates.put("book_type", book_type);
                                    updates.put("book_description", book_desc);
                                    if (Integer.parseInt(book_price) != 0) {
                                        updates.put("book_price", book_price);
                                    }
                                    updates.put("book_address", book_addr);
                                    updates.put("user_id", book.getUser_id());
                                    updates.put("tagged_communities", mSelectedCommunities);

                                    postRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Book updated Successfully", Toast.LENGTH_LONG).show();
                                                goToBookMainActivity();
                                            } else {
                                                hideProgress();
                                                Toast.makeText(getApplicationContext(), "Error updating the book", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "fields should be atleast 5 character long and price must be greater than 10", Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                            } else {
                                Toast.makeText(this, "Please tag at least 1 community", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else {
                hideProgress();
                Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
            }
        } else {
            hideProgress();
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            loadImageIntoView(uri);
            if (!isActionNewBook) {
                isImageChangedForUpdate = true;
            }
        }
    }

    private void chooseImageToUpload() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } else {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY);
            }
        } else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY);
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
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        chooseImageToUpload();
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showProgress() {
        avi.show();
        bookcreateBtnText.setText("");
        uploadBookButton.setEnabled(false);
    }

    private void hideProgress() {
        bookcreateBtnText.setText(isActionNewBook ? "UPLOAD BOOK" : "UPDATE BOOK");
        uploadBookButton.setEnabled(true);
        avi.hide();
    }

    private void goToBookMainActivity() {
        startActivity(new Intent(UploadBookActivity.this, BookCityMain.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private String getTimeDate() {
        return DateTimeUtils.getStringFromDate(DateTimeUtils.getCurrentDateTime());
    }

    private int getCommunityID(String comm) {
        if (multiArrayList == null) {
            multiArrayList = new ArrayList<>();
            multiArrayList.add(new MultiSelectModel(0, "Architecture"));
            multiArrayList.add(new MultiSelectModel(1, "Bio Sciences"));
            multiArrayList.add(new MultiSelectModel(2, "Chemical Engineering"));
            multiArrayList.add(new MultiSelectModel(3, "Chemistry"));
            multiArrayList.add(new MultiSelectModel(4, "Civil Engineering"));
            multiArrayList.add(new MultiSelectModel(5, "Computer Science"));
            multiArrayList.add(new MultiSelectModel(6, "Department of Biotechnology"));
            multiArrayList.add(new MultiSelectModel(7, "Development Studies"));
            multiArrayList.add(new MultiSelectModel(8, "Earth Sciences"));
            multiArrayList.add(new MultiSelectModel(9, "Economics"));
            multiArrayList.add(new MultiSelectModel(10, "Electrical and Computer Engineering"));
            multiArrayList.add(new MultiSelectModel(11, "Environmental Sciences"));
            multiArrayList.add(new MultiSelectModel(12, "Health Informatics"));
            multiArrayList.add(new MultiSelectModel(13, "Humanities"));
            multiArrayList.add(new MultiSelectModel(14, "Management Sciences"));
            multiArrayList.add(new MultiSelectModel(15, "Mathematics"));
            multiArrayList.add(new MultiSelectModel(16, "Mechanical Engineering"));
            multiArrayList.add(new MultiSelectModel(17, "Meteorology"));
            multiArrayList.add(new MultiSelectModel(18, "Pharmacy"));
            multiArrayList.add(new MultiSelectModel(19, "Physics"));
            multiArrayList.add(new MultiSelectModel(20, "Statistics"));
        }
        for (int i = 0; i < multiArrayList.size(); i++) {
            if (multiArrayList.get(i).getName().toLowerCase().equalsIgnoreCase(comm.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    private void removeMainImage() {
        isImageLoaded = false;
        isImageChangedForUpdate = false;
        uri = null;
        mainImage.setVisibility(View.GONE);
        removeMainImgBtn.setVisibility(View.GONE);
        uploadImgLayout.setVisibility(View.VISIBLE);
    }

    private void loadImageIntoView(Uri mUri) {
        isImageLoaded = true;
        uri = mUri;
        Glide.with(getApplicationContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).into(mainImage);
        mainImage.setVisibility(View.VISIBLE);
        removeMainImgBtn.setVisibility(View.VISIBLE);
        uploadImgLayout.setVisibility(View.GONE);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent.getStringExtra("actionType").equalsIgnoreCase("editBook")) {
            isActionNewBook = false;
            bookcreateBtnText.setText("UPDATE BOOK");
            bookTxt.setText("Edit Book");
            book = (Book) intent.getSerializableExtra("book");
            uploadBookTitle.setText(book.getBook_title());
            uploadBookPrice.setText(book.getBook_price());
            uploadBookDesc.setText(book.getBook_description());
            uploadBookAddr.setText(book.getBook_address());
            if (book.getTagged_communities() != null && book.getTagged_communities().size() != 0) {
                tagCommsTxt.setText(book.getTagged_communities().size() + " communities tagged");
                for (int i = 0; i < book.getTagged_communities().size(); i++) {
                    int id = getCommunityID(book.getTagged_communities().get(i));
                    if (id != -1) {
                        mSelectedCommunities.add(multiArrayList.get(id).getName());
                        mSelectedCommunitiesIDs.add(id);
                    }
                }
            }
            loadImageIntoView(Uri.parse(book.getCover_photo()));
            if (book.getBook_type().equalsIgnoreCase("@Exchange")) {
                typeSpinner.setSelection(0);
            } else if (book.getBook_type().equalsIgnoreCase("@Sell")) {
                typeSpinner.setSelection(1);
            } else {
                typeSpinner.setSelection(2);
            }
        } else {
            isActionNewBook = true;
            removeMainImage();
            bookTxt.setText("UPLOAD BOOK");
        }
    }

    @Override
    protected void onDestroy() {
        multiArrayList = null;
        mSelectedCommunitiesIDs = null;
        mSelectedCommunities = null;
        super.onDestroy();
    }

    private void handleCancellation() {
        if (isActionNewBook) {
            if (uploadBookTitle.getText().toString().length() != 0 || uploadBookDesc.getText().toString().length() != 0 || isImageLoaded) {
                showConfirmationDialog();
            } else {
                finish();
            }
        } else {
            if (!uploadBookTitle.getText().toString().equalsIgnoreCase(book.getBook_title())  || !uploadBookDesc.getText().toString().equalsIgnoreCase(book.getBook_description()) || isImageChangedForUpdate) {
                showConfirmationDialog();
            } else {
                finish();
            }
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard everything?");
        builder.setMessage("Are you sure to exit the editor?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void populateTypeSpinner() {
        String[] spinnerArray = {"@Exchange", "@Sell", "@Donate"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("@Donate") || selectedItem.equals("@Exchange"))
                {
                    uploadBookPrice.setText("0");
                    uploadBookPrice.setEnabled(false);
                }else{
                    uploadBookPrice.setText("");
                    uploadBookPrice.setEnabled(true );
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                uploadBookPrice.setText(0);
                uploadBookPrice.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        handleCancellation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleCancellation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


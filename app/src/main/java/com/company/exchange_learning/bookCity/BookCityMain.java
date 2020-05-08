package com.company.exchange_learning.bookCity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.SearchActivity;
import com.company.exchange_learning.adapters.BooksAdapter;
import com.company.exchange_learning.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookCityMain extends AppCompatActivity {
    private static final String TAG = "BookCityMainA";
    private Toolbar toolbar;
    private LinearLayout uploadBtn, myBooksBtn;
    private TextView toolbarExhangeTxt,toolbarLearningTxt;
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loader;
    private List<Book> books, myBooks;
    private BooksAdapter mAdapter;
    private DatabaseReference bookDataRef;
    private LinearLayout emptyMsgLayout;
    private TextView uploadLink;
    private boolean ifShowingMyBooks = false;
    private CardView mainHeader;
    private boolean registeredtoolbarListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookcity_content_main);
        initUI();
        initRecylerView();
        subscribeToBooks();
    }

    private void initUI(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarExhangeTxt = findViewById(R.id.exchange_txt);
        toolbarLearningTxt = findViewById(R.id.learning_txt);
        recyclerView = findViewById(R.id.bookcity_recylerview);
        uploadBtn = findViewById(R.id.bookcity_main_upload_book);
        uploadLink = findViewById(R.id.emptyMsgUploadBook);
        mainHeader = findViewById(R.id.main_header);
        myBooksBtn = findViewById(R.id.bookcity_main_go_to_my_book);
        emptyMsgLayout = findViewById(R.id.empty_msg);
        loader = findViewById(R.id.avi);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BookCityMain.this,UploadBookActivity.class);
                i.putExtra("actionType","uploadBook");
                startActivity(i);
            }
        });
        uploadLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BookCityMain.this,UploadBookActivity.class);
                i.putExtra("actionType","uploadBook");
                startActivity(i);
            }
        });
        myBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMyBooks(true);
            }
        });

    }

    private void initRecylerView(){
        books = new ArrayList<>();
        myBooks = new ArrayList<>();
        mAdapter = new BooksAdapter(books, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }



    private void subscribeToBooks() {
        books.clear();
        myBooks.clear();
        showProgressBar();
        bookDataRef = FirebaseDatabase.getInstance().getReference("Books_City");
        bookDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChildren()) {
                        long totalBooks = dataSnapshot.getChildrenCount();
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Book book = dsp.getValue(Book.class);
                            book.setBook_id(dsp.getKey());
                            books.add(book);
                            if (book.getUser_id().equalsIgnoreCase(Constants.getConstantUid())) {
                                myBooks.add(book);
                            }
                            if (books.size() == totalBooks) {
                                prepareRecyclerView(false);
                            }
                        }
                    } else {
                        prepareRecyclerView(false);
                    }
                } else {
                    prepareRecyclerView(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error While Loading Books", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleMyBooks(boolean show) {
        if (show) {
            toolbarExhangeTxt.setText("My Books");
            toolbarLearningTxt.setVisibility(View.GONE);
            ifShowingMyBooks = true;
            mainHeader.setVisibility(View.GONE);
            enableViews(true);
            prepareRecyclerView(true);
        } else {
            toolbarExhangeTxt.setText("BOOK");
            toolbarLearningTxt.setVisibility(View.VISIBLE);
            ifShowingMyBooks = false;
            mainHeader.setVisibility(View.VISIBLE);
            enableViews(false);
            prepareRecyclerView(false);
        }
    }

    private void enableViews(boolean enable) {
        if (enable) {
            if (!registeredtoolbarListener) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleMyBooks(false);
                    }
                });
            }
            registeredtoolbarListener = true;
        } else {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            registeredtoolbarListener = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (ifShowingMyBooks) {
            toggleMyBooks(false);
        } else {
            super.onBackPressed();
        }
    }

    private void prepareRecyclerView(boolean showOnlyUserBooks){
        Log.i(TAG,"Size = "+ books.size());
        Log.i(TAG,"My Books Size = "+ myBooks.size());
        hideProgressBar();
        if (!showOnlyUserBooks) {
            if (books.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyMsgLayout.setVisibility(View.VISIBLE);
            } else {
                if (!books.get(0).getBook_title().equals("NoBook")){
                    Book book = new Book();
                    book.setBook_title("NoBook");
                    books.add(0, book);
                }
                Log.i(TAG,"Setting Book Data");
                recyclerView.setVisibility(View.VISIBLE);
                emptyMsgLayout.setVisibility(View.GONE);
                mAdapter.setDataSet(books);
                mAdapter.notifyDataSetChanged();
            }
        }else{
            if (myBooks.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyMsgLayout.setVisibility(View.VISIBLE);
            } else {
                if (!myBooks.get(0).getBook_title().equals("NoBook")){
                    Book book = new Book();
                    book.setBook_title("NoBook");
                    myBooks.add(0, book);
                }
                Log.i(TAG,"Setting MyBook Data");
                recyclerView.setVisibility(View.VISIBLE);
                emptyMsgLayout.setVisibility(View.GONE);
                mAdapter.setDataSet(myBooks);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                gotoSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoSearchActivity() {
        startActivity(new Intent(BookCityMain.this, SearchActivity.class).putExtra("type", "book").putExtra("books", (Serializable) books));
    }

    private void showProgressBar() {
        recyclerView.setVisibility(View.GONE);
        emptyMsgLayout.setVisibility(View.GONE);
        loader.show();
    }

    private void hideProgressBar() {
        loader.hide();
    }
}

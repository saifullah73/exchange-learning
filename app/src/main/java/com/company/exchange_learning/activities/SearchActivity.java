package com.company.exchange_learning.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.exchange_learning.Profile.ProfileActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.adapters.BooksAdapter;
import com.company.exchange_learning.adapters.PostsAdapter;
import com.company.exchange_learning.listeners.OnPostClickListener;
import com.company.exchange_learning.listeners.OnPostUserImageClickListener;
import com.company.exchange_learning.model.Book;
import com.company.exchange_learning.model.PostModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnPostClickListener, OnPostUserImageClickListener {

    private Toolbar toolbar;
    private Spinner typeSpinner, criteriaSpinner;
    private RecyclerView recyclerView;
    private TextView toolbarTitle, emptyMsg;
    private EditText searchEditTxt;
    private ImageView emptyIcon;
    private AVLoadingIndicatorView searchProgress;

    private List<Book> mBooks;
    private List<PostModel> mPosts;
    private List<Book> mTempBooks;
    private List<PostModel> mTempPosts;

    private PostsAdapter mPostAdapter;
    private BooksAdapter mBookAdapter;

    private String searchType, selectedType, selectedCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
        handleIntent();
    }

    private void handleIntent() {
        try {
            searchType = getIntent().getStringExtra("type");
            if (searchType.equalsIgnoreCase("post")) {
                mPosts = (List<PostModel>) getIntent().getSerializableExtra("posts");
                toolbarTitle.setText("Search Posts");
                searchEditTxt.setHint("Search Posts");
            } else {
                mBooks = (List<Book>) getIntent().getSerializableExtra("books");
                searchEditTxt.setHint("Search Books");
                toolbarTitle.setText("Search Books");
            }
            populatetypeSpinner(searchType);
            populateCriteriaSpinner(searchType);
            initRecyclerView(searchType);
        } catch (Exception e) {
        }
    }

    private void initRecyclerView(String type) {
        if (type.equalsIgnoreCase("post")) {
            if (!mPosts.isEmpty()) {
                hideEmptyMsg();
                mPostAdapter = new PostsAdapter(mPosts, this, this, this);
                LinearLayoutManager lm = new LinearLayoutManager(this);
                lm.setReverseLayout(true);
                lm.setStackFromEnd(true);
                recyclerView.setLayoutManager(lm);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mPostAdapter);
                mPostAdapter.notifyDataSetChanged();
            } else {
                showEmptyMsg();
            }
        } else {
            if (!mBooks.isEmpty()) {
                hideEmptyMsg();
                mBookAdapter = new BooksAdapter(mBooks, this);
                LinearLayoutManager lm = new LinearLayoutManager(this);
                lm.setReverseLayout(true);
                lm.setStackFromEnd(true);
                recyclerView.setLayoutManager(lm);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mBookAdapter);
                mBookAdapter.notifyDataSetChanged();
            } else {
                showEmptyMsg();
            }
        }
    }

    private void initSearch(String charSequence) {
        if (charSequence.length() != 0) {
            showProgress();
            performSearch(charSequence);
        } else {
            resetSearch();
        }
    }

    private void performSearch(CharSequence s) {
        selectedType = typeSpinner.getSelectedItem().toString();
        selectedCriteria = criteriaSpinner.getSelectedItem().toString();
        try {
            if (searchType.equalsIgnoreCase("post")) {
                performPostsSearch(s.toString());
            } else {
                performBooksSearch(s.toString());
            }
        } catch (Exception e) {
        }
    }

    private void performPostsSearch(String s) {
        mTempPosts.clear();
        if (selectedType.equalsIgnoreCase("@Exchange")) {
            if (selectedCriteria.equalsIgnoreCase("Post Title")) {
                for (PostModel post : mPosts) {
                    if (post.getPost_title() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= post.getPost_title().length()) {
                            if (post.getPost_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_title().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Body")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for body");
                for (PostModel post : mPosts) {
                    if (post.getPost_body() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= post.getPost_body().length()) {
                            if (post.getPost_body().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_body().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Image Info")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for info");
                for (PostModel post : mPosts) {
                    if (post.getPost_image_info() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= post.getPost_image_info().length()) {
                            if (post.getPost_image_info().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_image_info().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            }
        } else if (selectedType.equalsIgnoreCase("@Price")) {
            Log.d("searchDebug", "performSearch: SEARCHING: " + s + " in posts price");
            if (selectedCriteria.equalsIgnoreCase("Post Title")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for title");
                for (PostModel post : mPosts) {
                    if (post.getPost_title() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Price")) {
                        if (s.length() <= post.getPost_title().length()) {
                            if (post.getPost_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_title().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Body")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for body");
                for (PostModel post : mPosts) {
                    if (post.getPost_body() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Price")) {
                        if (s.length() <= post.getPost_body().length()) {
                            if (post.getPost_body().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_body().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Image Info")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for info");
                for (PostModel post : mPosts) {
                    if (post.getPost_image_info() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Price")) {
                        if (s.length() <= post.getPost_image_info().length()) {
                            if (post.getPost_image_info().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_image_info().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            }
        } else if (selectedType.equalsIgnoreCase("@Help")) {
            Log.d("searchDebug", "performSearch: SEARCHING: " + s + " in posts help");
            if (selectedCriteria.equalsIgnoreCase("Post Title")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for title");
                for (PostModel post : mPosts) {
                    if (post.getPost_title() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Help")) {
                        if (s.length() <= post.getPost_title().length()) {
                            if (post.getPost_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_title().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Body")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for body");
                for (PostModel post : mPosts) {
                    if (post.getPost_body() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Help")) {
                        if (s.length() <= post.getPost_body().length()) {
                            if (post.getPost_body().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_body().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Post Image Info")) {
                Log.d("searchDebug", "performSearch: SEARCHING: " + s + " for info");
                for (PostModel post : mPosts) {
                    if (post.getPost_image_info() != null && post.getPost_type() != null && post.getPost_type().equalsIgnoreCase("@Help")) {
                        if (s.length() <= post.getPost_image_info().length()) {
                            if (post.getPost_image_info().toLowerCase().contains(s.toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        } else {
                            if (s.toLowerCase().contains(post.getPost_image_info().toLowerCase())) {
                                mTempPosts.add(post);
                            }
                        }
                    }
                }
                if (mTempPosts.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mPostAdapter.setDataSet(mTempPosts);
                }
            }
        }
    }

    private void performBooksSearch(String s) {
        mTempBooks.clear();
        if (selectedType.equalsIgnoreCase("@Exchange")) {
            if (selectedCriteria.equalsIgnoreCase("Book Title")) {
                for (Book book : mBooks) {
                    Log.d("bookdebug", "performBooksSearch: TYPE: " + book.getBook_type() + " s: " + s);
                    if (book.getBook_title() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= book.getBook_title().length()) {
                            if (book.getBook_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_title().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Description")) {
                for (Book book : mBooks) {
                    if (book.getBook_description() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= book.getBook_description().length()) {
                            if (book.getBook_description().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_description().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Address")) {
                for (Book book : mBooks) {
                    if (book.getBook_address() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Exchange")) {
                        if (s.length() <= book.getBook_address().length()) {
                            if (book.getBook_address().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_address().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            }
        } else if (selectedType.equalsIgnoreCase("@Donate")) {
            if (selectedCriteria.equalsIgnoreCase("Book Title")) {
                for (Book book : mBooks) {
                    if (book.getBook_title() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Donate")) {
                        if (s.length() <= book.getBook_title().length()) {
                            if (book.getBook_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_title().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Description")) {
                for (Book book : mBooks) {
                    if (book.getBook_description() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Donate")) {
                        if (s.length() <= book.getBook_description().length()) {
                            if (book.getBook_description().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_description().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Address")) {
                for (Book book : mBooks) {
                    if (book.getBook_address() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Donate")) {
                        if (s.length() <= book.getBook_address().length()) {
                            if (book.getBook_address().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_address().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            }
        } else if (selectedType.equalsIgnoreCase("@Sell")) {
            if (selectedCriteria.equalsIgnoreCase("Book Title")) {
                for (Book book : mBooks) {
                    if (book.getBook_title() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Sell")) {
                        if (s.length() <= book.getBook_title().length()) {
                            if (book.getBook_title().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_title().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Description")) {
                for (Book book : mBooks) {
                    if (book.getBook_description() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Sell")) {
                        if (s.length() <= book.getBook_description().length()) {
                            if (book.getBook_description().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_description().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            } else if (selectedCriteria.equalsIgnoreCase("Book Address")) {
                for (Book book : mBooks) {
                    if (book.getBook_address() != null && book.getBook_type() != null && book.getBook_type().equalsIgnoreCase("@Sell")) {
                        if (s.length() <= book.getBook_address().length()) {
                            if (book.getBook_address().toLowerCase().contains(s.toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        } else {
                            if (s.toLowerCase().contains(book.getBook_address().toLowerCase())) {
                                mTempBooks.add(book);
                            }
                        }
                    }
                }
                if (mTempBooks.isEmpty()) {
                    hideProgress();
                    showEmptyMsg();
                } else {
                    hideProgress();
                    hideEmptyMsg();
                    mBookAdapter.setDataSet(mTempBooks);
                }
            }
        }
        mBookAdapter.notifyDataSetChanged();
    }

    private void resetSearch() {
        mTempBooks.clear();
        mTempPosts.clear();
        if (searchType.equalsIgnoreCase("post")) {
            mPostAdapter.setDataSet(mPosts);
            recyclerView.scrollToPosition(mPostAdapter.getItemCount() - 1);
        } else {
            mBookAdapter.setDataSet(mBooks);
            mBookAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(mBookAdapter.getItemCount() - 1);
        }
    }

    private void showEmptyMsg() {
        emptyMsg.setVisibility(View.VISIBLE);
        emptyIcon.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showProgress() {
        recyclerView.setVisibility(View.GONE);
        searchProgress.show();
    }

    private void hideProgress() {
        recyclerView.setVisibility(View.VISIBLE);
        searchProgress.hide();
    }

    private void hideEmptyMsg() {
        emptyMsg.setVisibility(View.GONE);
        emptyIcon.setVisibility(View.GONE);
    }

    private void populatetypeSpinner(String type) {
        String[] spinnerArray = null;
        if (type.equalsIgnoreCase("post")) {
            spinnerArray = new String[]{"@Exchange", "@Price", "@Help"};
        } else {
            spinnerArray = new String[]{"@Exchange", "@Donate", "@Sell"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void populateCriteriaSpinner(String type) {
        String[] spinnerArray = null;
        if (type.equalsIgnoreCase("post")) {
            spinnerArray = new String[]{"Post Title", "Post Body", "Post Image Info"};
        } else {
            spinnerArray = new String[]{"Book Title", "Book Description", "Book Address"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        criteriaSpinner.setAdapter(adapter);
    }

    private void initUI() {
        toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        mPosts = new ArrayList<>();
        mBooks = new ArrayList<>();
        mTempPosts = new ArrayList<>();
        mTempBooks = new ArrayList<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarTitle = findViewById(R.id.search_title);
        typeSpinner = findViewById(R.id.searchTypeSpinner);
        criteriaSpinner = findViewById(R.id.searchCriteriaSpinner);
        recyclerView = findViewById(R.id.searchRecyclerView);
        searchEditTxt = findViewById(R.id.searchEditTxt);
        emptyIcon = findViewById(R.id.emptyIcon);
        emptyMsg = findViewById(R.id.emptyMsg);
        searchProgress = findViewById(R.id.searchAvi);

        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                initSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = adapterView.getItemAtPosition(i).toString();
                if (searchEditTxt.getText() != null) {
                    initSearch(searchEditTxt.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        criteriaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCriteria = adapterView.getItemAtPosition(i).toString();
                if (searchEditTxt.getText() != null) {
                    initSearch(searchEditTxt.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mBooks = null;
        mPosts = null;
        super.onDestroy();
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
    public void showPostDetail(PostModel post) {
        Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
        intent.putExtra("PostObject", post);
        startActivity(intent);
    }

    @Override
    public void showProfile(String id) {
        Intent i = new Intent(SearchActivity.this, ProfileActivity.class);
        i.putExtra("uid", id);
        startActivity(i);
    }
}

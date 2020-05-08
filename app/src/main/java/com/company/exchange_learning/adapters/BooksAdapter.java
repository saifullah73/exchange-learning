package com.company.exchange_learning.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.R;
import com.company.exchange_learning.bookCity.BookDetail;
import com.company.exchange_learning.model.Book;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int BOOK_NO_MORE = 0;
    private final static int BOOK_MORE = 1;
    private static final String TAG = "BooksAdapter";
    private List<Book> books;
    private Context context;

    public class BookViewHolder extends RecyclerView.ViewHolder{

        private TextView titleView;
        private ImageView imageView;
        private ConstraintLayout layout;
        private TextView typeView;
        private TextView descView,priceView;
        private LinearLayout pricelayout;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.bookList_title);
            imageView = itemView.findViewById(R.id.bookList_cover);
            layout = itemView.findViewById(R.id.bookitem_constraint_layout);
            typeView = itemView.findViewById(R.id.bookList_type);
            descView = itemView.findViewById(R.id.bookList_text);
            priceView = itemView.findViewById(R.id.bookList_price);
            pricelayout = itemView.findViewById(R.id.book_price_layout);

        }
    }

    public static class BookNoMorePostViewHolder extends RecyclerView.ViewHolder {

        public BookNoMorePostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public BooksAdapter(List<Book> myDataset, Context context) {
        books = myDataset;
        this.context = context;
    }

    public void setDataSet(List<Book> ss) {
        books = ss;
    }

    @Override
    public int getItemViewType(int position) {
        Book book = books.get(position);
        if (book.getBook_title().equals("NoBook")) {
            return BOOK_NO_MORE;
        } else {
            return BOOK_MORE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BOOK_NO_MORE) {
            return new BookNoMorePostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.no_more_book, parent, false));
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
            return new BookViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (holder.getItemViewType() == BOOK_MORE) {
            //Log.i("BookAdapter","Setting Item "+ books.get(position).toString());
            ((BooksAdapter.BookViewHolder) holder).descView.setText(books.get(position).getBook_description());
            ((BooksAdapter.BookViewHolder) holder).titleView.setText(books.get(position).getBook_title());
            ((BooksAdapter.BookViewHolder) holder).typeView.setText(books.get(position).getBook_type());
            if (books.get(position).getBook_price() != null && !books.get(position).getBook_price().equals("")) {
                ((BooksAdapter.BookViewHolder) holder).priceView.setText(books.get(position).getBook_price() + "RS/-");
            }else{
                ((BookViewHolder) holder).pricelayout.setVisibility(View.GONE);
            }
            Log.d(TAG, "Starting Load");
            Glide.with(context)
                    .load(books.get(position).getCover_photo())
                    .dontAnimate()
                    .placeholder(R.drawable.default_image)
                    .into(((BookViewHolder) holder).imageView);
            ((BooksAdapter.BookViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, BookDetail.class);
                    i.putExtra("book", books.get(position));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        return books.size();
    }

}

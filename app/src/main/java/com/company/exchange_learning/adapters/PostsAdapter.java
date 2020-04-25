package com.company.exchange_learning.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.PostModel;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int POST_NO_IMAGE_TYPE = 1;
    public static final int POST_IMAGE_TYPE = 2;
    public static final int POST_NO_MORE_POST_TYPE = 3;

    private List<PostModel> mPostsList;
    private Context mContex;

    public PostsAdapter(List<PostModel> mPostsList, Context mContex) {
        this.mPostsList = mPostsList;
        this.mContex = mContex;
    }

    @Override
    public int getItemViewType(int position) {
        PostModel post = mPostsList.get(position);
        if ((post.getPostImage() == null || post.getPostImage().equals("")) && !post.getPostType().equals("NoMorePost")) {
            return POST_NO_IMAGE_TYPE;
        } else if (post.getPostType().equals("NoMorePost")) {
            return POST_NO_MORE_POST_TYPE;
        } else {
            return POST_IMAGE_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == POST_NO_IMAGE_TYPE) {
            return new PostsWithNoImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_post_item_layout, parent, false));
        } else if (viewType == POST_NO_MORE_POST_TYPE) {
            return new PostNoMorePostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_no_more_posts_layout, parent, false));
        } else if (viewType == POST_IMAGE_TYPE) {
            return new PostsWithImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_img_post_item__layout, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostModel post = mPostsList.get(position);
        switch (holder.getItemViewType()) {
            case POST_NO_IMAGE_TYPE:
                handlePostWithNoImage(holder, post);
                break;
            case POST_IMAGE_TYPE:
                handlePostWithImage(holder, post);
                break;
        }
    }

    private void handlePostWithNoImage(RecyclerView.ViewHolder holder, PostModel post) {
        ((PostsWithNoImageViewHolder) holder).postItemBody.setText(post.getPostBody());
        ((PostsWithNoImageViewHolder) holder).postItemTitle.setText(post.getPostTitle());
        ((PostsWithNoImageViewHolder) holder).postItemType.setText(post.getPostType());
        ((PostsWithNoImageViewHolder) holder).postItemCategory.setText(post.getPostCategory());
        ((PostsWithNoImageViewHolder) holder).postItemCategory.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
        ((PostsWithNoImageViewHolder) holder).postItemDate.setText(post.getPostDate());
        ((PostsWithNoImageViewHolder) holder).postItemUserName.setText(post.getPostUserPostedName());
    }

    private int randomizeColor() {
        int[] colors = {R.color.category_arch, R.color.category_bioSci, R.color.category_chem, R.color.category_chemEng
                , R.color.category_civil, R.color.category_cs, R.color.category_devStd, R.color.category_math, R.color.category_pharm};
        return ContextCompat.getColor(this.mContex, colors[new Random().nextInt(colors.length)]);
    }

//    private String handleCategoryColor(String category, TextView textView) {
//        switch (category) {
//            case "Architecture":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Biosciences":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Chemical Engineering":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Chemistry":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Civil Engineering":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Computer Science":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Department of Biotechnology":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Development Studies":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Earth Sciences":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Economics":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Electrical and Computer Engineering":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Environmental Sciences":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Health Informatics":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Humanities":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Management Sciences":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Mathematics":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Mechanical Engineering":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Meteorology":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Pharmacy":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Physics":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//            case "Statistics":
//                textView.getBackground().setColorFilter(ContextCompat.getColor(this.mContex, R.color.category_cs), PorterDuff.Mode.SRC_ATOP);
//                break;
//        }
//    }

    private void handlePostWithImage(RecyclerView.ViewHolder holder, PostModel post) {
        ((PostsWithImageViewHolder) holder).postImageItemImageInfo.setText(post.getPostImageInfo());
        ((PostsWithImageViewHolder) holder).postImageItemType.setText(post.getPostType());
        ((PostsWithImageViewHolder) holder).postImageItemCategory.setText(post.getPostCategory());
        ((PostsWithImageViewHolder) holder).postImageItemCategory.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
        ((PostsWithImageViewHolder) holder).postImageItemDate.setText(post.getPostDate());
        ((PostsWithImageViewHolder) holder).postImageItemUserName.setText(post.getPostUserPostedName());
        Glide.with(((PostsWithImageViewHolder) holder).itemView.getContext()).load(post.getPostImage()).placeholder(R.drawable.main_post_image_avatart).into(((PostsWithImageViewHolder) holder).postImageMainImage);
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    public static class PostNoMorePostViewHolder extends RecyclerView.ViewHolder {

        public PostNoMorePostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class PostsWithNoImageViewHolder extends RecyclerView.ViewHolder {

        private TextView postItemBody;
        private TextView postItemCategory;
        private TextView postItemDate;
        private TextView postItemTitle;
        private TextView postItemType;
        private CircleImageView postItemUserImg;
        private TextView postItemUserName;

        public PostsWithNoImageViewHolder(@NonNull View itemView) {
            super(itemView);
            postItemBody = itemView.findViewById(R.id.post_item_postBody_txtview);
            postItemCategory = itemView.findViewById(R.id.post_item_postCategory_txtview);
            postItemDate = itemView.findViewById(R.id.post_item_postTime_txtview);
            postItemTitle = itemView.findViewById(R.id.post_item_postTitle_txtview);
            postItemType = itemView.findViewById(R.id.post_item_postType_txtview);
            postItemUserImg = itemView.findViewById(R.id.post_item_postedUserImg_imgView);
            postItemUserName = itemView.findViewById(R.id.post_item_postedUserName_txtview);
        }
    }

    public static class PostsWithImageViewHolder extends RecyclerView.ViewHolder {

        private TextView postImageItemImageInfo;
        private TextView postImageItemCategory;
        private TextView postImageItemDate;
        private TextView postImageItemType;
        private CircleImageView postImageItemUserImg;
        private ImageView postImageMainImage;
        private TextView postImageItemUserName;

        public PostsWithImageViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageItemImageInfo = itemView.findViewById(R.id.post_image_item_postBody_txtview);
            postImageItemCategory = itemView.findViewById(R.id.post_image_item_postCategory_txtview);
            postImageItemDate = itemView.findViewById(R.id.post_image_item_postTime_txtview);
            postImageItemType = itemView.findViewById(R.id.post_image_item_postType_txtview);
            postImageItemUserImg = itemView.findViewById(R.id.post_image_item_postedUserImg_imgView);
            postImageItemUserName = itemView.findViewById(R.id.post_image_item_postedUserName_txtview);
            postImageMainImage = itemView.findViewById(R.id.main_image_post_image_imgview);
        }
    }
}

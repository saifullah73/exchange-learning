package com.company.exchange_learning.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.exchange_learning.R;
import com.company.exchange_learning.activities.ImageDetailActivity;
import com.company.exchange_learning.listeners.OnPostClickListener;
import com.company.exchange_learning.listeners.OnPostUserImageClickListener;
import com.company.exchange_learning.model.PostModel;

import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int POST_NO_IMAGE_TYPE = 1;
    public static final int POST_IMAGE_TYPE = 2;
    public static final int POST_NO_MORE_POST_TYPE = 3;

    private List<PostModel> mPostsList;
    private Context mContex;
    private OnPostClickListener mOnPostClickListener;
    private OnPostUserImageClickListener listener;


    public PostsAdapter(List<PostModel> mPostsList, Context mContex, OnPostClickListener listener, OnPostUserImageClickListener listener2) {
        this.mPostsList = mPostsList;
        this.listener = listener2;
        this.mContex = mContex;
        this.mOnPostClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        PostModel post = mPostsList.get(position);
        if ((post.getPost_image() == null || post.getPost_image().equals("")) && !post.getPost_type().equals("NoMorePost")) {
            return POST_NO_IMAGE_TYPE;
        } else if (post.getPost_type().equals("NoMorePost")) {
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

    public List<PostModel> getDataSet() {
        return mPostsList;
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

    private void handlePostWithNoImage(RecyclerView.ViewHolder holder, final PostModel post) {
        ((PostsWithNoImageViewHolder) holder).setIsRecyclable(false);
        ((PostsWithNoImageViewHolder) holder).postItemBody.setText(post.getPost_body());
        ((PostsWithNoImageViewHolder) holder).postItemTitle.setText(post.getPost_title());
        ((PostsWithNoImageViewHolder) holder).postItemType.setText(post.getPost_type());
        ((PostsWithNoImageViewHolder) holder).postItemDate.setText(post.getPost_date());
        ((PostsWithNoImageViewHolder) holder).postItemUserName.setText(WordUtils.capitalize(post.getPost_user_posted_name()));
        ((PostsWithNoImageViewHolder) holder).postItemUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showProfile(post.getUser_id());
            }
        });
        ((PostsWithNoImageViewHolder) holder).postItemUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showProfile(post.getUser_id());
            }
        });
        if (post.getPost_user_posted_image() != null) {
            Glide.with(((PostsWithNoImageViewHolder) holder).itemView.getContext()).load(post.getPost_user_posted_image()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.main_user_profile_avatar).into(((PostsWithNoImageViewHolder) holder).postItemUserImg);
        }
        ((PostsWithNoImageViewHolder) holder).titleAndBodyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPostClickListener.showPostDetail(post);
            }
        });

        if (post.getTagged_communities().size() > 0) {
            int n = post.getTagged_communities().size();
            if (n <= 2) {
                ((PostsWithNoImageViewHolder) holder).secondCateogryHolder.setVisibility(View.GONE);
                ((PostsWithNoImageViewHolder) holder).thirdCateogryHolder.setVisibility(View.GONE);
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(mContex);
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    ((PostsWithNoImageViewHolder) holder).firstCateogryHolder.addView(textView);
                }
            } else {
                int dist = 0;
                if (n <= 6) {
                    ((PostsWithNoImageViewHolder) holder).secondCateogryHolder.setVisibility(View.VISIBLE);
                    dist = 2;
                } else {
                    ((PostsWithNoImageViewHolder) holder).thirdCateogryHolder.setVisibility(View.VISIBLE);
                    dist = 3;
                }
                int len = 0;
                int layoutToAdd = 1;
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(mContex);
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    if (dist == 2) {
                        if (layoutToAdd == 1) {
                            ((PostsWithNoImageViewHolder) holder).firstCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else {
                            ((PostsWithNoImageViewHolder) holder).secondCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 1;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        }
                    } else {
                        if (layoutToAdd == 1) {
                            ((PostsWithNoImageViewHolder) holder).firstCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else if (layoutToAdd == 2) {
                            ((PostsWithNoImageViewHolder) holder).secondCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 3;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        } else {
                            ((PostsWithNoImageViewHolder) holder).thirdCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 3;
                            }
                        }
                    }
                }
            }
        }
    }

    private void handlePostWithImage(final RecyclerView.ViewHolder holder, final PostModel post) {
        ((PostsWithImageViewHolder) holder).setIsRecyclable(false);
        ((PostsWithImageViewHolder) holder).postImageItemImageInfo.setText(post.getPost_image_info());
        ((PostsWithImageViewHolder) holder).postImageItemType.setText(post.getPost_type());
        ((PostsWithImageViewHolder) holder).postImageItemDate.setText(post.getPost_date());
        ((PostsWithImageViewHolder) holder).postImageItemUserName.setText(WordUtils.capitalize(post.getPost_user_posted_name()));
        ((PostsWithImageViewHolder) holder).postImageItemUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showProfile(post.getUser_id());
            }
        });
        ((PostsWithImageViewHolder) holder).postImageItemUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.showProfile(post.getUser_id());
            }
        });
        Glide.with(((PostsWithImageViewHolder) holder).itemView.getContext()).load(post.getPost_image()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.main_post_image_avatart).into(((PostsWithImageViewHolder) holder).postImageMainImage);
        if (post.getPost_user_posted_image() != null) {
            Glide.with(((PostsWithImageViewHolder) holder).itemView.getContext()).load(post.getPost_user_posted_image()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.main_user_profile_avatar).into(((PostsWithImageViewHolder) holder).postImageItemUserImg);
        }
        ((PostsWithImageViewHolder) holder).postImageMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PostsWithImageViewHolder) holder).itemView.getContext().startActivity(new Intent(((PostsWithImageViewHolder) holder).itemView.getContext(), ImageDetailActivity.class).putExtra("imageUrl", post.getPost_image()));
            }
        });
        ((PostsWithImageViewHolder) holder).postImageItemImageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPostClickListener.showPostDetail(post);
            }
        });

        if (post.getTagged_communities().size() > 0) {
            int n = post.getTagged_communities().size();
            if (n <= 2) {
                ((PostsWithImageViewHolder) holder).secondCateogryHolder.setVisibility(View.GONE);
                ((PostsWithImageViewHolder) holder).thirdCateogryHolder.setVisibility(View.GONE);
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(mContex);
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    ((PostsWithImageViewHolder) holder).firstCateogryHolder.addView(textView);
                }
            } else {
                int dist = 0;
                if (n <= 6) {
                    ((PostsWithImageViewHolder) holder).secondCateogryHolder.setVisibility(View.VISIBLE);
                    dist = 2;
                } else {
                    ((PostsWithImageViewHolder) holder).thirdCateogryHolder.setVisibility(View.VISIBLE);
                    dist = 3;
                }
                int len = 0;
                int layoutToAdd = 1;
                for (int i = 0; i < post.getTagged_communities().size(); i++) {
                    TextView textView = new TextView(mContex);
                    textView.setText(post.getTagged_communities().get(i));
                    textView.setTextColor(Color.WHITE);
                    textView.setTextSize(14);
                    textView.setBackgroundResource(R.drawable.background_search);
                    textView.getBackground().setColorFilter(randomizeColor(), PorterDuff.Mode.SRC_ATOP);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMarginStart(16);
                    textView.setLayoutParams(lp);
                    textView.setPadding(8, 4, 8, 4);
                    if (dist == 2) {
                        if (layoutToAdd == 1) {
                            ((PostsWithImageViewHolder) holder).firstCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else {
                            ((PostsWithImageViewHolder) holder).secondCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 1;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        }
                    } else {
                        if (layoutToAdd == 1) {
                            ((PostsWithImageViewHolder) holder).firstCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 1;
                            }
                        } else if (layoutToAdd == 2) {
                            ((PostsWithImageViewHolder) holder).secondCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 3;
                                len = 0;
                            } else {
                                layoutToAdd = 2;
                            }
                        } else {
                            ((PostsWithImageViewHolder) holder).thirdCateogryHolder.addView(textView);
                            len += post.getTagged_communities().get(i).length();
                            if (len > 35) {
                                layoutToAdd = 2;
                                len = 0;
                            } else {
                                layoutToAdd = 3;
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDataSet(List<PostModel> posts) {
        this.mPostsList = posts;
        notifyDataSetChanged();
    }

    public void notifyImageLoaded(int pos) {
        notifyItemChanged(pos);
    }


    private int randomizeColor() {
        int[] colors = {R.color.category_bioSci, R.color.category_chem, R.color.category_chemEng, R.color.category_hum, R.color.category_met
                , R.color.category_civil, R.color.category_cs, R.color.category_devStd, R.color.category_math, R.color.category_pharm};
        return ContextCompat.getColor(this.mContex, colors[new Random().nextInt(colors.length)]);
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
        private TextView postItemDate;
        private TextView postItemTitle;
        private TextView postItemType;
        private CircleImageView postItemUserImg;
        private TextView postItemUserName;
        private LinearLayout titleAndBodyLayout;
        private LinearLayout firstCateogryHolder;
        private LinearLayout secondCateogryHolder;
        private LinearLayout thirdCateogryHolder;

        public PostsWithNoImageViewHolder(@NonNull View itemView) {
            super(itemView);
            postItemBody = itemView.findViewById(R.id.post_item_postBody_txtview);
            postItemDate = itemView.findViewById(R.id.post_item_postTime_txtview);
            postItemTitle = itemView.findViewById(R.id.post_item_postTitle_txtview);
            postItemType = itemView.findViewById(R.id.post_item_postType_txtview);
            postItemUserImg = itemView.findViewById(R.id.post_item_postedUserImg_imgView);
            postItemUserName = itemView.findViewById(R.id.post_item_postedUserName_txtview);
            titleAndBodyLayout = itemView.findViewById(R.id.titleAndBodyLayout);
            firstCateogryHolder = itemView.findViewById(R.id.postItemFirstCategoryLayout);
            secondCateogryHolder = itemView.findViewById(R.id.postItemSecondCategoryLayout);
            thirdCateogryHolder = itemView.findViewById(R.id.postItemThirdCategoryLayout);
        }
    }

    public static class PostsWithImageViewHolder extends RecyclerView.ViewHolder {

        private TextView postImageItemImageInfo;
        private TextView postImageItemDate;
        private TextView postImageItemType;
        private CircleImageView postImageItemUserImg;
        private ImageView postImageMainImage;
        private TextView postImageItemUserName;
        private LinearLayout firstCateogryHolder;
        private LinearLayout secondCateogryHolder;
        private LinearLayout thirdCateogryHolder;

        public PostsWithImageViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageItemImageInfo = itemView.findViewById(R.id.post_image_item_postBody_txtview);
            postImageItemDate = itemView.findViewById(R.id.post_image_item_postTime_txtview);
            postImageItemType = itemView.findViewById(R.id.post_image_item_postType_txtview);
            postImageItemUserImg = itemView.findViewById(R.id.post_image_item_postedUserImg_imgView);
            postImageItemUserName = itemView.findViewById(R.id.post_image_item_postedUserName_txtview);
            postImageMainImage = itemView.findViewById(R.id.main_image_post_image_imgview);
            firstCateogryHolder = itemView.findViewById(R.id.postImgItemFirstCategoryLayout);
            secondCateogryHolder = itemView.findViewById(R.id.postImgItemSecondCategoryLayout);
            thirdCateogryHolder = itemView.findViewById(R.id.postImgItemThirdCategoryLayout);
        }
    }


}

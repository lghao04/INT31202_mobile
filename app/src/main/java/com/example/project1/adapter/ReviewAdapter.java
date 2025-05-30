package com.example.project1.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.database.Review;
import com.example.project1.database.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private Map<String, User> userMap; // userId -> User (fullName + profileImageUrl)

    public ReviewAdapter(Context context, List<Review> reviewList, Map<String, User> userMap) {
        this.context = context;
        this.reviewList = reviewList;
        this.userMap = userMap;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Set rating
        holder.ratingBar.setRating(review.getRating());

        // Set comment
        holder.textComment.setText(review.getComment());

        // Set timestamp
        Map<String, Object> timestamp = review.getTimestamp();
        if (timestamp != null && timestamp.containsKey("time")) {
            Object timeObj = timestamp.get("time");
            long timeMillis;
            if (timeObj instanceof Long) {
                timeMillis = (Long) timeObj;
            } else if (timeObj instanceof Double) {
                timeMillis = ((Double) timeObj).longValue();
            } else {
                timeMillis = 0;
            }

            String formattedTime = DateFormat.format("dd/MM/yyyy HH:mm", new Date(timeMillis)).toString();
            holder.textTimestamp.setText(formattedTime);
        } else {
            holder.textTimestamp.setText("");
        }

        // Set user info
        User user = userMap.get(review.getUserId());
        if (user != null) {
            holder.textUserName.setText(user.getFullName());
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .into(holder.imageAvatar);
        } else {
            holder.textUserName.setText("Người dùng ẩn danh");
            holder.imageAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        // Set image review (hiển thị ảnh đầu tiên nếu có)
        if (review.getImageUrls() != null && !review.getImageUrls().isEmpty()) {
            holder.imageReview.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(review.getImageUrls().get(0))
                    .into(holder.imageReview);
        } else {
            holder.imageReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar, imageReview;
        TextView textUserName, textComment, textTimestamp;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            imageReview = itemView.findViewById(R.id.imageReview);
            textUserName = itemView.findViewById(R.id.textUserName);
            textComment = itemView.findViewById(R.id.textComment);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

package com.example.project1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.database.Review;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviews;
    private List<String> reviewIds;

    public ReviewAdapter(Context context, List<Review> reviews, List<String> reviewIds) {
        this.context = context;
        this.reviews = reviews;
        this.reviewIds = reviewIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtComment, txtReply, txtDate;
        RatingBar ratingBar;
        ImageView imgReview;
        EditText etReply;
        Button btnSendReply;

        public ViewHolder(View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtReply = itemView.findViewById(R.id.txtReply);
            txtDate = itemView.findViewById(R.id.txtDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imgReview = itemView.findViewById(R.id.imgReview);
            etReply = itemView.findViewById(R.id.etReply);
            btnSendReply = itemView.findViewById(R.id.btnSendReply);
        }
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review r = reviews.get(position);
        String reviewId = reviewIds.get(position);

        holder.txtComment.setText(r.comment);
        holder.ratingBar.setRating(r.rating);
        holder.txtDate.setText(r.createdAt);

        if (r.images != null && !r.images.isEmpty()) {
            Glide.with(context).load(r.images.get(0)).into(holder.imgReview);
            holder.imgReview.setVisibility(View.VISIBLE);
        } else {
            holder.imgReview.setVisibility(View.GONE);
        }

        if (r.reply != null && r.reply.text != null) {
            holder.txtReply.setText("Phản hồi: " + r.reply.text);
            holder.txtReply.setVisibility(View.VISIBLE);
            holder.etReply.setVisibility(View.GONE);
            holder.btnSendReply.setVisibility(View.GONE);
        } else {
            holder.txtReply.setVisibility(View.GONE);
            holder.etReply.setVisibility(View.VISIBLE);
            holder.btnSendReply.setVisibility(View.VISIBLE);

            holder.btnSendReply.setOnClickListener(v -> {
                String replyText = holder.etReply.getText().toString().trim();
                if (!replyText.isEmpty()) {
                    String replyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date());

                    Map<String, Object> replyMap = new HashMap<>();
                    replyMap.put("text", replyText);
                    replyMap.put("createdAt", replyDate);

                    FirebaseDatabase.getInstance().getReference("reviews")
                            .child(reviewId)
                            .child("reply")
                            .setValue(replyMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Phản hồi thành công!", Toast.LENGTH_SHORT).show();
                                holder.etReply.setVisibility(View.GONE);
                                holder.btnSendReply.setVisibility(View.GONE);
                                holder.txtReply.setText("Phản hồi: " + replyText);
                                holder.txtReply.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}

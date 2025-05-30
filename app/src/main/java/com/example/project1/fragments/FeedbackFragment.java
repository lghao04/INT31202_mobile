package com.example.project1.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.adapter.ReviewAdapter;
import com.example.project1.database.Review;
import com.example.project1.database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFeedback);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(getContext(), reviewList, userMap);
        recyclerView.setAdapter(adapter);

        loadReviews();

        return view;
    }

    private void loadReviews() {
        String restaurantId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("reviews");

        reviewRef.orderByChild("restaurantId").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Review review = snap.getValue(Review.class);
                            if (review != null) {
                                review.setReviewId(snap.getKey()); // Gán key làm id cho review
                                reviewList.add(review);
                                Log.d("FeedbackFragment", "Review: " + review.getComment());
                            }
                        }

                        Log.d("FeedbackFragment", "Số review tải được: " + snapshot.getChildrenCount());

                        loadUsers(); // Sau khi có review thì load thông tin user
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.e("FeedbackFragment", "Load reviews lỗi: " + error.getMessage());
                    }
                });
    }

    private void loadUsers() {
        userMap.clear();

        // Tạo danh sách userId duy nhất từ reviewList
        Set<String> userIds = new HashSet<>();
        for (Review review : reviewList) {
            if (review.getUserId() != null) {
                userIds.add(review.getUserId());
            }
        }

        if (userIds.isEmpty()) {
            // Không có userId nào => thông báo cập nhật adapter luôn
            adapter.notifyDataSetChanged();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Đếm số lượng user cần load để biết khi nào xong
        final int totalUsers = userIds.size();
        final int[] loadedCount = {0};


        for (String userId : userIds) {
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = null;
                    try {
                        user = snapshot.getValue(User.class);
                    } catch (Exception e) {
                        Log.e("FirebaseParse", "Lỗi khi parse user: " + snapshot.getKey(), e);
                    }
                    if (user != null) {
                        userMap.put(userId, user);
                    }
                    loadedCount[0]++;
                    // Khi đã load hết user thì thông báo adapter
                    if (loadedCount[0] == totalUsers) {
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FeedbackFragment", "Load user lỗi: " + error.getMessage());
                    loadedCount[0]++;
                    if (loadedCount[0] == totalUsers) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

}

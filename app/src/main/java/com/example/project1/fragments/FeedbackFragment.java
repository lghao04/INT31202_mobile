package com.example.project1.fragments;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<Review> reviewList = new ArrayList<>();
    private List<String> reviewIds = new ArrayList<>(); // để lưu reviewId tương ứng

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFeedback);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(getContext(), reviewList, reviewIds);
        recyclerView.setAdapter(adapter);

        loadReviews();

        return view;
    }

    private void loadReviews() {
        String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("reviews");

        ref.orderByChild("restaurantId").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewList.clear();
                        reviewIds.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Review r = snap.getValue(Review.class);
                            if (r != null) {
                                reviewList.add(r);
                                reviewIds.add(snap.getKey());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }
}

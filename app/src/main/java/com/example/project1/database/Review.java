package com.example.project1.database;
import java.util.List;
import java.util.Map;

public class Review {
    private String comment;
    private List<String> imageUrls;
    private String orderId;
    private int rating;
    private String restaurantId;
    private Map<String, Object> timestamp;
    private String userId;
    private String reviewId;



    public Review() {
        // Required empty constructor for Firebase
    }

    public Review(String comment, List<String> imageUrls, String orderId, int rating,
                  String restaurantId, Map<String, Object> timestamp, String userId) {
        this.comment = comment;
        this.imageUrls = imageUrls;
        this.orderId = orderId;
        this.rating = rating;
        this.restaurantId = restaurantId;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters and setters
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public Map<String, Object> getTimestamp() { return timestamp; }
    public void setTimestamp(Map<String, Object> timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

}

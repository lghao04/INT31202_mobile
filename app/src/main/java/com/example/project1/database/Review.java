package com.example.project1.database;

import java.util.List;
import java.util.Map;

public class Review {
    public String comment;
    public String createdAt;
    public List<String> images;
    public String orderId;
    public int rating;
    public String restaurantId;
    public String userId;
    public Reply reply;

    public static class Reply {
        public String createdAt;
        public String text;

        public Reply() {}
    }

    public Review() {}

}

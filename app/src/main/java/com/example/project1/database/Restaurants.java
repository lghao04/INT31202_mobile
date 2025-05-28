package com.example.project1.database;

import java.util.Map;

public class Restaurants {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String imageUrl;
    private String address;
    private double wallet;
    private double rating;
    private int totalRatings;
    private Map<String, OpeningHour> openingHours;

    public Restaurants() {
        // Required for Firebase
    }

    public Restaurants(String id, String name, String email, String phone, String imageUrl,
                       String address, double wallet, double rating, int totalRatings,
                       Map<String, OpeningHour> openingHours) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.address = address;
        this.wallet = wallet;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.openingHours = openingHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Map<String, OpeningHour> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<String, OpeningHour> openingHours) {
        this.openingHours = openingHours;
    }
}

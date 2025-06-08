package com.example.project1.database;
public class Restaurants {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address; // ✅ Sử dụng String thay vì object hoặc Map
    private String imageUrl;
    private Double wallet;
    private Double rating;

    public Restaurants() {}

    public Restaurants(String id, String name, String email, String phone, String address, String imageUrl, Double wallet, Double rating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.imageUrl = imageUrl;
        this.wallet = wallet;
        this.rating = rating;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getWallet() { return wallet; }
    public void setWallet(Double wallet) { this.wallet = wallet; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}

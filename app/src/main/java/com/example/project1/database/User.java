package com.example.project1.database;

import java.util.Map;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    private Map<String, Object> recentAddresses;
    private long createdAt;
    private String profileImageUrl;

    public User() {} // Bắt buộc cần constructor rỗng cho Firebase

    // Getter và Setter cho từng trường

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }

    public Map<String, Object> getRecentAddresses() {
        return recentAddresses;
    }
    public void setRecentAddresses(Map<String, Object> recentAddresses) {
        this.recentAddresses = recentAddresses;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // ✅ Hàm tiện ích trả về tên đầy đủ
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}

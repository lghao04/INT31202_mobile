package com.example.project1.database;


import java.io.Serializable;

public class Categories {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private int imageResource;

    public Categories() {

    }

    public Categories(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Categories(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
    @Override
    public String toString() {
        return name;  // Giúp Spinner hiển thị name thay vì đối tượng Categories
    }
}
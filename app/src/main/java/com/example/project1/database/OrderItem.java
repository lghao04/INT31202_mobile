//package com.example.project1.database;
//
//
//public class OrderItem {
//    private String category;
//    private int imageResource;
//    private String imageUrl;
//    private String itemId;
//    private String itemName;
//    private long itemPrice;
//    private int quantity;
//    private String restaurantId;
//    private long totalPrice;
//
//    public OrderItem() {
//    }
//
//    // Getters & setters
//    public String getCategory() { return category; }
//    public void setCategory(String category) { this.category = category; }
//
//    public int getImageResource() { return imageResource; }
//    public void setImageResource(int imageResource) { this.imageResource = imageResource; }
//
//    public String getImageUrl() { return imageUrl; }
//    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
//
//    public String getItemId() { return itemId; }
//    public void setItemId(String itemId) { this.itemId = itemId; }
//
//    public String getItemName() { return itemName; }
//    public void setItemName(String itemName) { this.itemName = itemName; }
//
//    public long getItemPrice() { return itemPrice; }
//    public void setItemPrice(long itemPrice) { this.itemPrice = itemPrice; }
//
//    public int getQuantity() { return quantity; }
//    public void setQuantity(int quantity) { this.quantity = quantity; }
//
//    public String getRestaurantId() { return restaurantId; }
//    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
//
//    public long getTotalPrice() { return totalPrice; }
//    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }
//}
package com.example.project1.database;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String itemId;
    private String itemName;
    private int itemPrice;
    private int quantity;
    private int totalPrice;
    private String imageUrl;
    private String restaurantId;
    private String category;

    public OrderItem() {
        // Constructor rỗng cho Firebase
    }

    public OrderItem(String itemId, String itemName, int itemPrice, int quantity,
                     int totalPrice, String imageUrl, String restaurantId, String category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.imageUrl = imageUrl;
        this.restaurantId = restaurantId;
        this.category = category;
    }

    // Getter và Setter
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getItemPrice() { return itemPrice; }
    public void setItemPrice(int itemPrice) { this.itemPrice = itemPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

package com.example.project1.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {
    private String id;
    private String userId;
    private String restaurantId;
    private String restaurantName;
    private String address;
    private int subtotal;
    private int deliveryFee;
    private int total;
    private String status;
    private String note;
    private String paymentMethod;
    private Date orderTime;
    private ArrayList<OrderItem> items;
    private String cancelReason; // ✅ Thêm trường mới

    public Order() {
        // Constructor rỗng cho Firebase
    }

    public Order(String id, String userId, String restaurantId, String restaurantName,
                 String address, int subtotal, int deliveryFee, int total,
                 String status, String note, String paymentMethod, Date orderTime,
                 ArrayList<OrderItem> items, String cancelReason) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.address = address;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = total;
        this.status = status;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.orderTime = orderTime;
        this.items = (items == null) ? new ArrayList<>() : items;
        this.cancelReason = cancelReason;
    }

    // Getter và Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public int getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(int deliveryFee) { this.deliveryFee = deliveryFee; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public ArrayList<OrderItem> getItems() {
        return (items == null) ? new ArrayList<>() : items;
    }
    public void setItems(ArrayList<OrderItem> items) { this.items = items; }

    public String getCancelReason() { return cancelReason; }  // ✅ Getter
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }  // ✅ Setter
}

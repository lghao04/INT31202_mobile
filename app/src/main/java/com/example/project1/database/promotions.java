package com.example.project1.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class promotions implements Serializable {
    private String id; // sẽ gán thủ công từ push key
    private String promoCode;
    private String discountType;
    private double discountAmount;
    private String startDate;
    private String endDate;
    private int totalUsage;
    private int usagePerUser;
    private String minimumOrder;
    private String restaurantId;
    private double maxDiscountAmount;
    private boolean expired = false;

    public promotions() {
        // Firebase cần constructor rỗng
    }



    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getTotalUsage() { return totalUsage; }
    public void setTotalUsage(int totalUsage) { this.totalUsage = totalUsage; }

    public int getUsagePerUser() { return usagePerUser; }
    public void setUsagePerUser(int usagePerUser) { this.usagePerUser = usagePerUser; }

    public String getMinimumOrder() { return minimumOrder; }
    public void setMinimumOrder(String minimumOrder) { this.minimumOrder = minimumOrder; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public double getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(double maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

}

package com.example.newbee2.model;

import java.util.List;

public class Order {
    private Long orderId;
    private String orderNo;
    private Long userId;
    private Integer totalPrice;
    private String userAddressId;
    private Integer orderStatus;
    private String createTime;
    private String payType;
    private List<OrderItem> orderItems;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }
    public String getUserAddressId() { return userAddressId; }
    public void setUserAddressId(String userAddressId) { this.userAddressId = userAddressId; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public String getStatusText() {
        switch (orderStatus) {
            case 0: return "待支付";
            case 1: return "已支付/待确认";
            case 2: return "已确认/待发货";
            case 3: return "已发货/待收货";
            case 4: return "交易成功";
            default: return "未知状态";
        }
    }
}

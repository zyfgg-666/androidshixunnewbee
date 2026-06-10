package com.example.newbee2.model;

public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long goodsId;
    private Integer goodsCount;
    private String goodsName;
    private String goodsCoverImg;
    private Integer sellingPrice;

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Integer getGoodsCount() { return goodsCount; }
    public void setGoodsCount(Integer goodsCount) { this.goodsCount = goodsCount; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsCoverImg() { return goodsCoverImg; }
    public void setGoodsCoverImg(String goodsCoverImg) { this.goodsCoverImg = goodsCoverImg; }
    public Integer getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Integer sellingPrice) { this.sellingPrice = sellingPrice; }
}

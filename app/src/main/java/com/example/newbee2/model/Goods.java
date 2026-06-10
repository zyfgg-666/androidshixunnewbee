package com.example.newbee2.model;

public class Goods {
    private Long goodsId;
    private String goodsName;
    private String goodsIntro;
    private Integer goodsCategoryId;
    private String goodsCoverImg;
    private Object goodsCarouselList;
    private Integer sellingPrice;
    private String tag;
    private Integer originalPrice;
    private Integer goodsSellStatus;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsIntro() { return goodsIntro; }
    public void setGoodsIntro(String goodsIntro) { this.goodsIntro = goodsIntro; }
    public Integer getGoodsCategoryId() { return goodsCategoryId; }
    public void setGoodsCategoryId(Integer goodsCategoryId) { this.goodsCategoryId = goodsCategoryId; }
    public String getGoodsCoverImg() { return goodsCoverImg; }
    public void setGoodsCoverImg(String goodsCoverImg) { this.goodsCoverImg = goodsCoverImg; }
    public Object getGoodsCarouselList() { return goodsCarouselList; }
    public void setGoodsCarouselList(Object goodsCarouselList) { this.goodsCarouselList = goodsCarouselList; }
    public Integer getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(Integer sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public Integer getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Integer originalPrice) { this.originalPrice = originalPrice; }
    public Integer getGoodsSellStatus() { return goodsSellStatus; }
    public void setGoodsSellStatus(Integer goodsSellStatus) { this.goodsSellStatus = goodsSellStatus; }
}

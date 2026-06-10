package com.example.newbee2.model;

public class User {
    private Long userId;
    private String nickName;
    private String loginName;
    private String passwordMd5;
    private String introduceSign;
    private String address;
    private int shoppingCartCount;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getLoginName() { return loginName; }
    public void setLoginName(String loginName) { this.loginName = loginName; }
    public String getPasswordMd5() { return passwordMd5; }
    public void setPasswordMd5(String passwordMd5) { this.passwordMd5 = passwordMd5; }
    public String getIntroduceSign() { return introduceSign; }
    public void setIntroduceSign(String introduceSign) { this.introduceSign = introduceSign; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getShoppingCartCount() { return shoppingCartCount; }
    public void setShoppingCartCount(int shoppingCartCount) { this.shoppingCartCount = shoppingCartCount; }
}

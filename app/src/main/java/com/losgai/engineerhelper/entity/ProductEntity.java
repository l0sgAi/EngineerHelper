package com.losgai.engineerhelper.entity;


import java.util.Date;

public class ProductEntity {

    private Long id; // 序列号
    private String productName; // 产品名称;
    private Date purchaseTime; // 创建时间;
    private Long AuthInfoId; // 授权信息ID;

    public ProductEntity(String productName, Date purchaseTime, Long authInfoId) { // 构造函数
        this.productName = productName;
        this.purchaseTime = purchaseTime;
        AuthInfoId = authInfoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Long getAuthInfoId() {
        return AuthInfoId;
    }

    public void setAuthInfoId(Long authInfoId) {
        AuthInfoId = authInfoId;
    }
}

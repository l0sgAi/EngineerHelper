package com.losgai.engineerhelper.entity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductEntity {

    private Long id; // 序列号
    private String productName; // 产品名称;
    private Date purchaseTime; // 创建时间;
    private Long customerId; // 客户ID;

    public ProductEntity(String productName, Date purchaseTime,Long customerId) { // 构造函数
        this.productName = productName;
        this.purchaseTime = purchaseTime;
        this.customerId = customerId;
    }


    public ProductEntity() {} // 空构造函数

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

    public String DateToString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(purchaseTime);
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}

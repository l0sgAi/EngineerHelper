package com.losgai.engineerhelper.entity;


public class CustomerInfoEntity {

    private Long id;
    private String customerName; // 客户名称
    private String address; // 地址
    private String phone; // 电话
    private String email; // 邮箱

    // 构造函数
    public CustomerInfoEntity(String customerName,
                              String address,
                              String phone,
                              String email) {
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public CustomerInfoEntity() {} // 空构造函数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

package com.losgai.engineerhelper.entity;


import java.util.Date;

public class AuthInfoEntity {

    private Long id;
    private Date expireDate; // 授权过期时间
    private String authCode; // 授权码

    // TODO: 授权码初定生成规则：当前时间戳+随机数+产品ID+客户ID+工程师ID+过期时间
    //  最后将字符串SHA256加密

    public AuthInfoEntity(Date expireDate, String authCode) { // 构造函数
        this.expireDate = expireDate;
        this.authCode = authCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}

package com.losgai.engineerhelper.entity;


public class EngineerInfoEntity {

    private Long id;
    private String username;
    private String password;

    public EngineerInfoEntity(String username, String password) { // 构造函数
        this.username = username;
        this.password = password;
    }

    public EngineerInfoEntity() {} // 空构造函数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

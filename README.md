# 安卓课程设计:工程师助手

### 模块划分

1. ##### 登录模块
   
   在`LoginActivity.java`中，通过`SQLite`进行数据比对，进行简单的登录与注册，包括用户名和密码，工程师用户数据如下
   
   ```java
   private Long id;
   private String username;
   private String password;
   ```

2. ##### 客户管理模块
   
   在`CustomerFragment.java`中，客户信息增删改查，包括客户信息客户编号`Long`、姓名`String`、地址`String`、电话`String`及邮箱`String`
   ```java
   private Long id;
   private String customerName; // 客户名称
   private String address; // 地址
   private String phone; // 电话
   private String email; // 邮箱
   ```

3. ##### 产品管理模块
   
   在`ProductFragment.java`中，产品信息增删改查，包括产品序列号、名称及购买时间，还有对应的`Long`类型授权`id`，一个产品只有一个所属客户，对应一个客户编号`Long`
   
   ```java
   private Long id; // 序列号
   private String productName; // 产品名称;
   private Date purchaseTime; // 创建时间;
   private Long customerId; // 客户ID;
   ```
   
   一个产品只对应一条授权信息`AuthInfo`，通过在产品列表长按弹出的选项菜单中选择"查看授权信息"可以进入产品授权管理模块，通过弹窗页面实现，包括的数据为
   
   ```java
   private Long id;
   private Date expireDate; // 授权过期时间
   private String authCode; // 授权码
   private Long productId; // 产品ID
   ```

   由于一个产品只对应一条授权信息，所以不设计额外的页面进行授权信息管理，而是和产品信息集成

4. ##### 个人主页
   
   对应`PersonalCenterFragment.java`，目前只有显示当前登录工程师信息和修改密码两个功能

5. ##### 导航模块
   
   长按对应用户数据，在弹出的菜单中选择"导航至用户地址"选项，调用高德`API`并打开高德地图应用，导航至用户的地址

### 当前进度

   *基本功能基本完成*



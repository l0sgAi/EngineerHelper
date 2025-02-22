package com.losgai.engineerhelper;

import static com.losgai.engineerhelper.dao.EngineerInfoDao.ID;
import static com.losgai.engineerhelper.helper.GeneralHelper.customToast;
import static com.losgai.engineerhelper.helper.GeneralHelper.sha256Encrypt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.losgai.engineerhelper.dao.EngineerInfoDao;
import com.losgai.engineerhelper.entity.EngineerInfoEntity;


// 登录页面
public class LoginActivity extends AppCompatActivity {

    private EngineerInfoDao engineerInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout); // 首先进入登录页面

        // 初始化数据库
        engineerInfoDao = new EngineerInfoDao(this);
        engineerInfoDao.open(); // 这里已经创建了数据库，后面的dao只要建表即可

        // 获取SharedPreferences对象
        SharedPreferences sharedPreferences = getSharedPreferences("ENGINEERHELPER_saved_account", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 获取视图对象
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.loginBtn); // 登录按钮
        Button registerButton = findViewById(R.id.registerBtn); // 注册按钮

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isSave = findViewById(R.id.saveAccount); // 是否记住用户名/密码按钮

        // 填入保存的用户名/密码
        if (sharedPreferences.contains("cur_username")) {
            usernameEditText.setText(sharedPreferences.getString("cur_username", ""));
            isSave.setChecked(true);
        } else {
            usernameEditText.setText("");
            isSave.setChecked(false);
        }

        if (sharedPreferences.contains("cur_password")) {
            passwordEditText.setText(sharedPreferences.getString("cur_password", ""));
            isSave.setChecked(true);
        } else {
            passwordEditText.setText("");
            isSave.setChecked(false);
        }

        // 设置监听器
        loginButton.setOnClickListener(v -> { // 登录按钮
            String userName = usernameEditText.getText().toString(); // 用户名
            String passwordNoEncrypted = passwordEditText.getText().toString(); // 未加密的密码
            Cursor cursor = engineerInfoDao.queryEngineerByUserName(userName);
            Log.i("查询用户名结果" + userName, String.valueOf(cursor.getCount()));
            if (cursor.getCount() > 0) {
                // 用户名存在
                cursor.moveToFirst();
                @SuppressLint("Range")
                long id = cursor.getLong(cursor.getColumnIndex(ID));
                @SuppressLint("Range")
                String password = cursor.getString(cursor.getColumnIndex("password"));
                if (password.equals(sha256Encrypt(passwordNoEncrypted))) {
                    // 对比密文，密码正确，跳转
                    customToast(this, userName + " 登录成功", R.layout.toast_view);

                    if (isSave.isChecked()) { // 记住用户名/密码
                        editor.putString("cur_username", userName);
                        editor.putString("cur_password", passwordNoEncrypted);
                    } else { // 不记住用户名密码
                        editor.remove("cur_username");
                        editor.remove("cur_password");
                    }

                    editor.putLong("cur_id", id); // 保存当前登录的id
                    editor.putString("cur_username_show", userName); // 用于在主页面显示用户名
                    editor.apply(); // 提交修改
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // 密码错误
                    customToast(this, "密码错误", R.layout.toast_view_e);
                }
            } else {
                // 用户名不存在
                customToast(this, "用户名不存在", R.layout.toast_view_e);
            }
        });

        registerButton.setOnClickListener(v -> { // 简单注册实现
            String userName = usernameEditText.getText().toString(); // 用户名
            String passwordNoEncrypted = passwordEditText.getText().toString(); // 未加密的密码
            Cursor cursor = engineerInfoDao.queryEngineerByUserName(userName);
            if (cursor.getCount() > 0) {
                // 用户名存在，非法
                customToast(this, "用户名已存在", R.layout.toast_view_e);
            } else {
                // 用户名不存在
                // 插入数据
                engineerInfoDao.insertEngineer(new EngineerInfoEntity(userName, sha256Encrypt(passwordNoEncrypted)));
                customToast(this, "注册成功", R.layout.toast_view);
            }
        });

    }
}
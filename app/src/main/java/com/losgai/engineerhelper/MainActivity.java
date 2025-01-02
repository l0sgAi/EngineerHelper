package com.losgai.engineerhelper;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


// 登录页面
public class MainActivity extends AppCompatActivity {
    // TODO: 客户管理/产品管理/个人中心(密码修改) 3个Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout); // 首先进入登录页面
    }
}

package com.losgai.engineerhelper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


// 登录页面
public class MainActivity extends AppCompatActivity {
    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // 初始化底部导航栏
        // TODO: 客户管理/产品管理/个人中心(密码修改) 3个Fragment
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // 默认加载客户管理Fragment
        if (savedInstanceState == null) {
            selectedFragment = new CustomerManagementFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        // 设置底部菜单栏的选项监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_customer) {
                selectedFragment = new CustomerManagementFragment();
            } else if (item.getItemId() == R.id.nav_product) {
                selectedFragment = new ProductManagementFragment();
            } else if (item.getItemId() == R.id.nav_personal_data) {
                selectedFragment = new PersonalCenterFragment();
            }
            // 切换Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }
}

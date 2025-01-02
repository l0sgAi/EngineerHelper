package com.losgai.engineerhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

// 客户管理Fragment
public class CustomerManagementFragment extends Fragment {
    // TODO: 客户管理Fragment的布局文件添加对应的适配器和内部视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_management, container, false);
    }
}

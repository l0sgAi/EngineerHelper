package com.losgai.engineerhelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

// 个人中心Fragment
public class PersonalCenterFragment extends Fragment {
    // TODO: 个人中心Fragment的布局文件添加对应的适配器和内部视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        // TODO: 个人中心的交互逻辑
        return view;
    }
}

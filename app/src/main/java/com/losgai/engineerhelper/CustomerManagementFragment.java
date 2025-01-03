package com.losgai.engineerhelper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.losgai.engineerhelper.adapter.CustomerAdapter;
import com.losgai.engineerhelper.dao.CustomerInfoDao;
import com.losgai.engineerhelper.entity.CustomerInfoEntity;

import java.util.List;
import java.util.Objects;

// 客户管理Fragment
public class CustomerManagementFragment extends Fragment {

    private CustomerInfoDao customerInfoDao; // 客户信息DAO
    private ListView customerListView; // 客户信息列表视图
    private CustomerAdapter customerAdapter; // 客户信息适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_management, container, false);
        // TODO: 客户管理Fragment的逻辑
        try{
            // 获取所有客户信息
            customerInfoDao = new CustomerInfoDao(getContext());
            customerInfoDao.open();
            // 客户信息列表
            List<CustomerInfoEntity> customerList = customerInfoDao.getAllCustomers();

            // 绑定列表视图控件
            customerListView = view.findViewById(R.id.listViewCustomer);

            // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
            customerAdapter = new CustomerAdapter(getContext(), R.layout.inner_list_layout_customer, customerList);
            customerListView.setAdapter(customerAdapter);

            // TODO: 绑定工具栏控件
            Toolbar toolbar = view.findViewById(R.id.toolbar_customer);

        }catch (Exception e){
            Log.e("创建客户信息DAO失败", Objects.requireNonNull(e.getMessage()));
        }
        return view;
    }
}

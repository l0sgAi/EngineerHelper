package com.losgai.engineerhelper;

import static com.losgai.engineerhelper.helper.GeneralHelper.customToast;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.losgai.engineerhelper.adapter.CustomerAdapter;
import com.losgai.engineerhelper.dao.CustomerInfoDao;
import com.losgai.engineerhelper.entity.CustomerInfoEntity;

import java.util.List;
import java.util.Objects;

import android.view.Menu;

// 客户管理Fragment
public class CustomerManagementFragment extends Fragment {

    private CustomerInfoDao customerInfoDao; // 客户信息DAO
    private ListView customerListView; // 客户信息列表视图
    private CustomerAdapter customerAdapter; // 客户信息适配器
    private List<CustomerInfoEntity> customerList; // 客户信息列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_management, container, false);
        // TODO: 客户管理Fragment的逻辑
        try {
            // 获取所有客户信息
            customerInfoDao = new CustomerInfoDao(getContext());
            customerInfoDao.open();
            // 客户信息列表
            customerList = customerInfoDao.getAllCustomers();

            // 绑定列表视图控件
            customerListView = view.findViewById(R.id.listViewCustomer);

            // 创建适配器并设置给 ListView 定义适配器 控件-桥梁-数据
            customerAdapter = new CustomerAdapter(getContext(), R.layout.inner_list_layout_customer, customerList);
            customerListView.setAdapter(customerAdapter);

        } catch (Exception e) {
            Log.e("创建客户信息DAO失败", Objects.requireNonNull(e.getMessage()));
        }
        return view;
    }

    // 创建菜单栏
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 获取 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_customer);

        // 设置 Toolbar 作为 ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        // 添加 MenuProvider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // 加载菜单资源文件
                menuInflater.inflate(R.menu.toolbar_customer_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_customer_add) {
                    showAddOrUpdateDialog(customerAdapter, null);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_customer_search) {
                    // TODO: 条件查询客户信息
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_customer_refresh) {
                    reset("客户数据已刷新",true);
                    return true;
                } else {
                    return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * 显示新增或更新对话框
     *
     * @param customerAdapter    适配器
     * @param customerInfoEntity 如果是更新，选中的对象
     */
    private void showAddOrUpdateDialog(CustomerAdapter customerAdapter,
                                       CustomerInfoEntity customerInfoEntity) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.customer_add_or_update, null);
        builder.setView(dialogView);

        // 获取对应组件
        EditText name = dialogView.findViewById(R.id.customer_add_or_update_name);
        EditText address = dialogView.findViewById(R.id.customer_add_or_update_address);
        EditText phone = dialogView.findViewById(R.id.customer_add_or_update_phone);
        EditText email = dialogView.findViewById(R.id.customer_add_or_update_email);
        Button buttonSubmit = dialogView.findViewById(R.id.submit_customer_add_or_update);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSubmit.setOnClickListener(v -> { // 新增数据
            if (customerInfoEntity == null) { // 没有选中的顾客对象，新增
                try {
                    // 创建新的 CustomerInfoEntity 对象
                    CustomerInfoEntity data = new CustomerInfoEntity(
                            name.getText().toString(),
                            address.getText().toString(),
                            phone.getText().toString(),
                            email.getText().toString()
                    );
                    Log.i("新增数据", "showDialog " + data);

                    if (!data.getCustomerName().isEmpty() &&
                            !data.getAddress().isEmpty() &&
                            !data.getPhone().isEmpty()) { // 至少输入姓名、地址和电话号

                        // 将新顾客对象添加到列表
                        if (customerInfoDao.addCustomer(data) > 0) { // >0即插入成功
                            customerList.clear();
                            customerList.addAll(customerInfoDao.getAllCustomers());
                            customToast(context, "数据已提交", R.layout.toast_view);
                        } else {
                            customToast(context, "学号冲突", R.layout.toast_view_e);
                        }
                        // 通知适配器数据已改变
                        customerAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        customToast(context, "新增数据失败，至少输入姓名、地址和电话号！", R.layout.toast_view_e);
                    }
                } catch (Exception e) {
                    Log.e("提交错误", "showDialog " + e.getMessage());
                }
            } else {
                // TODO: 更新数据
                try {
                    Log.i("更新数据", "showDialog " + customerInfoEntity);
                    if (!customerInfoEntity.getCustomerName().isEmpty() &&
                            !customerInfoEntity.getAddress().isEmpty() &&
                            !customerInfoEntity.getPhone().isEmpty()) { // 至少输入姓名、地址和电话号

                        // TODO: 更新数据的数据库操作

                        // 通知适配器数据已改变
                        customerAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        customToast(context, "修改数据失败，至少输入姓名、地址和电话号！", R.layout.toast_view_e);
                    }
                } catch (Exception e) {
                    Log.e("提交错误", "showDialog " + e.getMessage());
                }
            }
        });
    }

    private void reset(String msg,Boolean showToast) { // 刷新页面的方法
        customerList.clear();
        customerList.addAll(customerInfoDao.getAllCustomers());
        customerAdapter.notifyDataSetChanged();
        if(showToast)
            customToast(requireContext(),msg, R.layout.toast_view);
    }

}

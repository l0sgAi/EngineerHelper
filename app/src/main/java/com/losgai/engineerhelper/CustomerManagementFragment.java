package com.losgai.engineerhelper;

import static com.losgai.engineerhelper.helper.GeneralHelper.customToast;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.losgai.engineerhelper.dao.ProductInfoDao;
import com.losgai.engineerhelper.entity.CustomerInfoEntity;

import java.util.List;
import java.util.Objects;

// 客户管理Fragment
public class CustomerManagementFragment extends Fragment {

    private CustomerInfoDao customerInfoDao; // 客户信息DAO
    private ProductInfoDao productInfoDao; // 产品信息DAO
    private ListView customerListView; // 客户信息列表视图
    private CustomerAdapter customerAdapter; // 客户信息适配器
    private List<CustomerInfoEntity> customerList; // 客户信息列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_management, container, false);
        try {
            // 获取所有客户信息
            customerInfoDao = new CustomerInfoDao(getContext());
            customerInfoDao.open();
            productInfoDao = new ProductInfoDao(getContext());
            productInfoDao.open();
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

        // 长按列表项的监听器，弹出选项菜单
        customerListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            CustomerInfoEntity customerInfoEntity = customerList.get(position);
            showOperationDialog(customerInfoEntity);
            return true;
        });

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
                    // 条件查询客户信息
                    query(customerAdapter);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_customer_refresh) {
                    reset("客户数据已刷新", true);
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

        if (customerInfoEntity != null) {
            // 如果是更新，设置默认值
            name.setText(customerInfoEntity.getCustomerName());
            address.setText(customerInfoEntity.getAddress());
            phone.setText(customerInfoEntity.getPhone());
            email.setText(customerInfoEntity.getEmail());
            buttonSubmit.setText("更新");
        }

        // 弹出新增对话框
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
                try {
                    Log.i("更新数据", "showDialog " + customerInfoEntity);
                    // 更新数据
                    if (!name.getText().toString().isEmpty() &&
                            !address.getText().toString().isEmpty() &&
                            !phone.getText().toString().isEmpty()) { // 至少输入姓名、地址和电话号

                        // 更新数据的数据库操作
                        customerInfoEntity.setCustomerName(name.getText().toString());
                        customerInfoEntity.setAddress(address.getText().toString());
                        customerInfoEntity.setPhone(phone.getText().toString());
                        customerInfoEntity.setEmail(email.getText().toString());
                        customerInfoDao.updateCustomer(customerInfoEntity);

                        // 刷新页面
                        reset("数据已更新", true);
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

    // 查询操作，包括弹出对话框和查询逻辑
    private void query(CustomerAdapter customerAdapter) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.search_layout_customer, null);
        builder.setView(dialogView);

        // 获取对应组件
        EditText name = dialogView.findViewById(R.id.search_customer_name_or_id);
        EditText phone = dialogView.findViewById(R.id.search_customer_phone);
        EditText email = dialogView.findViewById(R.id.search_customer_email);
        Button buttonSearch = dialogView.findViewById(R.id.searchBtn_customer);

        // 弹出搜索对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 搜索数据
        buttonSearch.setOnClickListener(v -> {
            // 获取输入的查询条件
            String nameStr = name.getText().toString();
            String phoneStr = phone.getText().toString();
            String emailStr = email.getText().toString();

            if (nameStr.isEmpty() && phoneStr.isEmpty() && emailStr.isEmpty()) {
                customToast(context, "至少输入一个查询条件！", R.layout.toast_view_e);
                return;
            }

            // 获取构造的自定义查询结果
            List<CustomerInfoEntity> customersByNamePhoneEmail =
                    customerInfoDao.getCustomersByNamePhoneEmail(nameStr, phoneStr, emailStr);

            // 更新适配器数据
            customerList.clear();
            customerList.addAll(customersByNamePhoneEmail);
            customerAdapter.notifyDataSetChanged();
            customToast(context, "查询成功，找到" +
                    customersByNamePhoneEmail.size() +
                    "条数据", R.layout.toast_view);
            // 关闭对话框
            dialog.dismiss();
        });
    }

    // 弹出长按的操作对话框
    private void showOperationDialog(CustomerInfoEntity customerInfoEntity) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.operation_select_customer, null);
        builder.setView(dialogView);

        // 获取对应组件
        Button buttonUpdate = dialogView.findViewById(R.id.update_btn_customer);
        Button buttonDelete = dialogView.findViewById(R.id.delete_btn_customer);

        // 弹出操作对话框
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // 更新数据
        buttonUpdate.setOnClickListener(v -> {
            showAddOrUpdateDialog(customerAdapter, customerInfoEntity);
            alertDialog.dismiss();
        });

        // 删除数据
        buttonDelete.setOnClickListener(v -> {
            // TODO: 删除时要把客户对应的所有产品关联信息删除，即customerId设置为null
            AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
            confirmBuilder.setMessage("确认删除该顾客信息？对应的产品关联关系会一并删除！")
                    .setPositiveButton("确认", (dialog, which) -> {
                        try {
                            // 用户点击确认后，执行删除操作
                            if (customerInfoDao.deleteCustomer(customerInfoEntity.getId()) > 0 &&
                                    productInfoDao.deleteProductByCustomerId(customerInfoEntity.getId()) > 0) {
                                reset("数据已删除", true);
                            } else {
                                customToast(context, "删除数据失败", R.layout.toast_view_e);
                            }
                        } catch (Exception e) {
                            Log.e("删除数据失败", "showOperationDialog " + e.getMessage());
                        }
                    }).setNegativeButton("取消", null); // 用户点击取消，不做任何操作

            confirmBuilder.create().show();
            alertDialog.dismiss();
        });
    }

    // 刷新页面列表数据的方法
    private void reset(String msg, Boolean showToast) {
        customerList.clear();
        customerList.addAll(customerInfoDao.getAllCustomers());
        customerAdapter.notifyDataSetChanged();
        if (showToast)
            customToast(requireContext(), msg, R.layout.toast_view);
    }

}

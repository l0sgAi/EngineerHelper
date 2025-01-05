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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.losgai.engineerhelper.adapter.ProductListAdapter;
import com.losgai.engineerhelper.dao.CustomerInfoDao;
import com.losgai.engineerhelper.dao.ProductInfoDao;
import com.losgai.engineerhelper.entity.ProductEntity;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

// 产品管理Fragment
public class ProductManagementFragment extends Fragment {
    // TODO: 产品管理Fragment的布局文件添加对应的适配器和内部视图

    private ProductInfoDao productInfoDao; // 产品信息数据访问对象
    private CustomerInfoDao customerInfoDao; // 客户信息数据访问对象
    private ListView productListView; // 产品列表视图
    private ProductListAdapter productListAdapter; // 产品列表适配器
    private List<ProductEntity> productEntityList; // 产品实体列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        // TODO: 产品管理Fragment的逻辑
        try {
            // 获取产品列表
            productInfoDao = new ProductInfoDao((getContext()));
            productInfoDao.open();
            productEntityList = productInfoDao.getAllProducts();

            // 绑定列表视图控件
            productListView = view.findViewById(R.id.listViewProduct);

            // 创建适配器
            productListAdapter = new ProductListAdapter(getContext(), R.layout.inner_list_layout_product, productEntityList);
            // 设置适配器
            productListView.setAdapter(productListAdapter);
        } catch (Exception e) {
            Log.e("创建产品列表失败", Objects.requireNonNull(e.getMessage()));
        }

        // 长按列表项的监听器，弹出选项菜单
        productListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            ProductEntity productEntity = productEntityList.get(position);
            showOperationDialog(productEntity);
            return true;
        });
        return view;
    }

    // 菜单栏
    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 获取Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_product);

        // 设置Toolbar作为ActionBar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        // 添加MenuProvider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // 加载菜单资源
                menuInflater.inflate(R.menu.toolbar_product_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_product_add) {
                    // 添加产品
                    showAddOrUpdateDialog(productListAdapter, null);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_product_search) {
                    // 条件查询产品信息
                    query(productListAdapter);
                    return true;
                } else if (menuItem.getItemId() == R.id.menu_product_refresh) {
                    // 重置产品列表
                    reset("数据已重置", true);
                    return true;
                } else {
                    return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    // 显示新增或更新对话框
    private void showAddOrUpdateDialog(ProductListAdapter productListAdapter,
                                       ProductEntity productEntity) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.product_add_or_update, null);
        builder.setView(dialogView);

        // 获取对应组件
        EditText productName = dialogView.findViewById(R.id.product_add_or_update_name);
        DatePicker productDate = dialogView.findViewById(R.id.product_add_or_update_date);
        EditText productCustomerID = dialogView.findViewById(R.id.product_add_or_update_customer_id);
        Button buttonSubmit = dialogView.findViewById(R.id.submit_product_add_or_update);

        if (productEntity != null) {
            productName.setText(productEntity.getProductName());
            productDate.updateDate(productEntity.getPurchaseTime().getYear(), productEntity.getPurchaseTime().getMonth(), productEntity.getPurchaseTime().getDay());
            productCustomerID.setText(String.valueOf(productEntity.getCustomerId()));
            buttonSubmit.setText("更新");
        }

        // 弹出新增或更新对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSubmit.setOnClickListener(v -> {
            if (productEntity == null) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(productDate.getYear(), productDate.getMonth(), productDate.getDayOfMonth());
                    // 创建新的产品对象
                    ProductEntity data = new ProductEntity(
                            productName.getText().toString(),
                            calendar.getTime(),
                            Long.parseLong(productCustomerID.getText().toString())
                    );

                    customerInfoDao = new CustomerInfoDao(context);
                    customerInfoDao.open();
                    if (!customerInfoDao.isCustomerExist(data.getCustomerId())) {
                        customToast(context, "客户不存在，请先添加客户", R.layout.toast_view_e);
                        return;
                    }
                    customerInfoDao.close();

                    Log.i("新增产品", "showAddOrUpdateDialog: " + data.getProductName());

                    // 新增产品
                    if (!data.getProductName().isEmpty()) {
                        if (productInfoDao.addProduct(data) > 0) {
                            productEntityList.clear();
                            productEntityList.addAll(productInfoDao.getAllProducts());
                            customToast(context, "数据已提交", R.layout.toast_view);
                        } else {
                            customToast(context, "新增产品失败", R.layout.toast_view_e);
                        }
                        productListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        customToast(context, "新增产品失败，至少输入产品的名称！", R.layout.toast_view_e);
                    }

                } catch (Exception e) {
                    Log.e("新增产品失败", "showAddOrUpdateDialog: " + e.getMessage());
                }

            } else {
                try {
                    Log.i("更新产品", "showAddOrUpdateDialog: " + productEntity.getId());
                    // 更新产品
                    if (!productName.getText().toString().isEmpty() && !productCustomerID.getText().toString().isEmpty()) {
                        productEntity.setProductName(productName.getText().toString());

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(productDate.getYear(), productDate.getMonth(), productDate.getDayOfMonth());
                        productEntity.setPurchaseTime(calendar.getTime());

                        productEntity.setCustomerId(Long.parseLong(productCustomerID.getText().toString()));

                        productInfoDao.updateProduct(productEntity);
                        reset("数据已更新", true);
                        dialog.dismiss();

                    } else {
                        customToast(context, "请填写完整信息", R.layout.toast_view_e);
                    }
                } catch (Exception e) {
                    Log.e("更新产品失败", "showAddOrUpdateDialog: " + e.getMessage());
                }
            }
        });
    }

    private void query(ProductListAdapter productListAdapter) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.search_layout_product, null);
        builder.setView(dialogView);

        // 获取对应组件
        EditText productId = dialogView.findViewById(R.id.search_product_id);
        EditText productName = dialogView.findViewById(R.id.search_product_name);
        EditText customerID = dialogView.findViewById(R.id.search_product_costumer_id);
        Button buttonSearch = dialogView.findViewById(R.id.searchBtn_product);

        // 弹出搜索对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 搜索数据
        buttonSearch.setOnClickListener(v -> {
            String idStr = productId.getText().toString();
            String nameStr = productName.getText().toString();
            String customerIdStr = customerID.getText().toString();

            if (idStr.isEmpty() && nameStr.isEmpty() && customerIdStr.isEmpty()) {
                customToast(context, "至少输入一个查询条件！", R.layout.toast_view_e);
                return;
            }

            // 获取构造的自定义查询结果
            List<ProductEntity> productByName = productInfoDao.getProductByName(nameStr, customerIdStr);

            // 更新适配器
            productEntityList.clear();
            productEntityList.addAll(productByName);
            productListAdapter.notifyDataSetChanged();

            customToast(context, "查询成功", R.layout.toast_view);
            dialog.dismiss();
        });
    }

    // 弹出长按的操作对话框
    private void showOperationDialog(ProductEntity productEntity) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.operation_select_product, null);
        builder.setView(dialogView);

        // 获取对应组件
        Button buttonUpdate = dialogView.findViewById(R.id.update_btn_product);
        Button buttonDelete = dialogView.findViewById(R.id.delete_btn_product);

        // 弹出操作对话框
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // 更新数据
        buttonUpdate.setOnClickListener(v -> {
            showAddOrUpdateDialog(productListAdapter, productEntity);
            alertDialog.dismiss();
        });

        // 删除数据
        buttonDelete.setOnClickListener(v -> {
            AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
            confirmBuilder.setMessage("确定删除该产品吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        try {
                            if (productInfoDao.deleteProduct(productEntity.getId()) > 0) {
                                reset("数据已删除", true);
                            } else {
                                customToast(context, "删除失败", R.layout.toast_view_e);
                            }
                        } catch (Exception e) {
                            Log.e("删除产品失败", "showOperationDialog: " + e.getMessage());
                        }
                    }).setNegativeButton("取消", null);
            confirmBuilder.create().show();
            alertDialog.dismiss();
        });
    }

    public void reset(String msg, Boolean showTest) {
        productEntityList.clear();
        productEntityList.addAll(productInfoDao.getAllProducts());
        productListAdapter.notifyDataSetChanged();
        if (showTest) {
            customToast(requireContext(), msg, R.layout.toast_view);
        }
    }
}

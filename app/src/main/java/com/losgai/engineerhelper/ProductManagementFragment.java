package com.losgai.engineerhelper;

import static com.losgai.engineerhelper.helper.DateUtil.createDateFromInput;
import static com.losgai.engineerhelper.helper.DateUtil.dateToStringArray;
import static com.losgai.engineerhelper.helper.GeneralHelper.customToast;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.losgai.engineerhelper.adapter.CustomerAdapter;
import com.losgai.engineerhelper.adapter.ProductListAdapter;
import com.losgai.engineerhelper.dao.CustomerInfoDao;
import com.losgai.engineerhelper.dao.ProductInfoDao;
import com.losgai.engineerhelper.entity.CustomerInfoEntity;
import com.losgai.engineerhelper.entity.ProductEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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
        EditText inputYear = dialogView.findViewById(R.id.inputYear);
        EditText inputMonth = dialogView.findViewById(R.id.inputMonth);
        EditText inputDay = dialogView.findViewById(R.id.inputDay);

        Spinner productCustomer = dialogView.findViewById(R.id.spinner_customer);
        Button buttonSubmit = dialogView.findViewById(R.id.submit_product_add_or_update);

        // 客户信息列表
        customerInfoDao = new CustomerInfoDao(context);
        customerInfoDao.open();
        List<CustomerInfoEntity> customerInfoEntityList = customerInfoDao.getAllCustomers();
        customerInfoDao.close();
        CustomerAdapter customerAdapter = new CustomerAdapter(context, R.layout.inner_list_layout_customer, customerInfoEntityList);

        // 设置下拉列表样式
        customerAdapter.setDropDownViewResource(R.layout.customer_dropdown_item);
        // 设置适配器
        productCustomer.setAdapter(customerAdapter);

        if (productEntity != null) { // 更新操作初始化
            productName.setText(productEntity.getProductName());
            // 将 Date 转换为字符串
            try{
                String[] formattedDate = dateToStringArray(productEntity.getPurchaseTime());
                String year = formattedDate[0];  // 年
                String month = formattedDate[1]; // 月
                String day = formattedDate[2];   // 日

                // 填充到输入框
                inputYear.setText(year);
                inputMonth.setText(month);
                inputDay.setText(day);

                // 显示月、日输入框
                inputMonth.setVisibility(View.VISIBLE);
                inputDay.setVisibility(View.VISIBLE);
            }catch (Exception e){
                Log.e("日期转换失败", Objects.requireNonNull(e.getMessage()));
            }

            // 更新时，为下拉列表设置对应的值
            long defaultCustomerId = productEntity.getCustomerId(); // 默认选中id

            // 流处理查找索引
            int defaultIndex = IntStream.range(0, customerInfoEntityList.size())
                    .filter(i -> customerInfoEntityList.get(i).getId() == defaultCustomerId)
                    .findFirst()
                    .orElse(-1);

            // 设置默认选中项
            if (defaultIndex != -1) {
                productCustomer.setSelection(defaultIndex);
            }
            buttonSubmit.setText("更新");
        }

        // 弹出新增或更新对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 年份监听
        inputYear.addTextChangedListener(createTextWatcher(inputYear, 4, year -> {
            if (year.length() == 4) {
                int enteredYear = Integer.parseInt(year);
                int currentYear = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentYear = LocalDate.now().getYear();
                }
                if (enteredYear <= currentYear) {
                    inputMonth.requestFocus();
                } else {
                    inputYear.getText().clear();
                    customToast(context, "必须选择之前的日期", R.layout.toast_view_e);
                }
            }
        }));

        // 月份监听
        inputMonth.addTextChangedListener(createTextWatcher(inputMonth, 2, month -> {
            if (month.length() == 2) {
                int enteredMonth = Integer.parseInt(month);
                if (enteredMonth >= 1 && enteredMonth <= 12) {
                    inputDay.requestFocus();
                } else {
                    inputMonth.getText().clear();
                    customToast(context, "必须选择之前的日期", R.layout.toast_view_e);
                }
            }
        }));

        // 日期监听
        inputDay.addTextChangedListener(createTextWatcher(inputDay, 2, day -> {
            String yearStr = inputYear.getText().toString();
            String monthStr = inputMonth.getText().toString();

            if(yearStr.isEmpty() || monthStr.isEmpty()) {
                customToast(context, "请选择年/月", R.layout.toast_view_e);
                return;
            }

            if (day.length() == 2) {
                int enteredDay = Integer.parseInt(day);
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                LocalDate enteredDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enteredDate = LocalDate.of(year, month, 1);
                }
                int maxDay = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    maxDay = enteredDate.lengthOfMonth();
                }

                if (enteredDay >= 1 && enteredDay <= maxDay) {
                    LocalDate fullDate = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        fullDate = LocalDate.of(year, month, enteredDay);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (fullDate.isBefore(LocalDate.now())) {
                            customToast(context, "日期已选择: "
                                    + fullDate, R.layout.toast_view);
                        } else {
                            inputDay.getText().clear();
                            customToast(context,
                                    "必须选择之前的日期!", R.layout.toast_view_e);
                        }
                    }
                }
            }
        }));

        buttonSubmit.setOnClickListener(v -> {
            if (productEntity == null) { // 新增操作
                try {
                    String yearStr = inputYear.getText().toString();
                    String monthStr = inputMonth.getText().toString();
                    String dayStr = inputDay.getText().toString();

                    if(yearStr.isEmpty() || monthStr.isEmpty() || dayStr.isEmpty()) {
                        customToast(context, "请选择日期", R.layout.toast_view_e);
                        return;
                    }

                    // 获取选中的 Customer 对象
                    CustomerInfoEntity selectedCustomer =
                            (CustomerInfoEntity) productCustomer.getSelectedItem();
                    long selectedCustomerId = -1;
                    if (selectedCustomer != null) {
                        // 获取选中对象的 ID
                        selectedCustomerId = selectedCustomer.getId();
                        // 执行相关逻辑
                        Log.d("Spinner", "Selected Customer ID: " + selectedCustomerId);
                    } else {
                        // 未选择任何项目时的处理
                        Log.w("Spinner", "No customer selected!");
                        customToast(context, "请选择客户", R.layout.toast_view_e);
                        return;
                    }

                    // 创建新的产品对象
                    ProductEntity data = new ProductEntity(
                            productName.getText().toString(),
                            createDateFromInput(yearStr, monthStr, dayStr),
                            selectedCustomerId
                    );

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

            } else { // 更新操作
                try {
                    Log.i("更新产品", "showAddOrUpdateDialog: " + productEntity.getId());
                    String yearStr = inputYear.getText().toString();
                    String monthStr = inputMonth.getText().toString();
                    String dayStr = inputDay.getText().toString();
                    // 获取选中的 Customer 对象
                    CustomerInfoEntity selectedCustomer =
                            (CustomerInfoEntity) productCustomer.getSelectedItem();
                    long selectedCustomerId;
                    if (selectedCustomer != null) {
                        // 获取选中对象的 ID
                        selectedCustomerId = selectedCustomer.getId();
                        // 执行相关逻辑
                        Log.d("Spinner", "Selected Customer ID: " + selectedCustomerId);
                    } else {
                        // 未选择任何项目时的处理
                        Log.w("Spinner", "No customer selected!");
                        customToast(context, "请选择客户", R.layout.toast_view_e);
                        return;
                    }

                    // 更新产品
                    if (!productName.getText().toString().isEmpty()) {
                        productEntity.setProductName(productName.getText().toString());
                        productEntity.setPurchaseTime(createDateFromInput(yearStr, monthStr, dayStr));
                        productEntity.setCustomerId(selectedCustomerId);
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
        EditText productName = dialogView.findViewById(R.id.search_product_name);
        Spinner productCustomer = dialogView.findViewById(R.id.spinner_customer_search);
        Button buttonSearch = dialogView.findViewById(R.id.searchBtn_product);

        // 客户信息列表
        customerInfoDao = new CustomerInfoDao(context);
        customerInfoDao.open();
        List<CustomerInfoEntity> customerInfoEntityList = customerInfoDao.getAllCustomers();
        customerInfoDao.close();
        CustomerAdapter customerAdapter = new CustomerAdapter(context, R.layout.inner_list_layout_customer, customerInfoEntityList);

        // 设置下拉列表样式
        customerAdapter.setDropDownViewResource(R.layout.customer_dropdown_item);
        // 设置适配器
        productCustomer.setAdapter(customerAdapter);

        // 添加默认提示项
        CustomerInfoEntity defaultItem = new CustomerInfoEntity();
        defaultItem.setId(-1L); // 使用特殊的 ID 表示未选择
        defaultItem.setCustomerName("请选择客户");
        customerInfoEntityList.add(0, defaultItem); // 添加到列表的首位

        // 弹出搜索对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 搜索数据
        buttonSearch.setOnClickListener(v -> {
            // 获取选中的 Customer 对象
            CustomerInfoEntity selectedCustomer =
                    (CustomerInfoEntity) productCustomer.getSelectedItem();
            String nameStr = productName.getText().toString();

            if (nameStr.isEmpty() && selectedCustomer.getId() == -1) {
                customToast(context, "至少输入一个查询条件！", R.layout.toast_view_e);
                return;
            }

            // 获取构造的自定义查询结果
            List<ProductEntity> productByName = productInfoDao.getProductByName(nameStr, selectedCustomer.getId());

            // 更新适配器
            productEntityList.clear();
            productEntityList.addAll(productByName);
            productListAdapter.notifyDataSetChanged();

            customToast(context,
                    "查询成功，共有" + productByName.size() + "个结果",
                    R.layout.toast_view);
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
                            // TODO: 授权信息要一并删除
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

    // 日期输入框的监听器
    private TextWatcher createTextWatcher(EditText editText, int maxLength, OnValidInputListener listener) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (input.length() == maxLength) {
                    listener.onValidInput(input);
                }
            }
        };
    }

    @FunctionalInterface
    interface OnValidInputListener {
        void onValidInput(String input);
    }
}

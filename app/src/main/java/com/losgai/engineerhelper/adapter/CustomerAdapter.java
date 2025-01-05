package com.losgai.engineerhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.losgai.engineerhelper.R;
import com.losgai.engineerhelper.entity.CustomerInfoEntity;

import java.util.List;

// 用户视图的自定义适配器类
public class CustomerAdapter extends ArrayAdapter<CustomerInfoEntity> {

    private final Context context;
    private final int resource;
    private final List<CustomerInfoEntity> objects;

    public CustomerAdapter(Context context, int resource, List<CustomerInfoEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // 获取对象中的数据项
        CustomerInfoEntity customerInfo = getItem(position);
        if (customerInfo == null) {
            return convertView != null ? convertView : new View(context);
        }

        // 加载布局
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.inner_list_layout_customer, parent, false);

        // 获取内部布局中的控件
        ImageView customerImage = view.findViewById(R.id.imageUrl_inner_customer);
        TextView customerId = view.findViewById(R.id.inner_customer_id);
        TextView customerName = view.findViewById(R.id.inner_customer_name);
        TextView customerAddress = view.findViewById(R.id.inner_customer_address);
        TextView customerPhone = view.findViewById(R.id.inner_customer_phone);
        TextView customerEmail = view.findViewById(R.id.inner_customer_email);

        // 设置具体的文本内容
        customerImage.setImageResource(R.drawable.ic_customer_orange); // 设置默认图像
        customerId.setText("编号： " + customerInfo.getId());
        customerName.setText("客户名： " + customerInfo.getCustomerName());
        customerAddress.setText("地址： " + customerInfo.getAddress());
        customerPhone.setText("电话： " + customerInfo.getPhone());
        customerEmail.setText("邮箱： " + customerInfo.getEmail());

        // 返回视图
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getDropDownView(int position, View view, @NonNull ViewGroup parent) {
        // 获取对象中的数据项
        CustomerInfoEntity customerInfo = getItem(position);
        if (customerInfo == null) {
            return view != null ? view : new View(context);
        }

        // 下拉项显示逻辑
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.customer_dropdown_item, parent, false);
        }

        CustomerInfoEntity customer = getItem(position);
        if (customer != null) {
            // 获取内部布局中的控件
            TextView customerId = view.findViewById(R.id.inner_customer_id_dropdown);
            TextView customerName = view.findViewById(R.id.inner_customer_name_dropdown);
            TextView customerAddress = view.findViewById(R.id.inner_customer_address_dropdown);
            TextView customerPhone = view.findViewById(R.id.inner_customer_phone_dropdown);
            TextView customerEmail = view.findViewById(R.id.inner_customer_email_dropdown);

            // 设置具体的文本内容
            customerId.setText("编号： " + customerInfo.getId());
            customerName.setText("客户名： " + customerInfo.getCustomerName());
            customerAddress.setText("地址： " + customerInfo.getAddress());
            customerPhone.setText("电话： " + customerInfo.getPhone());
            customerEmail.setText("邮箱： " + customerInfo.getEmail());
        }
        return view;
    }
}

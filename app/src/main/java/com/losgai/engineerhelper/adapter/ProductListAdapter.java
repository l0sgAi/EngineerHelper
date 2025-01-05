package com.losgai.engineerhelper.adapter;

import static com.losgai.engineerhelper.helper.DateUtil.dateToString;

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
import com.losgai.engineerhelper.entity.ProductEntity;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ProductEntity> {

    private final Context context;
    private final int resource;
    private final List<ProductEntity> objects;

    public ProductListAdapter(Context context, int resource, List<ProductEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ProductEntity product = getItem(position);
        if (product == null) {
            return convertView != null ? convertView : new View(context);
        }

        // 加载布局
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.inner_list_layout_product, parent, false);

        // 获取内部布局中的控件
        ImageView productImage = view.findViewById(R.id.imageUrl_inner_product);
        TextView productId = view.findViewById(R.id.inner_product_id);
        TextView productName = view.findViewById(R.id.inner_product_name);
        TextView productDate = view.findViewById(R.id.inner_product_date);
        TextView productCustomId = view.findViewById(R.id.inner_product_customer_id);

        // 设置具体的文本内容
        productImage.setImageResource(R.drawable.ic_product_2); // 设置默认图像
        productId.setText("编号： " + product.getId());
        productName.setText("产品名： " + product.getProductName());
        productDate.setText("购买时间： " + dateToString(product.getPurchaseTime()));
        productCustomId.setText("所属客户ID： " + product.getCustomerId());

        return view;
    }
}

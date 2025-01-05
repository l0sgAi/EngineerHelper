package com.losgai.engineerhelper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.losgai.engineerhelper.entity.ProductEntity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductInfoDao {
    private static final String TABLE_NAME = "product_info";
    private static final String COLUMN_ID = "id"; // 序列号
    private static final String COLUMN_NAME = "product_name"; // 产品名称
    private static final String COLUMN_DATE = "purchase_time"; // 创建时间
    private static final String COLUMN_CUSTOMER_ID = "customer_id"; // 客户ID

    private final DatabaseHelperProduct dbHelper;
    private SQLiteDatabase database;

    public ProductInfoDao(Context context) {
        dbHelper = new DatabaseHelperProduct(context);
    }

    // 打开数据库
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        if (!isTableExists(TABLE_NAME)) {
            createProductInfoTable();
            if (isDatabaseEmpty()) {
                insertInitialProductData();
            }
        }

    }

    // 关闭数据库
    public void close() {
        dbHelper.close();
    }

    // 插入数据
    public long addProduct(ProductEntity product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getProductName());
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(product.getPurchaseTime()));
        values.put(COLUMN_CUSTOMER_ID, product.getCustomerId());
        return database.insert(TABLE_NAME, null, values);
    }

    // 更新数据
    public int updateProduct(ProductEntity product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getProductName());
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(product.getPurchaseTime()));
        values.put(COLUMN_CUSTOMER_ID, product.getCustomerId());
        return database.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }

    // 删除数据
    public int deleteProduct(long id) {
        return database.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // 根据客户ID删除产品
    public int deleteProductByCustomerId(long customerId) {
        return database.delete(TABLE_NAME, COLUMN_CUSTOMER_ID + " = ?",
                new String[]{String.valueOf(customerId)});
    }

    // 查询所有产品
    public List<ProductEntity> getAllProducts() {
        List<ProductEntity> products = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ProductEntity product = new ProductEntity();
                product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));

                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                if (date != null) {
                    Date purchaseTime = Date.valueOf(date);
                    product.setPurchaseTime(purchaseTime);
                }

                product.setCustomerId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)));
                products.add(product);
            }
            cursor.close();
        }
        return products;
    }

    // 按产品名称查询
    public List<ProductEntity> getProductByName(String name, String productId, String customerId) {
        List<ProductEntity> products = new ArrayList<>();
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (!name.isEmpty()) {
            selection += COLUMN_NAME + " LIKE ? ";
            selectionArgs.add("%" + name + "%");
        }

        if (!productId.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += COLUMN_ID + " = ? ";
            selectionArgs.add(productId);
        }

        if (!customerId.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += COLUMN_CUSTOMER_ID + " = ? ";
            selectionArgs.add(customerId);
        }

        return getProductsByCondition(selection, selectionArgs.toArray(new String[0]));
    }

    // 通用查询条件
    private List<ProductEntity> getProductsByCondition(String selection, String[] selectionArgs) {
        List<ProductEntity> products = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ProductEntity product = new ProductEntity();
                product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));

                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                Date purchaseTime = Date.valueOf(date);
                product.setPurchaseTime(purchaseTime);

                product.setCustomerId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)));
                products.add(product);
            }
            cursor.close();
        }
        return products;
    }

    // 检查数据库表是否为空
    private boolean isDatabaseEmpty() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        return isEmpty;
    }

    // 插入产品数据
    private void insertProductData(SQLiteDatabase db,
                                   String name,
                                   String date,
                                   long customerId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CUSTOMER_ID, customerId);
        db.insert(TABLE_NAME, null, values);
    }

    // 检查表是否存在
    private boolean isTableExists(String tableName) {
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 创建 product_info 表
    private void createProductInfoTable() {
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_CUSTOMER_ID + " INTEGER NOT NULL);";
        database.execSQL(createTableSQL);
    }

    // 插入初始产品数据
    private void insertInitialProductData() {
        insertProductData(database, "产品1", "2024-12-01", 1);
        insertProductData(database, "产品2", "2024-12-15", 2);
        insertProductData(database, "产品3", "2025-01-03", 3);
    }

    // 产品信息表的内部helper类
    public static class DatabaseHelperProduct extends SQLiteOpenHelper {
        private static final String DB_NAME = "product_info.db";
        private static final int DB_VERSION = 1;

        public DatabaseHelperProduct(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_CUSTOMER_ID + " INTEGER NOT NULL);";
            db.execSQL(createTableSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

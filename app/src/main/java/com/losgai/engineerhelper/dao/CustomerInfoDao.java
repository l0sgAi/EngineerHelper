package com.losgai.engineerhelper.dao;

import static com.losgai.engineerhelper.helper.GeneralHelper.DB_NAME;
import static com.losgai.engineerhelper.helper.GeneralHelper.DB_VERSION;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.losgai.engineerhelper.entity.CustomerInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class CustomerInfoDao {

    private static final String TABLE_NAME = "customer_info";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "customer_name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";

    private final DatabaseHelperCustomer dbHelper;
    private SQLiteDatabase database;

    public CustomerInfoDao(Context context) {
        dbHelper = new DatabaseHelperCustomer(context);
    }

    // 打开数据库
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        // 检查数据库中是否有 customer_info 表，如果没有则创建
        if (!isTableExists(TABLE_NAME)) {
            createCustomerInfoTable();
        }

        // 检查数据库中是否有数据，如果没有则插入初始3个客户数据
        if (isDatabaseEmpty()) {
           insertInitialCustomerData();
        }
    }

    // 关闭数据库
    public void close() {
        dbHelper.close();
    }

    // 插入数据
    public long addCustomer(CustomerInfoEntity customer) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, customer.getCustomerName());
        values.put(COLUMN_ADDRESS, customer.getAddress());
        values.put(COLUMN_PHONE, customer.getPhone());
        values.put(COLUMN_EMAIL, customer.getEmail());
        return database.insert(TABLE_NAME, null, values);
    }

    // 更新数据
    public int updateCustomer(CustomerInfoEntity customer) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, customer.getCustomerName());
        values.put(COLUMN_ADDRESS, customer.getAddress());
        values.put(COLUMN_PHONE, customer.getPhone());
        values.put(COLUMN_EMAIL, customer.getEmail());
        return database.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(customer.getId())});
    }

    // 删除数据
    public int deleteCustomer(long id) {
        return database.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // 查询所有数据
    public List<CustomerInfoEntity> getAllCustomers() {
        List<CustomerInfoEntity> customers = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CustomerInfoEntity customer = new CustomerInfoEntity();
                customer.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                customer.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                customer.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                customer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                customer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                customers.add(customer);
            }
            cursor.close();
        }
        return customers;
    }

    // 按用户名或id查询
    public List<CustomerInfoEntity> getCustomersByNameOrId(String name, long id) {
        return getCustomersByCondition(COLUMN_NAME + " LIKE ? OR"
                + COLUMN_ID + " = ?", new String[]{"%" + name + "%", String.valueOf(id)});
    }

    // 按电话查询
    public List<CustomerInfoEntity> getCustomersByPhone(String phone, String email) {
        return getCustomersByCondition(COLUMN_PHONE + " LIKE ?",
                new String[]{"%" + phone + "%"});
    }

    // 按邮箱查询
    public List<CustomerInfoEntity> getCustomersByEmail(String email) {
        return getCustomersByCondition(COLUMN_EMAIL + " LIKE ?",
                new String[]{"%" + email + "%"});
    }

    // 通用条件查询
    private List<CustomerInfoEntity> getCustomersByCondition(String selection, String[] selectionArgs) {
        List<CustomerInfoEntity> customers = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CustomerInfoEntity customer = new CustomerInfoEntity();
                customer.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                customer.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                customer.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                customer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                customer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                customers.add(customer);
            }
            cursor.close();
        }
        return customers;
    }

    // 检查数据库表是否为空
    private boolean isDatabaseEmpty() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        return isEmpty;
    }

    private void insertCustomerData(SQLiteDatabase db,
                                    String name,
                                    String address,
                                    String phone,
                                    String email) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_NAME, null, values);
    }

    // 检查表是否存在
    private boolean isTableExists(String tableName) {
        Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 创建 customer_info 表
    private void createCustomerInfoTable() {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_EMAIL + " TEXT)";
        database.execSQL(CREATE_TABLE);
    }

    private void insertInitialCustomerData() {
        insertCustomerData(database, "张三", "镇江市 京口区 江苏大学",
                "1234567890", "zhangsan@qq.com");
        insertCustomerData(database, "李四", "扬州市 邗江区 扬州大学",
                "0987654321", "lisi@gmail.com");
        insertCustomerData(database, "王五", "南京市 浦口区 南京工业大学",
                "5678901234", "wangwu@foxmail.com");
    }


    // 顾客信息表的内部helper类，onCreate只在初次建立数据库时调用
    private static class DatabaseHelperCustomer extends SQLiteOpenHelper {

        public DatabaseHelperCustomer(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 创建客户信息表
            String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_EMAIL + " TEXT)";
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

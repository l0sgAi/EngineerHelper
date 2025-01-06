package com.losgai.engineerhelper.dao;

import static com.losgai.engineerhelper.helper.DateUtil.dateToString;
import static com.losgai.engineerhelper.helper.GeneralHelper.DB_NAME;
import static com.losgai.engineerhelper.helper.GeneralHelper.DB_VERSION;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.losgai.engineerhelper.entity.AuthInfoEntity;
import com.losgai.engineerhelper.helper.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuthInfoDao {
    private static final String TABLE_NAME = "auth_info";
    private static final String COLUMN_ID = "id"; // 序列号
    private static final String COLUMN_EXPIRE_DATE = "expire_date"; // 过期时间
    private static final String COLUMN_AUTH_CODE = "auth_code"; // 授权码
    private static final String COLUMN_PRODUCT_ID = "product_id"; // 客户ID

    private final DatabaseHelperProduct dbHelper;
    private SQLiteDatabase database;

    public AuthInfoDao(Context context) {
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
    public long addAuthInfo(AuthInfoEntity authInfoEntity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPIRE_DATE, dateToString(authInfoEntity.getExpireDate()));
        values.put(COLUMN_AUTH_CODE, authInfoEntity.getAuthCode());
        values.put(COLUMN_PRODUCT_ID, authInfoEntity.getProductId());
        return database.insert(TABLE_NAME, null, values);
    }

    // 更新数据
    public int updateAuthInfo(AuthInfoEntity authInfoEntity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPIRE_DATE, dateToString(authInfoEntity.getExpireDate()));
        values.put(COLUMN_AUTH_CODE, authInfoEntity.getAuthCode());
        values.put(COLUMN_PRODUCT_ID, authInfoEntity.getProductId());
        return database.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(authInfoEntity.getId())});
    }

    // 删除数据
    public int deleteAuthInfo(long id) {
        return database.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // 根据产品id删除产品授权
    public int deleteProductByProductId(long productId) {
        return database.delete(TABLE_NAME, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});
    }

    // 按产品id查询
    public List<AuthInfoEntity> getAuthInfoByProductId(long productId) {
        String selection = "";
        List<String> selectionArgs = new ArrayList<>();

        if (productId > 0) { // 如果产品ID大于0则说明选中一个商品
            selection += COLUMN_PRODUCT_ID + " = ? ";
            selectionArgs.add(String.valueOf(productId));
        }

        return getProductsByCondition(selection, selectionArgs.toArray(new String[0]));
    }

    // 通用查询条件
    private List<AuthInfoEntity> getProductsByCondition(String selection, String[] selectionArgs) {
        List<AuthInfoEntity> authInfoList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, selection, selectionArgs,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AuthInfoEntity authInfo = new AuthInfoEntity();
                authInfo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                authInfo.setAuthCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTH_CODE)));

                // 设置过期时间，如果格式错误会报错
                Date expireDate;
                try {
                    // 使用线程安全的 SimpleDateFormat 解析日期
                    SimpleDateFormat dateFormat = DateUtil.dateFormat.get();
                    if (dateFormat == null) {
                        Log.e("AuthInfoDao_查询过期信息", "日dateFormat为空");
                        return authInfoList;
                    }
                    expireDate = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRE_DATE)));
                    authInfo.setExpireDate(expireDate);
                } catch (ParseException e) {
                    Log.e("AuthInfoDao_查询过期信息",
                            "日期转换失败" +
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTH_CODE)));
                }

                authInfo.setProductId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                authInfoList.add(authInfo);
            }
            cursor.close();
        }
        return authInfoList;
    }

    // 检查数据库表是否为空
    private boolean isDatabaseEmpty() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        return isEmpty;
    }

    // 插入产品数据
    private void insertAuthData(SQLiteDatabase db,
                                String date,
                                String authCode,
                                long productId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPIRE_DATE, date);
        values.put(COLUMN_AUTH_CODE, authCode);
        values.put(COLUMN_PRODUCT_ID, productId);
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
                COLUMN_EXPIRE_DATE + " TEXT, " +
                COLUMN_AUTH_CODE + " TEXT, " +
                COLUMN_PRODUCT_ID + " INTEGER);";
        database.execSQL(createTableSQL);
    }

    // 插入初始产品数据
    private void insertInitialProductData() {
        // 使用线程安全的 SimpleDateFormat 解析日期
        SimpleDateFormat dateFormat = DateUtil.dateFormat.get();
        assert dateFormat != null;
        insertAuthData(database, dateFormat.format(new Date()), "sample", 1);
        insertAuthData(database, dateFormat.format(new Date()), "sample", 2);
        insertAuthData(database, dateFormat.format(new Date()), "sample", 3);
    }

    // 产品信息表的内部helper类
    public static class DatabaseHelperProduct extends SQLiteOpenHelper {

        public DatabaseHelperProduct(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPIRE_DATE + " TEXT NOT NULL, " +
                    COLUMN_AUTH_CODE + " TEXT NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL);";
            db.execSQL(createTableSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

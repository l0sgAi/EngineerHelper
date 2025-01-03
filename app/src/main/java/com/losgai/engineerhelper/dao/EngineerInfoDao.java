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

import com.losgai.engineerhelper.entity.EngineerInfoEntity;
import com.losgai.engineerhelper.helper.GeneralHelper;

// *** 工程师数据管理类 ***

public class EngineerInfoDao {

    public static final String TAG = "EngineerInfoDao";
    public static final String TABLE_NAME = "engineer_info";  // 表名
    public static final String ID = "id";  // 主键ID
    public static final String USERNAME = "username";  // 用户名
    public static final String PASSWORD = "password";  // 密码

    // 内部帮助类和数据库对象
    private final DatabaseHelperEngineer dbHelper;
    private SQLiteDatabase database;

    public EngineerInfoDao(Context context) {
        dbHelper = new DatabaseHelperEngineer(context);
    }

    // 打开数据库
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        // 检查数据库中是否有数据，如果没有则插入管理员账户
        if (isDatabaseEmpty()) {
            insertDefaultAdmin();
        }
    }

    // 关闭数据库
    public void close() {
        dbHelper.close();
    }

    // 插入一条数据
    public long insertEngineer(EngineerInfoEntity engineer) {
        ContentValues values = new ContentValues();
        values.put(USERNAME, engineer.getUsername());
        values.put(PASSWORD, engineer.getPassword());
        return database.insert(TABLE_NAME, null, values);
    }

    // 根据ID删除数据
    public void deleteEngineer(long id) {
        database.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 更新用户信息
    public int updateEngineer(long id, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        return database.update(TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 根据ID查询用户
    public Cursor queryEngineerById(long id) {
        return database.query(TABLE_NAME, null, ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // 根据用户名查询用户
    public Cursor queryEngineerByUserName(String name) {
        return database.query(TABLE_NAME, null, USERNAME + " = ?", new String[]{name}, null, null, null);
    }

    // 查询所有用户
    public Cursor queryAllEngineers() {
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // 检查数据库表是否为空
    private boolean isDatabaseEmpty() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        return isEmpty;
    }

    // 插入默认管理员账户
    private void insertDefaultAdmin() {
        // 插入默认的admin账户
        ContentValues values = new ContentValues();
        values.put(USERNAME, "admin");
        String passwordEncrypted = GeneralHelper.sha256Encrypt("123456");
        if (passwordEncrypted == null) {
            Log.e("加密失败", "密码加密失败");
        } else {
            values.put(PASSWORD, passwordEncrypted);
            database.insert(TABLE_NAME, null, values);
            Log.d(TAG, "Inserted default admin user.");
        }

    }

    // 工程师信息表的内部helper类
    private static class DatabaseHelperEngineer extends SQLiteOpenHelper {

        public DatabaseHelperEngineer(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USERNAME + " TEXT NOT NULL, " +
                    PASSWORD + " TEXT NOT NULL);";
            db.execSQL(createTableSQL);
            Log.i(TAG, "数据库 " + TABLE_NAME + " 已经创建.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

package com.losgai.engineerhelper.helper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.losgai.engineerhelper.R;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class GeneralHelper { // 帮助类

    /**
     * SHA-256加密密码
     *
     * @param password 输入的密码原文
     */
    public static String sha256Encrypt(String password) {
        try {
            // 获取SHA-256加密算法的MessageDigest实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();

            // 转换成16进制
            for (byte b : result) {
                int value = b & 0xFF;  // 将byte转换为无符号整数
                String hexStr = Integer.toHexString(value);
                Log.d("INFO", hexStr);  // 打印输出16进制的字符串

                // 如果16进制长度为1，补充前导零
                if (hexStr.length() == 1) {
                    stringBuilder.append("0").append(hexStr);
                } else {
                    stringBuilder.append(hexStr);
                }
            }

            return stringBuilder.toString();
        } catch (GeneralSecurityException e) {
            Log.e("INFO", "加密失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 显示自定义Toast
     *
     * @param context   上下文
     * @param textInput 要显示的文本
     * @param background 自定义背景的布局ID
     *                   其中R.layout.toast_view为正确提示，
     *                   R.layout.toast_view_e为错误提示
     */
    public static void customToast(Context context, String textInput, int background) {
        // 获取布局填充器
        LayoutInflater inflater = LayoutInflater.from(context);

        // 加载自定义的Toast布局
        View layout = inflater.inflate(background, null);

        // 设置文本内容
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(textInput);

        // 创建Toast并设置自定义布局
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // 显示Toast
        toast.show();
    }
}

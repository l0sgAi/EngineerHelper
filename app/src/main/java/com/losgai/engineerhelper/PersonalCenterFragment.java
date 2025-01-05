package com.losgai.engineerhelper;

import static com.losgai.engineerhelper.helper.GeneralHelper.customToast;
import static com.losgai.engineerhelper.helper.GeneralHelper.sha256Encrypt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.losgai.engineerhelper.dao.EngineerInfoDao;

// 个人中心Fragment
public class PersonalCenterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = requireContext();
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        TextView textView = view.findViewById(R.id.username_show); // 用户名显示
        EditText newPassword = view.findViewById(R.id.password_change_edit);// 新密码输入框
        EditText newPasswordAffirm = view.findViewById(R.id.password_change_edit_affirm);// 新密码确认框
        Button changePasswordBth = view.findViewById(R.id.passwordChangeBtn); // 修改密码按钮

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isSave = view.findViewById(R.id.saveAccount_change); // 是否记住新账户按钮
        isSave.setChecked(true); // 默认记住

        // 获取SharedPreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("ENGINEERHELPER_saved_account", Context.MODE_PRIVATE);
        // 填入保存的用户名/密码
        if (sharedPreferences.contains("cur_username_show"))
            textView.setText(sharedPreferences.getString("cur_username_show", ""));

        changePasswordBth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPasswordStr = newPassword.getText().toString(); // 新密码
                String newPasswordAffirmStr = newPasswordAffirm.getText().toString(); // 新密码确认
                if (newPasswordStr.equals(newPasswordAffirmStr)) {
                    // 获取当前用户名
                    String curUsername = sharedPreferences.getString("cur_username", "");
                    // 修改密码
                    if (sharedPreferences.contains("cur_id")) {
                        try {
                            EngineerInfoDao engineerInfoDao = new EngineerInfoDao(context);
                            engineerInfoDao.open();
                            engineerInfoDao.updateEngineer(sharedPreferences.getLong("cur_id", 0),
                                    curUsername,
                                    sha256Encrypt(newPasswordStr));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (isSave.isChecked()) {
                                // 记住用户名/密码
                                editor.putString("cur_username", newPasswordStr);
                                editor.putString("cur_password", newPasswordAffirmStr);
                            } else { // 清除原来保存的密码
                                editor.remove("cur_password");
                            }
                            editor.apply();
                            customToast(context, "修改密码成功", R.layout.toast_view);
                        } catch (Exception e) {
                            customToast(context, "修改密码失败", R.layout.toast_view_e);
                        }
                    } else {
                        customToast(context, "未成功获取当前用户", R.layout.toast_view_e);
                    }
                } else {
                    customToast(context, "两次输入的密码不一致", R.layout.toast_view_e);
                }
            }
        });

        return view;
    }
}

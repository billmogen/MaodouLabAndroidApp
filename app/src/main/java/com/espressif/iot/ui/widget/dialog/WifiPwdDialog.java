package com.espressif.iot.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.espressif.iot.R;

/**
 * Created by billmogen on 2016/12/8.
 */


public class WifiPwdDialog extends Dialog {
    private Button cancelButton;
    private Button okButton;
    private EditText pswEdit;
    private OnCustomDialogListener customDialogListener;

    public WifiPwdDialog(Context context, OnCustomDialogListener customListener) {
        super(context);
        customDialogListener = customListener;

    }

    /** * 定义dialog的回调事件 */
    public interface OnCustomDialogListener {
        void back(String str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect_dialog);
        setTitle("请输入密码");
        pswEdit = (EditText) findViewById(R.id.wifiDialogPsd);
        //cancelButton = (Button) findViewById(R.id.wifiDialogCancel);
        //okButton = (Button) findViewById(R.id.wifiDialogCertain);
        //cancelButton.setOnClickListener(buttonDialogListener);
        //okButton.setOnClickListener(buttonDialogListener);

    }

    private View.OnClickListener buttonDialogListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.wifiDialogPsd) {
                pswEdit = null;
                customDialogListener.back(null);
                cancel();// 自动调用dismiss();
            } else {
                customDialogListener.back(pswEdit.getText().toString());
                dismiss();
            }
        }
    };
}
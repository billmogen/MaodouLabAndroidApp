package com.espressif.iot.ui.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.espressif.iot.R;

import static android.R.attr.id;

/**
 * Created by billmogen on 2016/12/9.
 */

public class WifiConnectDialogFragment extends DialogFragment{

    private Button cancelButton;
    private Button okButton;
    private EditText mPassword;
    private  WifiPasswdInputCompleteListener mListener;
    public interface WifiPasswdInputCompleteListener
    {
         void wifiPasswdInputComplete(String password);
    }
   @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewTest = inflater.inflate(R.layout.activity_wifi_connect_dialog, null);
        mPassword = (EditText) viewTest.findViewById(R.id.wifiDialogPsd);
        //cancelButton = (Button) view.findViewById(R.id.wifiDialogCancel);
        //okButton = (Button) view.findViewById(R.id.wifiDialogCertain);
        builder.setView(viewTest)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int id)
                    {
                        WifiPasswdInputCompleteListener listener = (WifiPasswdInputCompleteListener) getParentFragment();
                        String st = mPassword.getText().toString();
                        listener.wifiPasswdInputComplete(st);
                    }
                }).setNegativeButton("取消", null);
        return builder.create();
    }
/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (WifiPasswdInputCompleteListener) activity;
        } catch (ClassCastException e) {
            dismiss();
        }
    }
*/
}

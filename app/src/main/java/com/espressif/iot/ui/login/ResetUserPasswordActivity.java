package com.espressif.iot.ui.login;

import com.espressif.iot.R;
import com.espressif.iot.base.net.wifi.WifiAdmin;
import com.espressif.iot.type.user.EspLoginResult;
import com.espressif.iot.type.user.EspRegisterResult;
import com.espressif.iot.type.user.EspResetPasswordResult;
import com.espressif.iot.ui.main.EspActivityAbs;
import com.espressif.iot.user.IEspUser;
import com.espressif.iot.user.builder.BEspUser;
import com.espressif.iot.util.AccountUtil;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.exception.WilddogAuthException;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetUserPasswordActivity extends EspActivityAbs implements OnClickListener
{
    private IEspUser mUser;
    
    private EditText mEmailET;
    private Button mConfirmBtn;

    private int errorCodeResetPassword = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.reset_password_activity);
        
        mUser = BEspUser.getBuilder().getInstance();
        setTitle(getString(R.string.esp_reset_password));
        
        mEmailET = (EditText)findViewById(R.id.resetpwd_email);
        mEmailET.addTextChangedListener(mEmailWatcher);
        
        mConfirmBtn = (Button)findViewById(R.id.resetpwd_confirm);
        mConfirmBtn.setOnClickListener(this);
        mConfirmBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mConfirmBtn)
        {
            // Hide the soft keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEmailET.getWindowToken(), 0);
            new ResetPasswordTask(this).execute(mEmailET.getText().toString());
        }
    }
    
    private TextWatcher mEmailWatcher = new TextWatcher()
    {
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }
        
        @Override
        public void afterTextChanged(Editable s)
        {
            // Check Email address format is legal
            if (AccountUtil.isEmail(s.toString()))
            {
                mConfirmBtn.setEnabled(true);
            }
            else
            {
                mConfirmBtn.setEnabled(false);
            }
        }
    };
    
    private class ResetPasswordTask extends AsyncTask<String, Void, EspResetPasswordResult>
    {
        private Activity mActivity;
        
        private ProgressDialog mProgressDialog;
        
        public ResetPasswordTask(Activity activity)
        {
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute()
        {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(mActivity.getString(R.string.esp_reset_password_progress));
            mProgressDialog.show();
        }
        
        @Override
        protected EspResetPasswordResult doInBackground(String... params)
        {
            String email = params[0];
            return doActionRestPasswordWithWilddog(email);
        }
        
        @Override
        protected void onPostExecute(EspResetPasswordResult result)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            
            switch (result)
            {
                case SUC:
                    Toast.makeText(mActivity, R.string.esp_reset_password_result_completed, Toast.LENGTH_LONG).show();
                    break;
                case EMAIL_NOT_EXIST:
                    Toast.makeText(mActivity, R.string.esp_reset_password_result_email_not_exist, Toast.LENGTH_SHORT)
                        .show();
                    break;
                case FAILED:
                    Toast.makeText(mActivity, R.string.esp_reset_password_result_failed, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public EspResetPasswordResult doActionRestPasswordWithWilddog(String email)
    {

        WifiAdmin wifiAdmin = WifiAdmin.getInstance();
        if (!wifiAdmin.isNetworkAvailable())
        {
            return EspResetPasswordResult.FAILED;
        }

        WilddogAuth mAuth = WilddogAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()){
                    errorCodeResetPassword = 200;
                } else {
                    String errorCodeStr = ((WilddogAuthException)task.getException()).getErrorCode();
                    if (errorCodeStr.equals("invalid_user"))
                    {
                        errorCodeResetPassword = 404;
                    } else {
                        errorCodeResetPassword = 500;
                    }

                    Log.d("wilddog", "resetEmail+ " + task.getException().toString());
                }
            }
        });
        while(errorCodeResetPassword == 0)
        {

        }
        return  EspResetPasswordResult.getResetPasswordResult(errorCodeResetPassword);
    }
}

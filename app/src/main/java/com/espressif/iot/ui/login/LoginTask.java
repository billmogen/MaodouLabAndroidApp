package com.espressif.iot.ui.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.espressif.iot.R;
import com.espressif.iot.type.user.EspLoginResult;
import com.espressif.iot.type.user.EspThirdPartyLoginPlat;

public abstract class LoginTask extends AsyncTask<Integer, Void, EspLoginResult>
{
    private Context mContext;
    
    private ProgressDialog mDialog;
    
    public LoginTask(Context context)
    {
        mContext = context;
    }
    
    @Override
    protected void onPreExecute()
    {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(mContext.getString(R.string.esp_login_progress_message));
        mDialog.setCancelable(false);
        mDialog.show();
    }
    
    @Override
    protected EspLoginResult doInBackground(Integer... params)
    {
        return doLoginTest();
    }
    
    @Override
    protected void onPostExecute(EspLoginResult result)
    {
        mDialog.dismiss();
        mDialog = null;
        
        int msgRes = 0;
        switch (result)
        {
            case SUC:
                msgRes = R.string.esp_login_result_success;
                break;
            case NETWORK_UNACCESSIBLE:
                msgRes = R.string.esp_login_result_network_unaccessible;
                break;
            case PASSWORD_ERR:
                msgRes = R.string.esp_login_result_password_error;
                break;
            case NOT_REGISTER:
                msgRes = R.string.esp_login_result_not_register;
                break;
            default :
                msgRes = R.string.esp_login_result_failed;
                break;
        }
        
        Toast.makeText(mContext, msgRes, Toast.LENGTH_LONG).show();
        
        loginResult(result);
    }
    
    public abstract EspLoginResult doLoginTest();
    
    public void loginResult(EspLoginResult result)
    {
    }
}

package com.espressif.iot.ui.login;

import com.espressif.iot.R;
import com.espressif.iot.base.net.wifi.WifiAdmin;
import com.espressif.iot.type.user.EspLoginResult;
import com.espressif.iot.ui.login.LoginThirdPartyDialog.OnLoginListener;
import com.espressif.iot.ui.main.EspMainActivity;
import com.espressif.iot.ui.register.RegisterActivity;
import com.espressif.iot.user.IEspUser;
import com.espressif.iot.user.builder.BEspUser;
import com.espressif.iot.util.AccountUtil;
import com.espressif.iot.util.EspStrings;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.WilddogAuthError;
import com.wilddog.wilddogauth.core.exception.WilddogAuthException;
import com.wilddog.wilddogauth.core.exception.WilddogException;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;

import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.EditText;

import static com.espressif.iot.command.user.IEspCommandUserLogin.URL;

public class LoginActivity extends Activity implements OnClickListener, OnEditorActionListener {
    private IEspUser mUser;

    private EditText mEmailEdt;
    private EditText mPasswordEdt;

    private Button mLoginBtn;
    private Button mRegisterBtn;
    private TextView mForgetPwdTV;
    //private TextView mThirdPartyLoginTV;
    private WilddogAuth mAuth;
    private final static int REQUEST_REGISTER = 1;

    WilddogOptions options = new WilddogOptions.Builder().setSyncUrl(URL).build();

    //private LoginThirdPartyDialog mThirdPartyLoginDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();
        mUser = BEspUser.getBuilder().getInstance();


        setContentView(R.layout.login_activity);
        init();
    }

    private void init()
    {
        mEmailEdt = (EditText)findViewById(R.id.login_edt_account);
        mPasswordEdt = (EditText)findViewById(R.id.login_edt_password);
        mPasswordEdt.setOnEditorActionListener(this);
        
        mLoginBtn = (Button)findViewById(R.id.login_btn_login);
        mLoginBtn.setOnClickListener(this);
        
        mRegisterBtn = (Button)findViewById(R.id.login_btn_register);
        mRegisterBtn.setOnClickListener(this);
        
        //mThirdPartyLoginTV = (TextView)findViewById(R.id.login_text_third_party);
        //mThirdPartyLoginTV.setOnClickListener(this);
        
        mForgetPwdTV = (TextView)findViewById(R.id.forget_password_text);
        mForgetPwdTV.setOnClickListener(this);
        
        //mThirdPartyLoginDialog = new LoginThirdPartyDialog(this);
        //mThirdPartyLoginDialog.setOnLoginListener(mThirdPartyLoginListener);

    }
    
    @Override
    public void onClick(View v)
    {
        if (v == mLoginBtn)
        {
            login();
        }
        else if (v == mRegisterBtn)
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, REQUEST_REGISTER);
        }
        else if (v == mForgetPwdTV)
        {
            startActivity(new Intent(this, ResetUserPasswordActivity.class));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == mPasswordEdt) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_REGISTER)
        {
            if (resultCode == RESULT_OK)
            {
                // Register completed, set the new account email and password
                String email = data.getStringExtra(EspStrings.Key.REGISTER_NAME_EMAIL);
                String password = data.getStringExtra(EspStrings.Key.REGISTER_NAME_PASSWORD);
                mEmailEdt.setText(email);
                mPasswordEdt.setText(password);
            }
        }
    }
    
    private void login()
    {
        final String account = mEmailEdt.getText().toString();
        final int accountType = AccountUtil.getAccountType(account);
        if (accountType == AccountUtil.TYPE_NONE)
        {
            // Account id is illegal
            Toast.makeText(this, R.string.esp_login_email_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        final String password = mPasswordEdt.getText().toString();
        if (TextUtils.isEmpty(password))
        {
            // The password can't be empty
            Toast.makeText(this, R.string.esp_login_password_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        new LoginTask(this)
        {
            @Override
            public EspLoginResult doLoginTest()
            {
                if (accountType == AccountUtil.TYPE_EMAIL)
                {
                    // Login with Email

                    return doLoginWilddogWithEmail(account,password);
                    //return mUser.doActionUserLoginInternet(account, password);
                }
                else if (accountType == AccountUtil.TYPE_PHONE)
                {
                    // Login with Phone number
                    return mUser.doActionUserLoginPhone(account, password);
                }
                
                return null;
            }
            
            @Override
            public void loginResult(EspLoginResult result)
            {
                if (result == EspLoginResult.SUC)
                {
                    loginSuccess();
                }
            }
        }.execute(1);
    }
    
    private OnLoginListener mThirdPartyLoginListener = new OnLoginListener()
    {
        
        @Override
        public void onLoginComplete(EspLoginResult result)
        {
            if (result == EspLoginResult.SUC)
            {
                loginSuccess();
            }
        }
    };
    
    private void loginSuccess() {
        //setResult(RESULT_OK);
        //finish();
        Intent mainIntent = new Intent(this, EspMainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public EspLoginResult doLoginWilddogWithEmail(String userName, String password)
    {
        WifiAdmin wifiAdmin = WifiAdmin.getInstance();
        if (!wifiAdmin.isNetworkAvailable())
        {
            return EspLoginResult.NETWORK_UNACCESSIBLE;
        }


        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {


                if (!task.isSuccessful()) {

                    if (task.getException().getClass().getSimpleName().equals("WilddogAuthException")) {
                        String errorCode = ((WilddogAuthException) task.getException()).getErrorCode();

                        Log.d("Wilddog", "errorCode" + errorCode);
                    }


                } else {
                    Log.d("Wilddog", "signInWithEmail:onComplete:" + task.isSuccessful());
                    //Toast.makeText(LoginActivity.this, "Login Sucess!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        WilddogUser wUser = mAuth.getInstance().getCurrentUser();
        if (wUser != null)
        {
            //EspUser user = BEspUser.getBuilder().getInstance();
            mUser.setUserKey(wUser.getUid());
            mUser.setUserId((long)473648);
            mUser.setUserName(wUser.getDisplayName());
            mUser.setUserEmail(wUser.getEmail());
            mUser.saveUserInfoInDB();
            mUser.clearUserDeviceLists();

            return EspLoginResult.SUC;

        } else {
            Toast.makeText(LoginActivity.this, "Pls Login first!", Toast.LENGTH_SHORT).show();
            return EspLoginResult.FAILED;
        }


    }


}

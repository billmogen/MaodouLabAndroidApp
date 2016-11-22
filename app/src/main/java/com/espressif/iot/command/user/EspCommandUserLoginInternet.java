package com.espressif.iot.command.user;

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.espressif.iot.base.api.EspBaseApiUtil;
import com.espressif.iot.type.user.EspLoginResult;
import com.espressif.iot.ui.login.LoginActivity;
import com.espressif.iot.user.IEspUser;
import com.espressif.iot.user.builder.BEspUser;
import com.espressif.iot.util.MyApplication;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class EspCommandUserLoginInternet implements IEspCommandUserLoginInternet
{
    private final static Logger log = Logger.getLogger(EspCommandUserLoginInternet.class);
    
    @Override
    public EspLoginResult doCommandUserLoginInternet(String userEmail, String userPassword)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Email, userEmail);
            jsonObject.put(Password, userPassword);
            jsonObject.put(Remember, 1);
            JSONObject jsonObjectResult = EspBaseApiUtil.Post(URL, jsonObject);


            EspLoginResult result = null;
            if (jsonObjectResult == null)
            {
                log.debug(Thread.currentThread().toString() + "##doCommandUserLoginInternet(userEmail=[" + userEmail
                    + "],userPassword=[" + userPassword + "]): " + EspLoginResult.NETWORK_UNACCESSIBLE);
                return EspLoginResult.NETWORK_UNACCESSIBLE;
            }
            int status = jsonObjectResult.getInt(Status);
            if (status == HttpStatus.SC_OK)
            {
                JSONObject userJSON = jsonObjectResult.getJSONObject(USER);
                long userId = userJSON.getLong(Id);
                String userName = userJSON.getString(User_Name);
                JSONObject keyJSON = jsonObjectResult.getJSONObject(Key);
                String userKey = keyJSON.getString(Token);
                
                IEspUser user = BEspUser.getBuilder().getInstance();
                user.setUserKey(userKey);
                user.setUserId(userId);
                user.setUserName(userName);
                user.setUserEmail(userEmail);
            }
            else
            {
            }
            result = EspLoginResult.getEspLoginResult(status);
            log.debug(Thread.currentThread().toString() + "##doCommandUserLoginInternet(userEmail=[" + userEmail
                + "],userPassword=[" + userPassword + "]): " + result);
            return result;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        log.debug(Thread.currentThread().toString() + "##doCommandUserLoginInternet(userEmail=[" + userEmail
            + "],userPassword=[" + userPassword + "]): " + EspLoginResult.NETWORK_UNACCESSIBLE);
        return EspLoginResult.NETWORK_UNACCESSIBLE;
    }
    public EspLoginResult doCommandUserLoginWilddog(String userEmail, String userPassword)
    {
        /*
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl(URL).build();
        WilddogApp.initializeApp(MyApplication.getContext(), options);
        WilddogAuth  mAuth = WilddogAuth.getInstance();
        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(MyApplication.getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                Log.d("Wilddog", "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w("Wilddog", "signInWithEmail", task.getException());
                    Toast.makeText(MyApplication.getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
        return  EspLoginResult.NETWORK_UNACCESSIBLE;
    }
    
}

package com.espressif.iot.ui.widget.adapter;

/**
 * Created by billmogen on 2016/12/8.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;



import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.R;
import com.espressif.iot.base.net.wifi.WifiAdminTest;
import com.espressif.iot.ui.main.DrawerFragmentSearch;

import java.util.List;

/**
 * Created by billmogen on 2016/10/9.
 */

public class WifiAdaptTest extends BaseAdapter {

    LayoutInflater inflater;
    List<ScanResult> list;
    WifiAdminTest wifiAdminTest;
    private Context context;
    String mSSID;
    public WifiAdaptTest(Context context, List<ScanResult> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        wifiAdminTest = new WifiAdminTest(context);
        View view = null;
        view = inflater.inflate(R.layout.item_wifi_list, null);
        ScanResult scanResult = list.get(position);
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        productName.setText(scanResult.SSID);
        mSSID =  scanResult.SSID ;
        final ImageView deviceState = (ImageView) view.findViewById(R.id.wifi_device_state);

        final Button connectionButton = (Button) view.findViewById(R.id.connection_state);
        connectionButton.setText(R.string.esp_wifi_state_on);
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int wifiItemId = wifiAdminTest.IsConfiguration(mSSID);
                if (connectionButton.getText().toString().equals(R.string.esp_wifi_state_on))
                {

                    if (wifiItemId != -1) {
                        if (wifiAdminTest.ConnectWifi(wifiItemId)) {
                            // 连接已保存密码的WiFi
                            Toast.makeText(context, "正在连接",
                                    Toast.LENGTH_SHORT).show();
                            connectionButton.setText(R.string.esp_wifi_state_off);
                            deviceState.setImageResource(R.drawable.esp_device_status_cloud);

                        }
                    } else {

                        int netId = wifiAdminTest.AddWifiConfig(list, mSSID, "");
                        if (netId != -1) {
                            wifiAdminTest.getConfiguration();// 添加了配置信息，要重新得到配置信息
                            if (wifiAdminTest.ConnectWifi(netId)) {
                                // 连接成功，刷新UI
                                Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show();
                                //updateButton.setVisibility(View.INVISIBLE);

                            }
                        }
                    }
                } else {

                    wifiAdminTest.disConnectionWifi(wifiItemId);
                    connectionButton.setText(R.string.esp_wifi_state_on);
                    deviceState.setImageResource(R.drawable.esp_device_status_offline);

                }


            }
        });


        //判断信号强度，显示对应的指示图标

        /*
        if (Math.abs(scanResult.level) > 100) {
            //imageView.setImageIcon();
            deviceState.setImageResource(R.drawable.esp_device_signal_0);
        }
        */
        return view;
    }

}




package com.espressif.iot.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.espressif.iot.R;
import com.espressif.iot.base.net.wifi.WifiAdminTest;
import com.espressif.iot.ui.widget.adapter.WifiAdaptTest;
import com.espressif.iot.ui.widget.adapter.WifiAdapter;
import com.espressif.iot.ui.widget.dialog.WifiConnectDialogFragment;
import com.espressif.iot.ui.widget.dialog.WifiPwdDialog;

import java.util.ArrayList;
import java.util.List;

public class DrawerFragmentSearch extends android.app.Fragment implements View.OnClickListener,WifiConnectDialogFragment.WifiPasswdInputCompleteListener{

    private RadarView radarView;
    private Switch switchWifi;


    private ArrayList<WifiInfo> wifiArray;
    private WifiAdaptTest wifiInfoAdaptTest;
    private ListView listWifi;
    //private View wifiDivTabBar;
    //private Button updateButton;
    private String wifiPassword = null;

    private WifiManager wifiManager;
    private WifiAdminTest wifiAdmin;
    private EditText mPasswordEdit;
    private List<ScanResult> list;
    private ScanResult mScanResult;


    private StringBuffer sb = new StringBuffer();

    private int wifiDialogFlag = 0;
    public DrawerFragmentSearch() {

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        Log.i("i","  Fragment  执行onAttach");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_fragment_search,container,false);
        radarView = (RadarView) view.findViewById(R.id.radar_view);
        radarView.setOnClickListener(this);



        wifiAdmin = new WifiAdminTest(getActivity());

        listWifi = (ListView) view.findViewById(R.id.wifi_list);

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        switchWifi = (Switch) view.findViewById(R.id.switch_wifi);
        switchWifi.setChecked(wifiManager.isWifiEnabled());

        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                wifiManager.setWifiEnabled(isChecked);
                if(isChecked) {
                    listWifi.setVisibility(View.VISIBLE);

                    new Thread(new refreshWifiThread()).start();
                } else {
                    listWifi.setVisibility(View.GONE);

                }
            }
        });
/*
        listWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String wifiItemSSID = null;

            public void onItemClick(android.widget.AdapterView<?> parent,
                                    android.view.View view, int position, long id) {

                Log.d("Fuck", "BSSID:" + list.get(position).BSSID);

                // 连接WiFi
                wifiItemSSID = list.get(position).SSID;
                int wifiItemId = wifiAdmin.IsConfiguration("\""
                        + list.get(position).SSID + "\"");
                if (wifiItemId != -1) {
                    if (wifiAdmin.ConnectWifi(wifiItemId)) {
                        // 连接已保存密码的WiFi
                        Toast.makeText(getActivity(), "正在连接",
                                Toast.LENGTH_SHORT).show();
                        //updateButton.setVisibility(View.INVISIBLE);

                        new Thread(new refreshWifiThread()).start();
                    }
                } else {
                    // 没有配置好信息，配置
                    //WifiConnectDialogFragment wifiConnectDialogFragment = new WifiConnectDialogFragment();
                    //wifiConnectDialogFragment.show(getFragmentManager(), "passwordDialog");

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View wifiPasswordView = inflater.inflate(R.layout.activity_wifi_connect_dialog,null);
                    mPasswordEdit =(EditText) wifiPasswordView.findViewById(R.id.wifiDialogPsd);

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(wifiPasswordView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                            //wifiDialogFlag = 1;
                                            wifiPassword = mPasswordEdit.getText().toString();
                                }
                            }).setNegativeButton("取消",null).show();

                    if (wifiPassword != null) {
                        int netId = wifiAdmin.AddWifiConfig(list, wifiItemSSID, wifiPassword);
                        if (netId != -1) {
                            wifiAdmin.getConfiguration();// 添加了配置信息，要重新得到配置信息
                            if (wifiAdmin.ConnectWifi(netId)) {
                                // 连接成功，刷新UI
                                Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_SHORT).show();
                                //updateButton.setVisibility(View.INVISIBLE);
                                new Thread(new refreshWifiThread()).start();
                            }
                        } else {
                            Toast.makeText(getActivity(),"密码错误",Toast.LENGTH_SHORT).show();
                            // 网络连接错误
                        }
                    } else {
                    }



                }
            }
        });
                //getAllNetWorkList();
                */
        return view;
    }

    public void wifiPasswdInputComplete(String password) {
        Toast.makeText(getActivity(),password,Toast.LENGTH_SHORT).show();
        wifiPassword = password;
    }

    final Handler refreshWifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getAllNetWorkList();

                    break;

                default:
                    break;
            }
        }
    };

    public class refreshWifiThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                Message message = new Message();
                message.what = 1;
                refreshWifiHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radar_view:
                //radarView.
                radarView.setSearching(true);
                new Thread(new refreshWifiThread()).start();
                break;
            case R.id.wifi_list:
                break;
            default:
                break;
        }

    }

    public void getAllNetWorkList() {

        //wifiArray = new ArrayList<WifiInfo>();
        if (sb != null) {
            sb = new StringBuffer();
        }
        try{
            wifiAdmin.startScan();
        }catch (Exception e){
            e.printStackTrace();
        }

        list = wifiAdmin.getWifiList();
        for (int i = 0; i < list.size(); i++) {
            radarView.addPoint();
        }

        if (list != null) {
            /*
            for (int i = 0; i < list.size(); i++) {
                mScanResult = list.get(i);
                WifiInfo wifiInfo = new WifiInfo(mScanResult.BSSID, mScanResult.SSID, mScanResult.capabilities, mScanResult.level);
                wifiArray.add(wifiInfo);
            }*/

            wifiInfoAdaptTest = new WifiAdaptTest(getActivity().getApplicationContext(),list);

            listWifi.setAdapter(wifiInfoAdaptTest);

            wifiAdmin.getConfiguration();

            radarView.setSearching(false);

        }
    }
}

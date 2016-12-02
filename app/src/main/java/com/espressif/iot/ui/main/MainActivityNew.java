package com.espressif.iot.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.espressif.iot.R;

import com.espressif.iot.ui.main.DrawerFragmentSearch;

import org.w3c.dom.Text;

public  class MainActivityNew extends Activity  implements View.OnClickListener {

    private TextView mTextTopBar;
    private TextView mTextSearch;
    private TextView mTextGuide;
    private TextView mTextPersonal;

    private DrawerFragmentSearch drawerFragmentSearch;
    private EspDrawerFragmentLeft drawerFragmentLeft;

    private FrameLayout mLyContent;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_new);

        fragmentManager = getFragmentManager();
        mTextTopBar = (TextView) findViewById(R.id.txt_topbar);
        mTextSearch = (TextView) findViewById(R.id.main_tab_search);
        mTextGuide = (TextView) findViewById(R.id.main_tab_guide);
        mTextPersonal = (TextView) findViewById(R.id.main_tab_personal);

        mLyContent = (FrameLayout) findViewById(R.id.ly_content);

        mTextSearch.setOnClickListener(this);
        mTextGuide.setOnClickListener(this);
        mTextPersonal.setOnClickListener(this);

        mTextSearch.performClick(); //模拟第一次点击，初始界面

    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        switch (v.getId()) {
            case R.id.main_tab_search:
                setSelected();
                mTextSearch.setSelected(true);
                if (drawerFragmentSearch == null) {
                    drawerFragmentSearch = new DrawerFragmentSearch();
                    fragmentTransaction.add(R.id.ly_content, drawerFragmentSearch);
                } else {
                    fragmentTransaction.replace(R.id.ly_content, drawerFragmentSearch);
                }
                break;
            case R.id.main_tab_guide:
                setSelected();
                mTextGuide.setSelected(true);
                break;
            case R.id.main_tab_personal:
                setSelected();
                mTextPersonal.setSelected(true);
                if (drawerFragmentLeft == null) {
                    drawerFragmentLeft = new EspDrawerFragmentLeft();
                    fragmentTransaction.add(R.id.ly_content, drawerFragmentLeft);
                } else {
                    fragmentTransaction.replace(R.id.ly_content, drawerFragmentLeft);
                }

                break;
            default:
                break;

        }
        fragmentTransaction.commit();

    }

    private void setSelected() {
        mTextSearch.setSelected(false);
        mTextGuide.setSelected(false);
        mTextPersonal.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (drawerFragmentSearch != null) {
            fragmentTransaction.hide(drawerFragmentSearch);
        }
        if (drawerFragmentLeft != null) {
            fragmentTransaction.hide(drawerFragmentLeft);
        }
    }
}

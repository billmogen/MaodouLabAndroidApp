package com.espressif.iot.ui.main;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.espressif.iot.R;

public class DrawerFragmentSearch extends android.app.Fragment implements View.OnClickListener{

    private RadarView radarView;

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


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radar_view:
                radarView.setSearching(true);
                radarView.addPoint();
                break;
            default:
                break;
        }

    }
}

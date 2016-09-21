package com.felkertech.ussenterprise.ui;

import android.app.Activity;
import android.net.wifi.WifiEnterpriseConfig;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.felkertech.ussenterprise.model.Eap;
import com.felkertech.ussenterprise.model.Phase2;

/**
 * This adapter is to allow users to quickly pick their own Phase2 protocol.
 *
 * @author Nick
 * @version 2016.09.06
 */
public class Phase2SpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    int[] phase2Types = new int[] {
            Phase2.AUTHENTICATION_NONE,
            Phase2.AUTHENTICATION_PAP,
            Phase2.AUTHENTICATION_MSCHAP,
            Phase2.AUTHENTICATION_MSCHAPV2,
            Phase2.AUTHENTICATION_GTC,
    };
    private Activity mActivity;

    public Phase2SpinnerAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return phase2Types.length;
    }

    @Override
    public Object getItem(int position) {
        return phase2Types[position];
    }

    @Override
    public long getItemId(int position) {
        return phase2Types[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View spinnerView = convertView;
        if (spinnerView == null) {
            spinnerView = mActivity.getLayoutInflater().inflate(
                    android.R.layout.simple_spinner_dropdown_item, null);
        }
        ((TextView) spinnerView.findViewById(android.R.id.text1)).setText
                (Phase2.AUTHENTICATION_TYPES.get((int) getItemId(position)));
        return spinnerView;
    }
}

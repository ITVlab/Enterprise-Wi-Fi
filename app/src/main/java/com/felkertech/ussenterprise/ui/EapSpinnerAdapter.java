package com.felkertech.ussenterprise.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.felkertech.ussenterprise.model.Eap;

/**
 * This adapter is to allow users to quickly pick their own Eap protocol.
 *
 * @author Nick
 * @version 2016.09.06
 */
public class EapSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    int[] eapTypes = new int[] {
            Eap.METHOD_PEAP,
            Eap.METHOD_TLS,
            Eap.METHOD_TTLS,
            Eap.METHOD_PWD,
            Eap.METHOD_SIM,
            Eap.METHOD_AKA,
    };
    private Activity mActivity;

    public EapSpinnerAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return eapTypes.length;
    }

    @Override
    public Object getItem(int position) {
        return eapTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return eapTypes[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View spinnerView = convertView;
        if (spinnerView == null) {
            spinnerView = mActivity.getLayoutInflater().inflate(
                    android.R.layout.simple_spinner_dropdown_item, null);
        }
        ((TextView) spinnerView.findViewById(android.R.id.text1)).setText
                (Eap.METHOD_NAMES.get((int) getItemId(position)));
        return spinnerView;
    }
}

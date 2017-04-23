package com.felkertech.ussenterprise.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.felkertech.ussenterprise.R;
import com.felkertech.ussenterprise.activities.MainActivity;
import com.felkertech.ussenterprise.model.EnterpriseWifiConnection;
import com.felkertech.ussenterprise.model.SavedWifiDatabase;
import com.felkertech.ussenterprise.ui.EapSpinnerAdapter;
import com.felkertech.ussenterprise.ui.Phase2SpinnerAdapter;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.Wifi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/23/2017.
 *
 * A simpler Wi-Fi setup which primarily just shows your Wi-Fi info as a QR code.
 */

public class PersonalFragment extends Fragment {
    private String TAG = PersonalFragment.class.getSimpleName();

    public PersonalFragment() {
    }

    public void loadSsids() {
        List<String> ssids = getSsids();
        Log.d(TAG, "There are " + ssids.size() + " SSIDs nearby.");
        if (ssids.size() == 1) {
            ((EditText) getView().findViewById(R.id.ssid_edit)).setText(ssids.get(0));
        }
    }

    private TextWatcher qrUpdater = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String ssid = ((EditText) getView().findViewById(R.id.ssid_edit)).getText().toString();
            String password = ((EditText) getView().findViewById(R.id.password)).getText().toString();

            Wifi wifi = new Wifi();
            wifi.setPsk(password);
            wifi.setSsid(ssid);
            wifi.setAuthentication(Wifi.Authentication.WPA); // Let's assume.

            Bitmap b = QRCode.from(wifi.toString()).withSize(640, 640).bitmap();
            ImageView imageView = (ImageView) getView().findViewById(R.id.qr_image);
            imageView.setImageBitmap(b);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View baseLayout = inflater.inflate(R.layout.fragment_house_wifi, null);
        ((EditText) baseLayout.findViewById(R.id.ssid_edit)).addTextChangedListener(qrUpdater);
        ((EditText) baseLayout.findViewById(R.id.password)).addTextChangedListener(qrUpdater);

        baseLayout.findViewById(R.id.ssid_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> ssids = getSsids();
                new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                        .setTitle(R.string.choose_wifi_ssid)
                        .setItems(ssids
                                        .toArray(new CharSequence[ssids.size()]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((EditText) baseLayout.findViewById(R.id.ssid_edit))
                                                .setText(ssids.get(which));
                                    }
                                })
                        .show();
            }
        });

        printSavedWifiNetworks();

        return baseLayout;
    }

    public void printSavedWifiNetworks() {
        final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list != null) {
            for (WifiConfiguration i : list) {
                Log.d(TAG, "Found wifi " + i.SSID);
                Wifid(i.SSID);
            }
        }
    }

    private List<String> getSsids() {
        // Check permission
        List<String> ssids = new ArrayList<>();
        if (!((MainActivity) getActivity()).grantedLocationPermission()) {
            ((MainActivity) getActivity()).requestLocationPermission();
        } else {
            final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult scanResult : scanResults) {
                Log.d(TAG , scanResult.SSID + " " + scanResult.capabilities);
                if (!ssids.contains(scanResult.SSID)) {
                    ssids.add(scanResult.SSID);
                }
            }
        }
        return ssids;
    }

    private void Wifid(String log) {
        TextView logView = ((TextView) getView().findViewById(R.id.wifi_list));
        logView.setText(log + "          " + logView.getText());
    }
}

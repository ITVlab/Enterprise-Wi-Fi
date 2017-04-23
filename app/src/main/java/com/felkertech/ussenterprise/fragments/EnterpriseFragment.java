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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/23/2017.
 */

public class EnterpriseFragment extends Fragment {
    private String TAG = EnterpriseFragment.class.getSimpleName();

    private String ssid = "";
    private String userName = "";
    private String passWord = "";

    private BroadcastReceiver mWifiStateChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            Logd("Receive Wi-Fi update: " + extraWifiState);
            switch (extraWifiState)
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    Logd("Wi-Fi disabled");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Logd("Wi-Fi is being disabled");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    /*ConnectivityManager conMan = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (conMan == null) {
                        Log.e(TAG, "Con-Man is null");
                    }
                    while(conMan.getActiveNetworkInfo() == null || conMan.getActiveNetworkInfo().getState() != NetworkInfo.State.CONNECTED)
                    {

                        conMan = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                        Log.d(TAG, "Not connected yet?");
                        Log.d(TAG, "" + conMan.getActiveNetworkInfo().getState() + " : " + conMan.getActiveNetworkInfo().toString());
                        try
                        {
                            Thread.sleep(500);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }*/
                    final WifiManager wifiManager =
                            (WifiManager) getActivity().getApplicationContext().
                                    getSystemService(Context.WIFI_SERVICE);
                    Logd(wifiManager.getConnectionInfo().getSSID());
                    Logd(wifiManager.getConnectionInfo().toString());
                    Logd(wifiManager.getDhcpInfo().toString());
                    Logd("Wi-Fi is enabled.");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Logd("Wi-Fi is being enabled.");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Logd("Wi-Fi is in an unknown state.");
                    break;
            }
        }
    };

    private BroadcastReceiver mNetworkStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Logd("Network state changed to " + networkInfo.toString());
            }
            String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
            if (bssid != null) {
                Logd("Connected to " + bssid);
            }
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null) {
                Logd("Wi-Fi Info: " + wifiInfo.toString());
            }
        }
    };

    private BroadcastReceiver mSupplicantStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_SUPPLICANT_ERROR);
            boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED,
                    false);
            if (connected) {
                Logd("Supplicant connected? " + connected);
            }
            if (state != null) {
                Logd("Supplicant state? " + state.toString());
            }
        }
    };

    private View.OnFocusChangeListener backgroundColorChanger = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
//                v.setBackgroundColor(getResources().getColor(R.color.background_on_focus));
                v.getBackground().setColorFilter(getResources().getColor(
                        R.color.background_on_focus), PorterDuff.Mode.SRC_ATOP);
            } else {
//                v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                v.getBackground().setColorFilter(getResources().getColor(
                        android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
    };

    public EnterpriseFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) getView().findViewById(R.id.logs)).setText("");
        getActivity().registerReceiver(mWifiStateChangedReceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        getActivity().registerReceiver(mNetworkStateChangedReceiver,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        getActivity().registerReceiver(mSupplicantStateChangeReceiver,
                new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
        getActivity().registerReceiver(mSupplicantStateChangeReceiver,
                new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
        loadSsids();
    }

    public void loadSsids() {
        List<String> enterpriseSsids = getEnterpriseSsids();
        Log.d(TAG, "There are " + enterpriseSsids.size() + " enterprise SSIDs nearby.");
        if (enterpriseSsids.size() == 1) {
            ((EditText) getView().findViewById(R.id.ssid_edit)).setText(enterpriseSsids.get(0));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View baseLayout = inflater.inflate(R.layout.activity_main, null);

        baseLayout.findViewById(R.id.clone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SavedWifiDatabase savedWifiDatabase =
                        SavedWifiDatabase.getInstance(getActivity());
                new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                        .setTitle(R.string.choose_saved_profile)
                        .setItems(savedWifiDatabase.getSavedWifiSsids().toArray(
                                new CharSequence[savedWifiDatabase.getSavedWifiSsids().size()]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EnterpriseWifiConnection connection = savedWifiDatabase
                                                .getSavedWifiConnections().get(which);
                                        ((EditText) baseLayout.findViewById(R.id.ssid_edit))
                                                .setText(connection.getSsid());
                                        ((EditText) baseLayout.findViewById(R.id.identity))
                                                .setText(connection.getIdentity());
                                        ((EditText) baseLayout.findViewById(R.id.password))
                                                .setText(connection.getPassword());
                                        ((Spinner) baseLayout.findViewById(R.id.eap))
                                                .setSelection(connection.getEap());
                                    }
                                })
                        .show();
            }
        });
        baseLayout.findViewById(R.id.ssid_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> enterpriseSsids = getEnterpriseSsids();
                new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                        .setTitle(R.string.choose_wifi_ssid)
                        .setItems(enterpriseSsids
                                        .toArray(new CharSequence[enterpriseSsids.size()]),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((EditText) baseLayout.findViewById(R.id.ssid_edit))
                                                .setText(enterpriseSsids.get(which));
                                    }
                                })
                        .show();
            }
        });

        final int[] eap = new int[1];
        final int[] phase2 = new int[1];

        final EapSpinnerAdapter eapSpinner = new EapSpinnerAdapter(getActivity());
        ((Spinner) baseLayout.findViewById(R.id.eap)).setAdapter(eapSpinner);
        baseLayout.findViewById(R.id.eap).setOnFocusChangeListener(backgroundColorChanger);
        ((Spinner) baseLayout.findViewById(R.id.eap))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        eap[0] = (int) id;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        Phase2SpinnerAdapter phase2Spinner = new Phase2SpinnerAdapter(getActivity());
        ((Spinner) baseLayout.findViewById(R.id.phase2)).setAdapter(phase2Spinner);
        baseLayout.findViewById(R.id.phase2).setOnFocusChangeListener(backgroundColorChanger);
        ((Spinner) baseLayout.findViewById(R.id.phase2))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        phase2[0] = (int) id;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        baseLayout.findViewById(R.id.button_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssid = ((EditText) baseLayout.findViewById(R.id.ssid_edit)).getText().toString();
                userName = ((EditText) baseLayout.findViewById(R.id.identity)).getText().toString();
                passWord = ((EditText) baseLayout.findViewById(R.id.password)).getText().toString();
                try {
                    EnterpriseWifiConnection connection = new EnterpriseWifiConnection.Builder()
                            .setSsid(ssid)
                            .setIdentity(userName)
                            .setPassword(passWord)
                            .setEap(eap[0])
                            .setPhase2(phase2[0])
                            .build();
                    connect(connection);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        baseLayout.findViewById(R.id.button_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssid = ((EditText) baseLayout.findViewById(R.id.ssid_edit)).getText().toString();
                userName = ((EditText) baseLayout.findViewById(R.id.identity)).getText().toString();
                passWord = ((EditText) baseLayout.findViewById(R.id.password)).getText().toString();
                String wifiData = "WIFI:S:" + ssid + ";U:"+ userName + ";P:" + passWord + ";E:" +
                        eap[0] + ";PH:" + phase2[0] + ";;";
                Bitmap b = QRCode.from(wifiData).withSize(640, 640).bitmap();
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageBitmap(b);
                new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
                        R.style.MyDialogTheme))
                        .setView(imageView)
                        .show();
            }
        });
        printSavedWifiNetworks();

        return baseLayout;
    }

    public void connect(EnterpriseWifiConnection connection) {
        WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = connection.getSsid();
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        enterpriseConfig.setIdentity(connection.getIdentity());
        enterpriseConfig.setPassword(connection.getPassword());
        enterpriseConfig.setEapMethod(connection.getEap());
        enterpriseConfig.setPhase2Method(connection.getPhase2());
        wifiConfig.enterpriseConfig = enterpriseConfig;
        Logd("Create connection to '" + ssid + "'");
        SavedWifiDatabase.getInstance(getActivity()).addNetwork(connection);

//        startListeningToLogs();

        addNetwork(wifiConfig);
    }

    public void addNetwork(WifiConfiguration configuration) {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(configuration);
        Logd("Add network '" + configuration.SSID + "' to Wi-Fi Manager");
        rescanAndConnect();
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

    public void rescanAndConnect() {
        final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            Log.d(TAG, "Found wifi " + i.SSID);
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                Logd("Connecting to network");
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // What do we connect to?
                        Logd(wifiManager.getConnectionInfo().getSSID());
                        Logd(wifiManager.getConnectionInfo().toString());
                        Logd(wifiManager.getDhcpInfo().toString());
                        Logd(String.valueOf(wifiManager.getConnectionInfo().getNetworkId()));
                    }
                }, 1000 * 5);*/
                break;
            }
        }
    }

    private List<String> getEnterpriseSsids() {
        // Check permission
        List<String> ssids = new ArrayList<>();
        if (!((MainActivity) getActivity()).grantedLocationPermission()) {
            ((MainActivity) getActivity()).requestLocationPermission();
        } else {
            final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult scanResult : scanResults) {
                Log.d(TAG , scanResult.SSID + " " + scanResult.capabilities);
                if (scanResult.capabilities.contains("EAP") && !ssids.contains(scanResult.SSID)) {
                    ssids.add(scanResult.SSID);
                }
            }
        }
        return ssids;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mWifiStateChangedReceiver);
        getActivity().unregisterReceiver(mNetworkStateChangedReceiver);
        getActivity().unregisterReceiver(mSupplicantStateChangeReceiver);
    }

    private void Logd(String log) {
        TextView logView = ((TextView) getView().findViewById(R.id.logs));
        logView.setText(log + "\n\n" + logView.getText());
    }

    private void Wifid(String log) {
        TextView logView = ((TextView) getView().findViewById(R.id.wifi_list));
        logView.setText(log + "          " + logView.getText());
    }
}

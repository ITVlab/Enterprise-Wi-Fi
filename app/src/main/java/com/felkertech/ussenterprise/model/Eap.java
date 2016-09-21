package com.felkertech.ussenterprise.model;

import android.net.wifi.WifiEnterpriseConfig;
import android.util.SparseArray;

/**
 * Created by Nick on 9/5/2016.
 */

public class Eap {
    public static final int METHOD_PEAP = WifiEnterpriseConfig.Eap.PEAP;
    public static final int METHOD_TLS = WifiEnterpriseConfig.Eap.TLS;
    public static final int METHOD_TTLS = WifiEnterpriseConfig.Eap.TTLS;
    public static final int METHOD_PWD = WifiEnterpriseConfig.Eap.PWD;
    public static final int METHOD_SIM = WifiEnterpriseConfig.Eap.SIM;
    public static final int METHOD_AKA = WifiEnterpriseConfig.Eap.AKA;
    public static final SparseArray<String> METHOD_NAMES;

    static {
        METHOD_NAMES = new SparseArray<>();
        METHOD_NAMES.put(METHOD_PEAP, "PEAP");
        METHOD_NAMES.put(METHOD_TLS, "TLS");
        METHOD_NAMES.put(METHOD_TTLS, "TTLS");
        METHOD_NAMES.put(METHOD_PWD, "PWD");
        METHOD_NAMES.put(METHOD_SIM, "SIM");
        METHOD_NAMES.put(METHOD_AKA, "AKA");
    }
}

package com.felkertech.ussenterprise.model;

import android.net.wifi.WifiEnterpriseConfig;
import android.util.SparseArray;

/**
 * Created by Nick on 9/5/2016.
 */

public class Phase2 {
    public static final int AUTHENTICATION_NONE = WifiEnterpriseConfig.Phase2.NONE;
    public static final int AUTHENTICATION_PAP = WifiEnterpriseConfig.Phase2.PAP;
    public static final int AUTHENTICATION_MSCHAP = WifiEnterpriseConfig.Phase2.MSCHAP;
    public static final int AUTHENTICATION_MSCHAPV2 = WifiEnterpriseConfig.Phase2.MSCHAPV2;
    public static final int AUTHENTICATION_GTC = WifiEnterpriseConfig.Phase2.GTC;
    public static final SparseArray<String> AUTHENTICATION_TYPES;

    static {
        AUTHENTICATION_TYPES = new SparseArray<>();
        AUTHENTICATION_TYPES.put(AUTHENTICATION_NONE, "None");
        AUTHENTICATION_TYPES.put(AUTHENTICATION_PAP, "PAP");
        AUTHENTICATION_TYPES.put(AUTHENTICATION_MSCHAP, "MSCHAP");
        AUTHENTICATION_TYPES.put(AUTHENTICATION_MSCHAPV2, "MSCHAPV2");
        AUTHENTICATION_TYPES.put(AUTHENTICATION_GTC, "GTC");
    }
}

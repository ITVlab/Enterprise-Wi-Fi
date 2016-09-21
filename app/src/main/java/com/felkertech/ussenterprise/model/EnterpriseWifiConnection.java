package com.felkertech.ussenterprise.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nick on 9/5/2016.
 */

public class EnterpriseWifiConnection {
    private static final String KEY_SSID = "ssid";
    private static final String KEY_IDENTITY = "identity";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EAP = "eap";
    private static final String KEY_PHASE2 = "phase2";

    private JSONObject jsonObject;

    private EnterpriseWifiConnection() {
        jsonObject = new JSONObject();
    }

    public String getSsid() {
        try {
            return jsonObject.getString(KEY_SSID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getIdentity() {
        try {
            return jsonObject.getString(KEY_IDENTITY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPassword() {
        try {
            return jsonObject.getString(KEY_PASSWORD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getEap() {
        try {
            return jsonObject.getInt(KEY_EAP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPhase2() {
        try {
            return jsonObject.getInt(KEY_PHASE2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public static class Builder {
        EnterpriseWifiConnection wifiConnection;

        public Builder() {
            wifiConnection = new EnterpriseWifiConnection();
        }

        public Builder(JSONObject jsonObject) {
            wifiConnection = new EnterpriseWifiConnection();
            try {
                setSsid(jsonObject.getString(KEY_SSID));
                setIdentity(jsonObject.getString(KEY_IDENTITY));
                setPassword(jsonObject.getString(KEY_PASSWORD));
                setEap(jsonObject.getInt(KEY_EAP));
                setPhase2(jsonObject.getInt(KEY_PHASE2));
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        public Builder setSsid(String ssid) {
            try {
                wifiConnection.jsonObject.put(KEY_SSID, ssid);
            } catch (JSONException ignored) {
            }
            return this;
        }

        public Builder setIdentity(String identity) {
            try {
                wifiConnection.jsonObject.put(KEY_IDENTITY, identity);
            } catch (JSONException ignored) {
            }
            return this;
        }

        public Builder setPassword(String password) {
            try {
                wifiConnection.jsonObject.put(KEY_PASSWORD, password);
            } catch (JSONException ignored) {
            }
            return this;
        }

        public Builder setEap(int eap) {
            try {
                wifiConnection.jsonObject.put(KEY_EAP, eap);
            } catch (JSONException ignored) {
            }
            return this;
        }

        public Builder setPhase2(int phase2) {
            try {
                wifiConnection.jsonObject.put(KEY_PHASE2, phase2);
            } catch (JSONException ignored) {
            }
            return this;
        }

        public EnterpriseWifiConnection build() {
            if (wifiConnection.getSsid() == null || wifiConnection.getSsid().isEmpty()) {
                throw new IllegalArgumentException("You need an SSID!");
            }
            if (wifiConnection.getIdentity() == null || wifiConnection.getIdentity().isEmpty()) {
                throw new IllegalArgumentException("You need an identity!");
            }
            if (wifiConnection.getPassword() == null || wifiConnection.getPassword().isEmpty()) {
                throw new IllegalArgumentException("You need a password!");
            }
            return wifiConnection;
        }
    }
}

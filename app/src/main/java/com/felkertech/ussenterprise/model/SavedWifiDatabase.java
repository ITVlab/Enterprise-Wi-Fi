package com.felkertech.ussenterprise.model;

import android.content.Context;

import com.felkertech.settingsmanager.SettingsManager;
import com.felkertech.ussenterprise.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A pseudo-database made from a {@link org.json.JSONArray} stored in
 * {@link com.felkertech.settingsmanager.SettingsManager}.
 *
 * @author Nick
 * @version 2016.09.06
 */
public class SavedWifiDatabase {
    private static SettingsManager settingsManager;
    private static SavedWifiDatabase savedWifiDatabase;

    private JSONObject databaseRows;

    public static SavedWifiDatabase getInstance(Context context) {
        if (savedWifiDatabase == null) {
            savedWifiDatabase = new SavedWifiDatabase(context);
        }
        return savedWifiDatabase;
    }

    private SavedWifiDatabase(Context context) {
        settingsManager = new SettingsManager(context);
        try {
            databaseRows = new JSONObject(settingsManager.getString(R.string.sm_database));
        } catch (JSONException e) {
            databaseRows = new JSONObject();
            settingsManager.setString(R.string.sm_database, toString());
        }
    }

    public List<String> getSavedWifiSsids() {
        List<String> ssids = new ArrayList<>();
        for (EnterpriseWifiConnection enterpriseWifiConnection : getSavedWifiConnections()) {
            ssids.add(enterpriseWifiConnection.getSsid());
        }
        return ssids;
    }

    @Override
    public String toString() {
        return databaseRows.toString();
    }

    public List<EnterpriseWifiConnection> getSavedWifiConnections() {
        List<EnterpriseWifiConnection> enterpriseWifiConnections = new ArrayList<>();
        Iterator<String> keys = databaseRows.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject connection = new JSONObject(databaseRows.getString(key));
                enterpriseWifiConnections.add(new EnterpriseWifiConnection.Builder(connection)
                        .build());
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return enterpriseWifiConnections;
    }

    public void addNetwork(EnterpriseWifiConnection enterpriseWifiConnection) {
        try {
            databaseRows.put(enterpriseWifiConnection.getSsid(), enterpriseWifiConnection.toString());
            settingsManager.setString(R.string.sm_database, toString());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void removeNetwork(EnterpriseWifiConnection enterpriseWifiConnection) {
        databaseRows.remove(enterpriseWifiConnection.getSsid());
    }
}

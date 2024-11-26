/*
 * Copyright (C) 2019-2024 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.power.hub.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import com.voltage.support.preferences.SystemPropertySwitchPreference;
import com.power.hub.utils.DeviceUtils;
@SearchIndexable
public class Spoofing extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "Spoofing";
    private static final String KEY_SYSTEM_WIDE_CATEGORY = "spoofing_system_wide_category";
    private static final String KEY_PIXEL_PROPS = "persist.sys.pphooks.enable";

    private static final String KEY_PIF_JSON_FILE_PREFERENCE = "pif_json_file_preference";
    private Preference mPifJsonFilePreference;
    private Handler mHandler;

    private PreferenceCategory mSystemWideCategory;
    private SystemPropertySwitchPreference mPixelProps;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        addPreferencesFromResource(R.xml.spoofing);
        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources resources = context.getResources();
        mSystemWideCategory = (PreferenceCategory) findPreference(KEY_SYSTEM_WIDE_CATEGORY);
        mPixelProps = (SystemPropertySwitchPreference) findPreference(KEY_PIXEL_PROPS);

        mPifJsonFilePreference = findPreference(KEY_PIF_JSON_FILE_PREFERENCE);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mPifJsonFilePreference) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/json");
            startActivityForResult(intent, 10001);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.d(TAG, "URI received: " + uri.toString());
            try (InputStream inputStream = getActivity().getContentResolver().openInputStream(uri)) {
                if (inputStream != null) {
                    String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Log.d(TAG, "JSON data: " + json);
                    JSONObject jsonObject = new JSONObject(json);
                    for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                        String key = it.next();
                        String value = jsonObject.getString(key);
                        Log.d(TAG, "Setting property: persist.sys.pihooks_" + key + " = " + value);
                        SystemProperties.set("persist.sys.pihooks_" + key, value);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading JSON or setting properties", e);
            }
        }
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        return false;
    }
    @Override
    public int getMetricsCategory() {
        return MetricsEvent.VOLTAGE;
    }
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider(R.xml.spoofing) {
            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);
                final Resources resources = context.getResources();
                if (DeviceUtils.isCurrentlySupportedPixel()) {
                    keys.add(KEY_PIXEL_PROPS);
                }
                return keys;
            }
        };
}

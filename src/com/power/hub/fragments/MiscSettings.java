package com.power.hub.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import com.power.hub.fragments.SmartPixels;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.voltage.support.preferences.CustomSeekBarPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import android.content.pm.PackageManager.NameNotFoundException;
import com.android.settings.SettingsPreferenceFragment;
import com.voltage.support.preferences.SystemSettingMasterSwitchPreference;
import com.voltage.support.preferences.SystemSettingListPreference;
import com.voltage.support.preferences.SecureSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String SMART_PIXELS = "smart_pixels";

    private Preference mSmartPixels;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.powerhub_misc);

        final PreferenceScreen prefSet = getPreferenceScreen();

           mSmartPixels = (Preference) findPreference(SMART_PIXELS);
           boolean mSmartPixelsSupported = getResources().getBoolean(
                 com.android.internal.R.bool.config_supportSmartPixels);
           if (!mSmartPixelsSupported)
                 prefSet.removePreference(mSmartPixels);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    }
	

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VOLTAGE;
    }
	
	/**
     * For Search.
     */

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.powerhub_misc) {
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);

                    boolean mSmartPixelsSupported = context.getResources().getBoolean(
                            com.android.internal.R.bool.config_supportSmartPixels);
                    if (!mSmartPixelsSupported)
                        keys.add(SMART_PIXELS);

                    return keys;
                }
    };
}

package com.waifusims.wanicchou;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mProviderListPref;
    private ListPreference mDicTypeListPref;
    private ListPreference mMatchTypeListPref;
    private ListPreference mAutoSaveListPref;
    private ListPreference mAutoDeleteListPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProviderListPref = (ListPreference)  getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_provider_key));
        mDicTypeListPref = (ListPreference) getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_dictionary_type_key));
        mMatchTypeListPref = (ListPreference) getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_match_type_key));
        mAutoSaveListPref = (ListPreference) getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_auto_save_key));
        mAutoDeleteListPref = (ListPreference) getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_auto_delete_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        mProviderListPref.setSummary(mProviderListPref.getEntry().toString());
        mDicTypeListPref.setSummary(mDicTypeListPref.getEntry().toString());
        mMatchTypeListPref.setSummary(mMatchTypeListPref.getEntry().toString());
        mAutoDeleteListPref.setSummary(mAutoDeleteListPref.getEntry().toString());
        mAutoSaveListPref.setSummary(mAutoSaveListPref.getEntry().toString());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_search);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getContext().getString(R.string.pref_dictionary_type_key))){
            mDicTypeListPref.setSummary(mDicTypeListPref.getEntry().toString());
        }
        else if (key.equals(getContext().getString(R.string.pref_match_type_key))){
            mMatchTypeListPref.setSummary(mMatchTypeListPref.getEntry().toString());
        }
        else if (key.equals(getContext().getString(R.string.pref_auto_delete_key))){
            mAutoDeleteListPref.setSummary(mAutoDeleteListPref.getEntry().toString());
        }
        else if (key.equals(getContext().getString(R.string.pref_auto_save_key))){
            mAutoSaveListPref.setSummary(mAutoSaveListPref.getEntry().toString());
        }
        else if(key.equals(getContext().getString(R.string.pref_provider_key))){
            mProviderListPref.setSummary(mProviderListPref.getEntry().toString());
        }
    }
}

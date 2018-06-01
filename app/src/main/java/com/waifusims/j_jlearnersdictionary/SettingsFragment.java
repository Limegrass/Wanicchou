package com.waifusims.j_jlearnersdictionary;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mListPrefDicType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListPrefDicType = (ListPreference) getPreferenceScreen()
                .findPreference(getContext().getString(R.string.pref_dictionary_type_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        mListPrefDicType.setSummary(mListPrefDicType.getEntry().toString());
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
            mListPrefDicType.setSummary(mListPrefDicType.getEntry().toString());
        }

    }
}

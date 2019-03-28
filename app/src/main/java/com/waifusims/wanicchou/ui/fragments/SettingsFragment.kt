package com.waifusims.wanicchou.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.waifusims.wanicchou.R

class SettingsFragment : PreferenceFragmentCompat(),
                         SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var dictionaryListPref: ListPreference
    private lateinit var matchTypeListPref: ListPreference
    private lateinit var autoDeleteListPref: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dictionaryListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_dictionary_key)) as ListPreference
        matchTypeListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_match_type_key)) as ListPreference
        autoDeleteListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_auto_delete_key)) as ListPreference
    }

    override fun onResume() {
        super.onResume()
        dictionaryListPref.summary = dictionaryListPref.entry.toString()
        matchTypeListPref.summary = matchTypeListPref.entry.toString()
        autoDeleteListPref.summary = autoDeleteListPref.entry.toString()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_search, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key){
            context!!.getString(R.string.pref_match_type_key) -> {
                matchTypeListPref.summary = matchTypeListPref.entry.toString()
            }
            context!!.getString(R.string.pref_auto_delete_key) -> {
                autoDeleteListPref.summary = autoDeleteListPref.entry.toString()
            }
            context!!.getString(R.string.pref_dictionary_key) -> {
                dictionaryListPref.summary = dictionaryListPref.entry.toString()
            }

        }
    }
}

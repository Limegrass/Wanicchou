package com.waifusims.wanicchou.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.waifusims.wanicchou.R
import data.room.VocabularyRepository

class SettingsFragment : PreferenceFragmentCompat(),
                         SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var dictionaryListPref: ListPreference
    private lateinit var matchTypeListPref: ListPreference
    private lateinit var autoDeleteListPref: ListPreference

    private lateinit var inputLanguagePreference: ListPreference
    private lateinit var definitionLanguagePreference: ListPreference

    private lateinit var repository : VocabularyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dictionaryListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_dictionary_key)) as ListPreference
        repository = VocabularyRepository.getInstance(activity!!.application)
        matchTypeListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_match_type_key)) as ListPreference
        autoDeleteListPref = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_auto_delete_key)) as ListPreference
        inputLanguagePreference = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_word_language_key)) as ListPreference
        definitionLanguagePreference = preferenceScreen
                .findPreference<ListPreference>(context!!.getString(R.string.pref_definition_language_key)) as ListPreference
    }

    override fun onResume() {
        super.onResume()
        dictionaryListPref.summary = dictionaryListPref.entry.toString()
        matchTypeListPref.summary = matchTypeListPref.entry.toString()
        autoDeleteListPref.summary = autoDeleteListPref.entry.toString()
        inputLanguagePreference.summary = inputLanguagePreference.entry.toString()
        definitionLanguagePreference.summary = definitionLanguagePreference.entry.toString()
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
            context!!.getString(R.string.pref_word_language_key) -> {
                inputLanguagePreference.summary = inputLanguagePreference.entry.toString()
            }
            context!!.getString(R.string.pref_definition_language_key) -> {
                definitionLanguagePreference.summary = definitionLanguagePreference.entry.toString()
            }
        }
    }
}

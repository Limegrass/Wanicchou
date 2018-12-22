//package com.waifusims.wanicchou
//
//
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.support.v7.preference.ListPreference
//import android.support.v7.preference.PreferenceFragmentCompat
//
//class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
////
////    private var mProviderListPref: ListPreference? = null
////    private var mDicTypeListPref: ListPreference? = null
////    private var mMatchTypeListPref: ListPreference? = null
////    private var mAutoSaveListPref: ListPreference? = null
////    private var mAutoDeleteListPref: ListPreference? = null
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////
////        mProviderListPref = preferenceScreen
////                .findPreference(context!!.getString(R.string.pref_title_key)) as ListPreference
////        mDicTypeListPref = preferenceScreen
////                .findPreference(context!!.getString(R.string.pref_dictionary_type_key)) as ListPreference
////        mMatchTypeListPref = preferenceScreen
////                .findPreference(context!!.getString(R.string.pref_match_type_key)) as ListPreference
////        mAutoSaveListPref = preferenceScreen
////                .findPreference(context!!.getString(R.string.pref_auto_save_key)) as ListPreference
////        mAutoDeleteListPref = preferenceScreen
////                .findPreference(context!!.getString(R.string.pref_auto_delete_key)) as ListPreference
////    }
////
////    override fun onResume() {
////        super.onResume()
////        mProviderListPref!!.summary = mProviderListPref!!.entry.toString()
////        mDicTypeListPref!!.summary = mDicTypeListPref!!.entry.toString()
////        mMatchTypeListPref!!.summary = mMatchTypeListPref!!.entry.toString()
////        mAutoDeleteListPref!!.summary = mAutoDeleteListPref!!.entry.toString()
////        mAutoSaveListPref!!.summary = mAutoSaveListPref!!.entry.toString()
////        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
////    }
////
////    override fun onPause() {
////        super.onPause()
////        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
////    }
////
////    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
////        addPreferencesFromResource(R.xml.pref_search)
////    }
////
////    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
////        if (key == context!!.getString(R.string.pref_dictionary_type_key)) {
////            mDicTypeListPref!!.summary = mDicTypeListPref!!.entry.toString()
////        } else if (key == context!!.getString(R.string.pref_match_type_key)) {
////            mMatchTypeListPref!!.summary = mMatchTypeListPref!!.entry.toString()
////        } else if (key == context!!.getString(R.string.pref_auto_delete_key)) {
////            mAutoDeleteListPref!!.summary = mAutoDeleteListPref!!.entry.toString()
////        } else if (key == context!!.getString(R.string.pref_auto_save_key)) {
////            mAutoSaveListPref!!.summary = mAutoSaveListPref!!.entry.toString()
////        } else if (key == context!!.getString(R.string.pref_title_key)) {
////            mProviderListPref!!.summary = mProviderListPref!!.entry.toString()
////        }
////    }
//}

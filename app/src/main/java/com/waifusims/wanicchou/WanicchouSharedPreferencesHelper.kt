package com.waifusims.wanicchou

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

//class WanicchouSharedPreferencesHelper internal constructor(private val context: Context) {
////
////    val sharedPreferences: SharedPreferences
////        get() = PreferenceManager.getDefaultSharedPreferences(context)
////
////    val dictionaryPreference: String
////        get() = getString(R.string.pref_dictionary_type_key,
////                    R.string.pref_dictionary_type_default)!!
////
////    val matchType: String
////        get() = getString(R.string.pref_match_type_key, R.string.pref_match_type_default)!!
////
////    val dictionary: String
////        get() = getString(R.string.pref_dictionary_key, R.string.pref_dictionary_default)!!
////
////    /**
////     * Gets a string from shared preferences. Returns an empty string if it doesn't exist.
////     * @param resKey an integer resource key
////     * @return the string associated with the key
////     */
////    fun getString(resKey: Int): String? {
////        val stringIfMissing = ""
////        val sharedPrefKey = context.getString(resKey)
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
////    }
////
////    fun getString(sharedPrefKey: String): String? {
////        val stringIfMissing = ""
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
////    }
////
////    fun getString(resKey: Int, stringIfMissing: String): String? {
////        val sharedPrefKey = context.getString(resKey)
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
////    }
////
////    fun getString(resKey: Int, missingResKey: Int): String? {
////        val sharedPrefKey = context.getString(resKey)
////        val stringIfMissing = context.getString(missingResKey)
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
////    }
////
////    fun getString(sharedPrefKey: String, stringIfMissing: String): String? {
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
////    }
////
////    fun putString(resKey: Int, stringToSave: String) {
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        val sharedPrefKey = context.getString(resKey)
////        val editor = sharedPreferences.edit()
////        editor.putString(sharedPrefKey, stringToSave)
////        editor.apply()
////    }
////
////    fun putString(sharedPrefKey: String, stringToSave: String) {
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        val editor = sharedPreferences.edit()
////        editor.putString(sharedPrefKey, stringToSave)
////        editor.apply()
////    }
////
////    fun autoDeleteOption(): String? {
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(context.getString(R.string.pref_auto_delete_key),
////                context.getString(R.string.pref_auto_delete_default))
////    }
////
////    fun autoSaveOption(): String? {
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
////        return sharedPreferences.getString(context.getString(R.string.pref_auto_save_key),
////                context.getString(R.string.pref_auto_save_default))
////    }
//
//}

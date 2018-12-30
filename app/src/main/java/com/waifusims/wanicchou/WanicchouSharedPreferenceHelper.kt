package com.waifusims.wanicchou

import android.content.Context
import android.preference.PreferenceManager
import data.enums.AutoDelete
import data.enums.AutoSave
import data.enums.MatchType

class WanicchouSharedPreferenceHelper(val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var matchType: MatchType
        get() {
            val pref = getString(R.string.pref_match_type_key,
                                       R.string.pref_match_type_default)!!
            return MatchType.valueOf(pref)
        }
        set(value) {
            putString(R.string.pref_match_type_key, value.toString())
        }

    var dictionary: String
        get() = getString(R.string.pref_dictionary_key, R.string.pref_dictionary_default)!!
        set(value) {
            putString(R.string.pref_dictionary_key, value)
        }

    var wordLanguageCode: String
        get() = getString(R.string.pref_word_language_key, R.string.pref_word_language_default)!!
        set(value) {
            putString(R.string.pref_word_language_key, value)
        }

    var definitionLanguageCode: String
        get() = getString(R.string.pref_definition_language_key, R.string.pref_definition_language_default)!!
        set(value) {
            putString(R.string.pref_definition_language_key, value)
        }

    var autoSave: AutoSave
        get() {
            val pref = getString(R.string.pref_auto_save_key,
                                 R.string.pref_auto_save_default)!!
            return AutoSave.valueOf(pref)
        }
        set(value) {
            putString(R.string.pref_auto_save_key, value.toString())
        }

    var autoDelete : AutoDelete
        get() {
            val pref = getString(R.string.pref_auto_delete_key,
                    R.string.pref_auto_delete_default)!!
            return AutoDelete.valueOf(pref)

        }
        set(value) {
            putString(R.string.pref_auto_delete_key, value.toString())
        }

    private fun getString(resKey: Int, missingResKey: Int): String? {
        val sharedPrefKey = context.getString(resKey)
        val stringIfMissing = context.getString(missingResKey)
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)
    }

    private fun putString(resKey: Int, stringToSave: String) {
        val sharedPrefKey = context.getString(resKey)
        val editor = sharedPreferences.edit()
        editor.putString(sharedPrefKey, stringToSave)
        editor.apply()
    }

}

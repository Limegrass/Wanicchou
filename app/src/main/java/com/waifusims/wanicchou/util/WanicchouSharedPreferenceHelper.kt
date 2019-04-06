package com.waifusims.wanicchou.util

import android.content.Context
import android.preference.PreferenceManager
import com.waifusims.wanicchou.R
import data.enums.AutoDelete
import data.enums.MatchType

class WanicchouSharedPreferenceHelper(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var databaseMatchType: MatchType
        get() {
            val pref = getString(R.string.pref_database_match_type_key,
                    R.string.pref_match_type_default)
            return MatchType.valueOf(pref)
        }
        set(value) {
            putString(R.string.pref_database_match_type_key, value.toString())
        }

    var dictionaryMatchType: MatchType
        get() {
            val pref = getString(R.string.pref_dictionary_match_type_key,
                    R.string.pref_match_type_default)
            return MatchType.valueOf(pref)
        }
        set(value) {
            putString(R.string.pref_dictionary_match_type_key, value.toString())
        }

    var dictionary: Long
        get() {
            val dictionary = getString(R.string.pref_dictionary_key,
                    R.integer.pref_dictionary_default)
            return dictionary.toLong()
        }
        set(value) {
            putString(R.string.pref_dictionary_key, value.toString())
        }

    var wordLanguageID: Long
        get() = getString(R.string.pref_vocabulary_language_key, R.string.pref_word_language_default).toLong()
        set(value) {
            putString(R.string.pref_vocabulary_language_key, value.toString())
        }

    var definitionLanguageID: Long
        get() = getString(R.string.pref_definition_language_key,
                          R.string.pref_definition_language_default)
                         .toLong()
        set(value) {
            putString(R.string.pref_definition_language_key, value.toString())
        }

    var autoDelete : AutoDelete
        get() {
            val pref = getString(R.string.pref_auto_delete_key,
                    R.string.pref_auto_delete_default)
            return AutoDelete.valueOf(pref)
        }
        set(value) {
            putString(R.string.pref_auto_delete_key, value.toString())
        }

    private fun getString(resKey: Int, missingResKey: Int): String {
        val sharedPrefKey = context.getString(resKey)
        val stringIfMissing = context.getString(missingResKey)
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing)!!
    }

    private fun putString(resKey: Int, stringToSave: String) {
        val sharedPrefKey = context.getString(resKey)
        val editor = sharedPreferences.edit()
        editor.putString(sharedPrefKey, stringToSave)
        editor.apply()
    }

}

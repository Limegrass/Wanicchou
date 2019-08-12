package com.limegrass.wanicchou.ui.fragments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.limegrass.wanicchou.R
import data.arch.search.IDictionarySource
import com.limegrass.wanicchou.enums.AutoDelete
import data.enums.Dictionary
import data.enums.Language
import data.enums.MatchType
import data.web.DictionarySearchProviderFactory

class SettingsFragment : PreferenceFragmentCompat(),
                         SharedPreferences.OnSharedPreferenceChangeListener {
    //<editor-fold desc="Fields">
    private lateinit var application : Application
    private lateinit var fragmentContext : Context
    //<editor-fold desc="ListPreferences">
    // TODO: Consider subclassing ListPreference for the ones with complex operations
    private val dictionaryPreference: ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        val dictionaries = Dictionary.values()
        preference.entries = dictionaries.map{
            it.dictionaryName
        }.toTypedArray()
        preference.entryValues = dictionaries.map {
            it.dictionaryID.toString()
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_dictionary_key)
        preference.title = getString(R.string.dictionary_title)
        preference.summary = preference.entry
        preference.setDefaultValue(data.enums.Dictionary.SANSEIDO.dictionaryID.toString())
        preference
    }

    private val dictionaryMatchTypePreference : ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        val webPage = DictionarySearchProviderFactory(dictionary).get()
        val matchTypes = webPage.supportedMatchTypes
        preference.entries = matchTypes.map{
            val id = fragmentContext.resources.getIdentifier("MATCH_TYPE_${it.name}",
                    "string",
                    fragmentContext.packageName)
            fragmentContext.getString(id)
        }.toTypedArray()
        preference.entryValues = matchTypes.map {
            it.name
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_dictionary_match_type_key)
        preference.title = "Dictionary Match Type"
        preference.summary = preference.entry
        //All dictionaries probably supports some form of equals.
        preference.setDefaultValue(MatchType.WORD_EQUALS.name)
        preference
    }


    private val databaseMatchTypePreference: ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        val matchTypes = MatchType.values()
        preference.entries = matchTypes.map{
            val id = fragmentContext.resources.getIdentifier("MATCH_TYPE_${it.name}",
                    "string",
                    fragmentContext.packageName)
            fragmentContext.getString(id)
        }.toTypedArray()
        preference.entryValues = matchTypes.map {
            it.name
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_database_match_type_key)
        preference.title = getString(R.string.database_match_type_title)
        preference.summary = preference.entry
        preference.setDefaultValue(MatchType.WORD_EQUALS.name)
        preference
    }
    private val autoDeletePreference: ListPreference by lazy {
        val autoDeleteTypes = AutoDelete.values()
        val preference = ListPreference(fragmentContext)
        preference.entries = autoDeleteTypes.map{
            val id = fragmentContext.resources.getIdentifier("AUTO_DELETE_${it.name}",
                                                             "string",
                                                             fragmentContext.packageName)
            fragmentContext.getString(id)
        }.toTypedArray()
        preference.entryValues = autoDeleteTypes.map {
            it.name
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_auto_delete_key)
        preference.title = getString(R.string.auto_delete_title)
        preference.summary = preference.entry
        preference.setDefaultValue(AutoDelete.NEVER.name)
        preference
    }

    private val vocabularyLanguagePreference : ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        setVocabularyLanguageOptions(preference)
        preference.key = fragmentContext.getString(R.string.pref_vocabulary_language_key)
        preference.title = "Vocabulary Language"
        preference.summary = preference.entry
        setToVocabularyLanguageDefault(preference)
        preference
    }
    private val definitionLanguagePreference : ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        setDefinitionLanguagePreference(preference)
        preference.key = fragmentContext.getString(R.string.pref_definition_language_key)
        preference.title = "Definition Language"
        preference.summary = preference.entry
        setToDefinitionLanguageDefault(preference)
        preference
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Helper Properties">
    private val dictionary : Dictionary
        get() {
            val dictionaryID = dictionaryPreference.value.toLong()
            return Dictionary.getDictionary(dictionaryID)
        }
    private val webPage : IDictionarySource
        get() = DictionarySearchProviderFactory(dictionary).get()

    private val vocabularyLanguage : Language
        get() {
            val vocabularyLanguageID = vocabularyLanguagePreference.value.toLong()
            return Language.getLanguage(vocabularyLanguageID)
        }

    private val definitionLanguageID : Long
        get() = definitionLanguagePreference.value.toLong()
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContext = context!!
        application = activity!!.application
        preferenceScreen.addPreference(dictionaryPreference)
        preferenceScreen.addPreference(dictionaryMatchTypePreference)
        preferenceScreen.addPreference(databaseMatchTypePreference)
        preferenceScreen.addPreference(autoDeletePreference)
        preferenceScreen.addPreference(vocabularyLanguagePreference)
        preferenceScreen.addPreference(definitionLanguagePreference)
    }

    override fun onResume() {
        super.onResume()
        dictionaryPreference.summary = dictionaryPreference.entry
        dictionaryMatchTypePreference.summary = dictionaryMatchTypePreference.entry
        databaseMatchTypePreference.summary = databaseMatchTypePreference.entry
        autoDeletePreference.summary = autoDeletePreference.entry
        vocabularyLanguagePreference.summary = vocabularyLanguagePreference.entry
        definitionLanguagePreference.summary = definitionLanguagePreference.entry
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
        when (key) {
            context!!.getString(R.string.pref_dictionary_key) -> {
                setVocabularyLanguageOptions(vocabularyLanguagePreference)
                setDefinitionLanguagePreference(definitionLanguagePreference)
                setToDefinitionLanguageDefault(definitionLanguagePreference)
                setSummary(key)
            }
            context!!.getString(R.string.pref_vocabulary_language_key) -> {
                setDefinitionLanguagePreference(definitionLanguagePreference)
                if(definitionLanguagePreference.findIndexOfValue(definitionLanguageID.toString()) < 0){
                    definitionLanguagePreference.value = dictionary.defaultDefinitionLanguage.languageID.toString()
                }
                setSummary(key)
            }
            else -> setSummary(key)
        }
    }
    //</editor-fold>

    //<editor-fold desc="Helper functions">
    private fun setToVocabularyLanguageDefault(preference : ListPreference) {
        preference.value = dictionary.defaultVocabularyLanguage.languageID.toString()
    }
    private fun setToDefinitionLanguageDefault(preference : ListPreference) {
        preference.value = dictionary.defaultDefinitionLanguage.languageID.toString()
    }
    private fun setVocabularyLanguageOptions(vocabularyLanguagePreference: ListPreference) {
        val translations = webPage.supportedTranslations
        vocabularyLanguagePreference.entries = translations.keys.map{
            it.displayName
        }.distinct().toTypedArray()
        vocabularyLanguagePreference.entryValues = translations.keys.map {
            it.languageID.toString()
        }.distinct().toTypedArray()
    }

    private fun setDefinitionLanguagePreference(definitionLanguagePreference: ListPreference){
        val supportedDefinitionLanguages = (webPage.supportedTranslations[vocabularyLanguage]
                ?: error("Vocabulary language preference not found"))
        definitionLanguagePreference.entries = supportedDefinitionLanguages.map{
            it.displayName
        }.toTypedArray()
        definitionLanguagePreference.entryValues = supportedDefinitionLanguages.map {
            it.languageID.toString()
        }.distinct().toTypedArray()
    }

    private fun setSummary(key : String){
        val preference = findPreference<Preference>(key)
        if(preference is ListPreference){
            preference.summary = preference.entry
        }
    }
    //</editor-fold>
}

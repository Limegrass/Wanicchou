package com.waifusims.wanicchou.ui.fragments


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.util.WanicchouSharedPreferenceHelper
import data.arch.lang.JapaneseVocabulary
import data.enums.AutoDelete
import data.enums.MatchType
import data.room.VocabularyRepository
import data.room.entity.Translation
import data.web.sanseido.SanseidoWebPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsFragment : PreferenceFragmentCompat(),
                         SharedPreferences.OnSharedPreferenceChangeListener {

    private val dictionaryPreference: ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        val dictionaries = repository.dictionaries
        preference.entries = dictionaries.map{
            it.dictionaryName
        }.toTypedArray()
        preference.entryValues = dictionaries.map {
            it.dictionaryID.toString()
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_dictionary_key)
        preference.title = "Dictionary"
        preference.summary = preference.entry
        preference.setDefaultValue(SanseidoWebPage.DICTIONARY_ID)
        preference
    }
    private val matchTypePreference: ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        val matchTypes = repository.matchTypes
        preference.entries = matchTypes.map{
            it.matchTypeName
        }.toTypedArray()
        preference.entryValues = matchTypes.map {
            it.matchTypeName
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_match_type_key)
        preference.title = "Match Type"
        preference.summary = preference.entry
        preference.setDefaultValue(MatchType.WORD_EQUALS.name)
        preference
    }
    private val autoDeletePreference: ListPreference by lazy {
        val autoDeleteTypes = AutoDelete.values()
        val preference = ListPreference(fragmentContext)
        preference.entries = autoDeleteTypes.map{
            it.name
        }.toTypedArray()
        preference.entryValues = autoDeleteTypes.map {
            it.name
        }.toTypedArray()
        preference.key = fragmentContext.getString(R.string.pref_auto_delete_key)
        preference.title = "Auto Delete"
        preference.summary = preference.entry
        preference.setDefaultValue(AutoDelete.NEVER.name)
        preference
    }
    private val sharedPreferenceHelper : WanicchouSharedPreferenceHelper by lazy {
        WanicchouSharedPreferenceHelper(fragmentContext)
    }

    private val vocabularyLanguagePreference : ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        setVocabularyLanguageOptions(preference)
        preference.key = fragmentContext.getString(R.string.pref_vocabulary_language_key)
        preference.title = "Vocabulary Language"
        preference.summary = preference.entry
        preference.setDefaultValue(JapaneseVocabulary.LANGUAGE_ID.toString())
        preference
    }

    private val definitionLanguagePreference : ListPreference by lazy {
        val preference = ListPreference(fragmentContext)
        setDefinitionLanguagePreference(preference)
        preference.key = fragmentContext.getString(R.string.pref_definition_language_key)
        preference.title = "Definition Language"
        preference.summary = preference.entry
        preference.setDefaultValue(JapaneseVocabulary.LANGUAGE_ID.toString())
        preference
    }

    private fun setTranslations(dictionaryID: Long){
        if(translations == null || translations!!.first().dictionaryID != dictionaryID){
            runBlocking(Dispatchers.IO) {
                translations = repository.getDictionaryAvailableTranslations(dictionaryID)
            }
        }
    }

    private fun setVocabularyLanguageOptions(vocabularyLanguagePreference: ListPreference) {
        vocabularyLanguagePreference.entries = translations!!.map{
            t ->
            repository.languages.single { l ->
                l.languageID == t.vocabularyLanguageID
            }.languageName
        }.distinct().toTypedArray()
        vocabularyLanguagePreference.entryValues = translations!!.map {
            it.vocabularyLanguageID.toString()
        }.distinct().toTypedArray()
    }

    private fun setDefinitionLanguagePreference(definitionLanguagePreference: ListPreference){
        definitionLanguagePreference.entries = translations!!.filter{
            it.vocabularyLanguageID == vocabularyLanguagePreference.value.toLong()
        }.map{
            t ->
            repository.languages.single { l ->
                l.languageID == t.definitionLanguageID
            }.languageName
        }.distinct().toTypedArray()
        definitionLanguagePreference.entryValues = translations!!.map {
            it.definitionLanguageID.toString()
        }.distinct().toTypedArray()
    }

    private var translations : List<Translation>? = null

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository.getInstance(activity!!.application)
    }
    private lateinit var application : Application
    private lateinit var fragmentContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContext = context!!
        application = activity!!.application
        val dictionaryID = sharedPreferenceHelper.dictionary
        setTranslations(dictionaryID)
        preferenceScreen.addPreference(dictionaryPreference)
        preferenceScreen.addPreference(matchTypePreference)
        preferenceScreen.addPreference(autoDeletePreference)
        preferenceScreen.addPreference(vocabularyLanguagePreference)
        preferenceScreen.addPreference(definitionLanguagePreference)
    }

    override fun onResume() {
        super.onResume()
        dictionaryPreference.summary = dictionaryPreference.entry
        matchTypePreference.summary = matchTypePreference.entry
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
                val dictionaryID = dictionaryPreference.value.toLong()
                setTranslations(dictionaryID)
                setVocabularyLanguageOptions(vocabularyLanguagePreference)
                setDefinitionLanguagePreference(definitionLanguagePreference)
                setSummary(key)
            }
            context!!.getString(R.string.pref_vocabulary_language_key) -> {
                setDefinitionLanguagePreference(definitionLanguagePreference)
            }
            else -> setSummary(key)
        }
    }
    private fun setSummary(key : String){
        val preference = findPreference<Preference>(key)
        if(preference is ListPreference){
            preference.summary = preference.entry
        }
    }
}

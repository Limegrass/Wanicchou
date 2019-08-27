package com.limegrass.wanicchou.util

import android.content.Context
import android.preference.PreferenceManager
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.enums.AutoDelete
import data.anki.IAnkiDroidConfigIdentifierStorage
import data.arch.models.IDefinition
import data.arch.models.IDictionaryEntry
import data.arch.models.IVocabulary
import data.enums.Dictionary
import data.enums.Language
import data.enums.MatchType
import data.models.Definition
import data.models.DictionaryEntry
import data.models.Vocabulary

class WanicchouSharedPreferences(private val context: Context)
    : IAnkiDroidConfigIdentifierStorage {
    override fun getDeckID(deckName : String) : Long? {
        val ankiDecksKey = context.getString(R.string.pref_anki_decks_key)
        val sharedPreferencesDecks = context.getSharedPreferences(ankiDecksKey, Context.MODE_PRIVATE)
        val deckID = sharedPreferencesDecks.getLong(deckName, -1)
        return if (deckID != -1L) deckID else null
    }

    override fun addDeckID(deckName : String, deckID : Long) {
        val ankiDecksKey = context.getString(R.string.pref_anki_decks_key)
        val decksDb = context.getSharedPreferences(ankiDecksKey, Context.MODE_PRIVATE)
        decksDb.edit().putLong(deckName, deckID).apply()
    }

    override fun getModelID(modelName : String, minimumFieldCount : Int) : Long? {
        val ankiModelsKey = context.getString(R.string.pref_anki_models_key)
        val sharedPreferenceModels = context.getSharedPreferences(ankiModelsKey, Context.MODE_PRIVATE)
        val modelID = sharedPreferenceModels.getLong(modelName, -1)
        return if (modelID != -1L) modelID else null
    }

    override fun addModelID(modelName : String, modelID : Long) {
        val ankiModelsKey = context.getString(R.string.pref_anki_models_key)
        val modelsDb = context.getSharedPreferences(ankiModelsKey, Context.MODE_PRIVATE)
        modelsDb.edit().putLong(modelName, modelID).apply()
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //TODO: I can model the last searched as a string as well.
    //TODO: Add a shared preferences for searchProvider priority. This can be modelled as a delimited

    var lastDictionaryEntry : IDictionaryEntry
        get() = DictionaryEntry(vocabulary, definition)
        set(value) {
            vocabulary = value.vocabulary
            definition = value.definitions
        }

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

    var dictionary: Dictionary
        get() {
            val dictionaryID = getString(R.string.pref_dictionary_key,
                    R.integer.pref_dictionary_default).toLong()

            return Dictionary.getDictionary(dictionaryID)
        }
        set(value) {
            putString(R.string.pref_dictionary_key, value.dictionaryID.toString())
        }

    var vocabularyLanguage : Language
        get() {
            val languageID = getString(R.string.pref_vocabulary_language_key,
                    R.string.pref_word_language_default).toLong()
            return Language.getLanguage(languageID)
        }
        set(value) {
            putString(R.string.pref_vocabulary_language_key, value.languageID.toString())
        }

    var definitionLanguage: Language
        get() {
            val languageID = getString(R.string.pref_definition_language_key,
                    R.string.pref_definition_language_default).toLong()
            return Language.getLanguage(languageID)
        }
        set(value) {
            putString(R.string.pref_definition_language_key, value.languageID.toString())
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

    private var vocabulary : IVocabulary
        get() {
            val word = getString(R.string.pref_previous_vocabulary_word_key,
                    R.string.pref_previous_vocabulary_word_default)
            val pronunciation= getString(R.string.pref_previous_vocabulary_pronunciation_key,
                    R.string.pref_previous_vocabulary_pronunciation_default)
            val pitch = getString(R.string.pref_previous_vocabulary_pitch_key,
                    R.string.pref_previous_vocabulary_pitch_default)
            val language = Language.valueOf(getString(R.string.pref_previous_vocabulary_language_key,
                    R.string.pref_previous_vocabulary_language_default))
            return Vocabulary(word, pronunciation, pitch, language)
        }
        set(vocabulary) {
            putString(R.string.pref_previous_vocabulary_word_key, vocabulary.word)
            putString(R.string.pref_previous_vocabulary_pronunciation_key, vocabulary.pronunciation)
            putString(R.string.pref_previous_vocabulary_pitch_key, vocabulary.pitch)
            putString(R.string.pref_previous_vocabulary_language_key, vocabulary.language.toString())
        }

    private var definition : List<IDefinition>
        get() {
            val textKey = context.getString(R.string.pref_previous_definition_text_key)
            val definitionTexts = sharedPreferences.getStringSet(textKey, null)?.toList()
                    ?: listOf(context.getString(R.string.pref_previous_definition_text_default))

            val languageKey = context.getString(R.string.pref_previous_definition_language_key)
            val languages = sharedPreferences.getStringSet(languageKey, null)?.toList()
                    ?: listOf(context.getString(R.string.pref_previous_definition_language_default))

            val dictionaryKey = context.getString(R.string.pref_previous_definition_dictionary_key)
            val dictionaries = sharedPreferences.getStringSet(dictionaryKey, null)?.toList()
                    ?: listOf(context.getString(R.string.pref_previous_definition_dictionary_default))

            check(definitionTexts.size == languages.size && languages.size == dictionaries.size)

            val definitions = mutableListOf<Definition>()
            for (i in definitionTexts.indices){
                definitions.add(Definition(definitionTexts[i],
                        Language.valueOf(languages[i]),
                        Dictionary.valueOf(dictionaries[i])))
            }
            return definitions
        }
        set(definitions){
            val editor = sharedPreferences.edit()
            val textKey = context.getString(R.string.pref_previous_definition_text_key)
            editor.putStringSet(textKey, definitions.map { it.definitionText }.toSet())
            val languageKey = context.getString(R.string.pref_previous_definition_language_key)
            editor.putStringSet(languageKey, definitions.map { it.language.toString() }.toSet())
            val dictionaryKey = context.getString(R.string.pref_previous_definition_dictionary_key)
            editor.putStringSet(dictionaryKey, definitions.map { it.dictionary.toString() }.toSet())
            editor.apply()
        }
}

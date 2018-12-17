package data.vocab.model

import data.room.entity.Definition
import data.room.entity.Vocabulary

/**
 * Interface for creating a factory responsible for generating DictionaryEntries
 */
interface DictionaryEntryFactory {
    fun getDictionaryEntry(html: String,
                           wordLanguageCode: String,
                           definitionLanguageCode: String) : DictionaryEntry

    fun getDictionaryEntry(wordSource: String,
                           wordLanguageCode: String,
                           definitionSource: String,
                           definitionLanguageCode: String): DictionaryEntry

    fun getDictionaryEntry(vocab: Vocabulary, def: Definition): DictionaryEntry

    fun getInvalidDictionaryEntry(invalidWord: String,
                           wordLanguageCode: String,
                           definitionLanguageCode: String): DictionaryEntry

}
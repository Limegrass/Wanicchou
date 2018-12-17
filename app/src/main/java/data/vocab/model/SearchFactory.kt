package data.vocab.model

import data.vocab.shared.WordListEntry


/**
 * Interface for creating a factory responsible for generating DictionaryEntries
 */
interface SearchFactory {
    fun getSearch(html: String,
                  wordLanguageCode: String,
                  definitionLanguageCode: String): Search
    fun getDictionaryEntry(html: String,
                           wordLanguageCode: String,
                           definitionLanguageCode: String): DictionaryEntry
    fun getRelatedWords(html: String,
                        wordLanguageCode: String,
                        definitionLanguageCode: String): Array<WordListEntry>
}
package data.vocab.model


/**
 * Interface for creating a factory responsible for generating DictionaryEntries
 */
interface SearchFactory {
    fun getSearch(html: String, wordLanguageCode: String, definitionLanguageCode: String): Search
}
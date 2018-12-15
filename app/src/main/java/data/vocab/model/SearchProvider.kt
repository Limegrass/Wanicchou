@file:Suppress("PropertyName")

package data.vocab.model

/**
 * Definition of components for a search provider
 * See Sanseidou.java as an example
 */
interface SearchProvider {
    val WEB_VIEW_CLASS: Class<*>
    val SEARCH_RESULT_CLASS: Class<*>
    val VOCABULARY_CLASS: Class<*>
}

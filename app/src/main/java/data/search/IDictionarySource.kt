package data.search

import data.enums.Language
import data.enums.MatchType
import java.net.URL

interface IDictionarySource  {
    fun buildSearchQueryURL(searchRequest: SearchRequest) : URL
    val supportedMatchTypes : Set<MatchType>
    /**
     * A map of support languages from its source word language to a target definition language
     */
    val supportedTranslations : Map<Language, Set<Language>>
}

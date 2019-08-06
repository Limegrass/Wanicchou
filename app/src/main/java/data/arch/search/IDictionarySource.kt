package data.arch.search

import data.arch.models.IDictionaryEntry
import data.arch.util.ISearchProvider
import data.enums.Language
import data.enums.MatchType
import data.models.DictionaryEntry

interface IDictionarySource : ISearchProvider<List<IDictionaryEntry>, SearchRequest> {
    val supportedMatchTypes : Set<MatchType>
    /**
     * A map of support languages from its source word language to a target definition language
     */
    val supportedTranslations : Map<Language, Set<Language>>
}

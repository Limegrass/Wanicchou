package data.arch.search

import data.enums.MatchType
import data.room.entity.Definition
import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

interface IDictionaryWebPage {
    suspend fun search(searchTerm: String,
                       wordLanguageCode: String,
                       definitionLanguageCode: String,
                       matchType: MatchType) : Document

    fun getSupportedMatchTypes(): Set<MatchType>

    val dictionaryID : Long
}

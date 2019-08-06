package data.room.search

import data.arch.search.SearchRequest
import data.enums.MatchType
import data.room.database.WanicchouDatabase
import data.room.dbo.composite.DictionaryEntry

internal class WordEqualsDatabaseSearchStrategy : IDatabaseSearchStrategy {
    override suspend fun search(database : WanicchouDatabase,
                                searchRequest: SearchRequest): List<DictionaryEntry> {
        val templateString = MatchType.WORD_EQUALS.templateString
        val formattedSearchTerm = templateString.format(searchRequest.searchTerm)
        val dao = database.dictionaryEntryDao()
        return dao.searchWordEqual(formattedSearchTerm,
                                   searchRequest.wordLanguage)
    }
}
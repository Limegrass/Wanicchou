package data.room.search

import data.arch.search.SearchRequest
import data.enums.MatchType
import data.room.database.WanicchouDatabase
import data.room.dbo.composite.DictionaryEntry

class WordOrDefinitionLikeSearchStrategy : IDatabaseSearchStrategy {
    override suspend fun search(database: WanicchouDatabase,
                                searchRequest: SearchRequest): List<DictionaryEntry> {
        val templateString = MatchType.WORD_OR_DEFINITION_CONTAINS.templateString
        val formattedSearchTerm = templateString.format(searchRequest.searchTerm)
        val dao = database.dictionaryEntryDao()
        return dao.searchWordOrDefinitionLike(formattedSearchTerm,
                                              searchRequest.vocabularyLanguage,
                                              searchRequest.definitionLanguage)
    }
}
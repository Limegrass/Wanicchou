package room.search

import data.search.SearchRequest
import data.enums.MatchType
import room.database.WanicchouDatabase
import room.dbo.composite.DictionaryEntry

class DefinitionLikeSearchStrategy
    : IDatabaseSearchStrategy {
    override suspend fun search(database: WanicchouDatabase,
                                searchRequest: SearchRequest): List<DictionaryEntry> {
        val templateString = MatchType.DEFINITION_CONTAINS.templateString
        val formattedSearchTerm = templateString.format(searchRequest.searchTerm)
        val dao = database.dictionaryEntryDao()
        return dao.searchDefinitionLike(formattedSearchTerm,
                                        searchRequest.vocabularyLanguage,
                                        searchRequest.definitionLanguage)
    }
}
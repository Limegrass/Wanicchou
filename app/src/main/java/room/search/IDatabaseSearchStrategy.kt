package room.search

import data.search.SearchRequest
import room.database.WanicchouDatabase
import room.dbo.composite.DictionaryEntry

interface IDatabaseSearchStrategy {
    suspend fun search(database : WanicchouDatabase,
                       searchRequest: SearchRequest) : List<DictionaryEntry>
}
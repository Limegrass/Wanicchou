package data.room.search

import data.arch.search.SearchRequest
import data.room.database.WanicchouDatabase
import data.room.dbo.composite.DictionaryEntry

interface IDatabaseSearchStrategy {
    suspend fun search(database : WanicchouDatabase,
                       searchRequest: SearchRequest) : List<DictionaryEntry>
}
package data.room.search

import data.room.database.WanicchouDatabase
import data.room.entity.Vocabulary

interface IDatabaseSearchStrategy {
    suspend fun search(database : WanicchouDatabase,
               searchTerm : String,
               wordLanguageCode : String,
               definitionLanguageCode : String) : List<Vocabulary>
}
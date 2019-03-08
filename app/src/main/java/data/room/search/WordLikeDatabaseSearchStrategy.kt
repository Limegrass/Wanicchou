package data.room.search

import data.room.database.WanicchouDatabase
import data.room.entity.Vocabulary

internal class WordLikeDatabaseSearchStrategy : IDatabaseSearchStrategy {
    override suspend fun search(database : WanicchouDatabase,
                                searchTerm: String,
                                wordLanguageCode: String,
                                definitionLanguageCode: String): List<Vocabulary> {
        return database.vocabularyDao().searchWordLike(searchTerm, wordLanguageCode)
    }
}

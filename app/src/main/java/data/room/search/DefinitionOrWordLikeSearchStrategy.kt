package data.room.search

import data.room.database.WanicchouDatabase
import data.room.entity.Vocabulary

class DefinitionOrWordLikeSearchStrategy : IDatabaseSearchStrategy {
    override suspend fun search(database: WanicchouDatabase,
                                searchTerm: String,
                                wordLanguageID: Long,
                                definitionLanguageID: Long): List<Vocabulary> {
        return database.vocabularyDao().searchWordOrDefinitionLike(searchTerm,
                                                                   wordLanguageID,
                                                                   definitionLanguageID)
    }

}

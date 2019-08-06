package data.room.dao.composite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import data.room.dbo.composite.VocabularyAndTag

@Dao
interface VocabularyAndTagDao {
    @Transaction
    @Query("""
        SELECT *
        FROM VocabularyAndTag
        WHERE VocabularyID = :vocabularyID """)
    suspend fun getVocabularyAndTag(vocabularyID: Long) : List<VocabularyAndTag>
}
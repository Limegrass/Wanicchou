package data.room.dao

import androidx.room.Dao
import androidx.room.Query
import data.room.entity.VocabularyTag

@Dao
interface VocabularyTagDao : BaseDao<VocabularyTag>{
    @Query("""
        DELETE FROM VocabularyTag
        WHERE TagID = :tagID AND VocabularyID = :vocabularyID
    """)
    suspend fun deleteVocabularyTag(vocabularyID: Long, tagID: Long)
}

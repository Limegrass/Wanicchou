package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.room.dao.BaseDao
import data.room.dbo.composite.VocabularyAndTag
import data.room.dbo.entity.VocabularyTag

@Dao
interface VocabularyTagDao : BaseDao<VocabularyTag> {
    @Query("""
        DELETE
        FROM VocabularyTag
        WHERE VocabularyTagID IN (
            SELECT vt.VocabularyTagID
            FROM VocabularyTag vt
            JOIN Tag t
                ON t.TagID = vt.TagID
            WHERE t.TagText = :tagText
                AND vt.VocabularyID = :vocabularyID )
    """)
    suspend fun deleteVocabularyTag(vocabularyID: Long, tagText: String)
}
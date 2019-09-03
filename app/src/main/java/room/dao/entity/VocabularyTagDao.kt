package room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import room.dao.BaseDao
import room.dbo.entity.VocabularyTag

@Dao
interface VocabularyTagDao : BaseDao<VocabularyTag> {
    @Query("""
        DELETE
        FROM VocabularyTag
        WHERE VocabularyTagID = (
            SELECT vt.VocabularyTagID
            FROM VocabularyTag vt
            JOIN Tag t
                ON t.TagID = vt.TagID
            WHERE t.TagText = :tagText
                AND vt.VocabularyID = :vocabularyID )
    """)
    suspend fun deleteVocabularyTag(tagText: String, vocabularyID: Long) : Int
}
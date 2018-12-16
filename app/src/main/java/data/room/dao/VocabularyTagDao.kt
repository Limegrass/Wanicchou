package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Query
import data.room.entity.Tag
import data.room.entity.VocabularyTag

interface VocabularyTagDao : BaseDao<VocabularyTag> {
    @Query("""
        SELECT t.*
        FROM Tag t
        JOIN VocabularyTag vt
            ON vt.TagID = t.TagID
        WHERE vt.VocabularyID = :vocabularyID""")
    fun getTagsForVocabularyID(vocabularyID: Int) : LiveData<List<Tag>>
}

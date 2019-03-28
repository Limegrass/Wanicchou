package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import data.room.entity.VocabularyNote

@Dao
interface VocabularyNoteDao : BaseDao<VocabularyNote> {
    @Query("""
        SELECT vn.*
        FROM VocabularyNote vn
        WHERE vn.VocabularyID = :vocabularyID""")
    fun getVocabularyNoteForVocabularyID(vocabularyID: Int): LiveData<List<VocabularyNote>>
}

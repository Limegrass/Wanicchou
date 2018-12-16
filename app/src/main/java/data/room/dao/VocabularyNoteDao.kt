package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Query
import data.room.entity.VocabularyNote

interface VocabularyNoteDao : BaseDao<VocabularyNote> {
    @Query("""
        SELECT vn.*
        FROM VocabularyNote vn
        WHERE vn.VocabularyID = :vocabularyID""")
    fun getVocabularyNoteForVocabularyID(vocabularyID: Int): LiveData<List<VocabularyNote>>
}

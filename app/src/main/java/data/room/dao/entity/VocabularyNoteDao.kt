package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import data.room.dao.BaseDao
import data.room.dbo.entity.VocabularyNote

@Dao
interface VocabularyNoteDao : BaseDao<VocabularyNote> {
    @Transaction
    @Query("""
        SELECT vn.NoteText
        FROM VocabularyNote vn
        WHERE vn.VocabularyID = :vocabularyID""")
    suspend fun getNotesForVocabulary(vocabularyID : Long): List<String>

    @Query("""
        UPDATE VocabularyNote
        SET NoteText = :updatedText
        WHERE VocabularyID = :vocabularyID
            AND NoteText = :originalText """)
    suspend fun updateNote(updatedText: String, originalText : String, vocabularyID: Long)

    @Query("""
        DELETE FROM VocabularyNote
        WHERE VocabularyID = :vocabularyID
            AND NoteText = :noteText """)
    suspend fun deleteNote(noteText : String, vocabularyID: Long)
}
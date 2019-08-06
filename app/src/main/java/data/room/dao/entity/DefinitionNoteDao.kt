package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.room.dao.BaseDao
import data.room.dbo.entity.DefinitionNote

@Dao
interface DefinitionNoteDao : BaseDao<DefinitionNote> {
    @Query("""
        SELECT dn.NoteText
        FROM DefinitionNote dn
        WHERE dn.DefinitionID = :definitionID """)
    fun getNotesForDefinition(definitionID: Long) : List<String>

    @Query("""
        UPDATE DefinitionNote
        SET NoteText = :updatedText
        WHERE DefinitionID = :definitionID
            AND NoteText = :originalText """)
    suspend fun updateNote(updatedText: String, originalText : String, definitionID: Long)

    @Query("""
        DELETE FROM DefinitionNote
        WHERE DefinitionID = :definitionID
            AND NoteText = :noteText """)
    suspend fun deleteNote(noteText : String, definitionID: Long)
}
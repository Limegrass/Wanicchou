package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import data.room.entity.DefinitionNote

@Dao
interface DefinitionNoteDao : BaseDao<DefinitionNote> {
    @Query(value = """
        SELECT dn.*
        FROM DefinitionNote dn
        WHERE dn.DefinitionID = :definitionID""")
    fun getNotesForDefinitionID(definitionID: Long) : List<DefinitionNote>
}

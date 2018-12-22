package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import data.room.entity.DefinitionNote

@Dao
interface DefinitionNoteDao : BaseDao<DefinitionNote> {
    @Query("""
        SELECT dn.*
        FROM DefinitionNote dn
        WHERE dn.DefinitionID = :definitionID""")
    fun getNotesForDefinitionID(definitionID: Int) : LiveData<List<DefinitionNote>>
}

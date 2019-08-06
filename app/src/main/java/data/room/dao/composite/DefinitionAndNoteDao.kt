//package data.room.dao.composite
//
//import androidx.room.Dao
//import androidx.room.Query
//import androidx.room.Transaction
//import data.room.dbo.composite.DefinitionAndNote
//
//@Dao
//interface DefinitionAndNoteDao {
//    @Transaction
//    @Query("""
//        SELECT *
//        FROM DefinitionNote dn
//        WHERE dn.DefinitionID = :definitionID """)
//    fun getNotesForDefinition(definitionID: Long) : List<DefinitionAndNote>
//}
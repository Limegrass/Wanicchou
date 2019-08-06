//package data.room.dao.composite
//
//import androidx.room.Dao
//import androidx.room.Query
//import androidx.room.Transaction
//import data.room.dbo.composite.VocabularyAndNote
//
//@Dao
//interface VocabularyAndNoteDao {
//    @Transaction
//    @Query("""
//        SELECT *
//        FROM VocabularyNote vn
//        WHERE vn.VocabularyID = :vocabularyID""")
//    suspend fun getNotesForVocabulary(vocabularyID : Long): List<VocabularyAndNote>
//}
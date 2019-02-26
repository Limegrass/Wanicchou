package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import data.room.entity.Vocabulary
import data.room.entity.VocabularyInformation

@Dao
interface VocabularyDao : BaseDao<Vocabulary> {
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        ORDER BY v.VocabularyID
        LIMIT 1""")
    fun getLatest(): LiveData<List<Vocabulary>>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
    """)
    fun getAll(): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        WHERE v.Word = :searchTerm
        """)
    fun getVocabularyID(searchTerm: String): Long

    // The index constraint fails on insert
    // if it already exists. Until @Insert properly supports it, must use this.
    @Query("""
        SELECT COUNT(1)
        FROM Vocabulary v
        WHERE v.Word = :word
            AND v.LanguageCode = :wordLanguageCode
            AND v.Pronunciation = :pronunciation
            AND v.Pitch = :pitch
    """)
    fun isAlreadyInserted(word: String,
                         pronunciation: String,
                         wordLanguageCode: String,
                         pitch: String): Boolean

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN VocabularyRelation vr
            ON vr.SearchVocabularyID = v.VocabularyID
        WHERE vr.SearchVocabularyID = :vocabularyID """)
    fun getWordsRelatedToVocabularyID(vocabularyID: Long): List<Vocabulary>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word = :word""")
    fun getVocabulary(word: String) : LiveData<Vocabulary>


    // =========================== Searches =========================
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE  v.LanguageCode = :wordLanguageCode
//            AND v.Word = :searchTerm""")
//    fun search(searchTerm: String,
//               wordLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE  v.LanguageCode = :wordLanguageCode
//            AND v.Word LIKE :searchTerm""")
//    fun searchWithWildcards(searchTerm: String,
//               wordLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.LanguageCode = :wordLanguageCode
//            AND v.Word LIKE :searchTerm+'%' """)
//    fun searchStartsWith(searchTerm: String,
//                         wordLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.LanguageCode = :wordLanguageCode
//            AND v.Word LIKE '%'+:searchTerm""")
//    fun searchEndsWith(searchTerm: String,
//                       wordLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.LanguageCode = :wordLanguageCode
//            AND v.Word LIKE '%'+:searchTerm+'%' """)
//    fun searchContains(searchTerm: String,
//                       wordLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        JOIN Definition d
//            ON v.VocabularyID = d.VocabularyID
//        WHERE d.LanguageCode = :definitionLanguageCode
//            AND d.DefinitionText LIKE '%'+:searchTerm+'%' """)
//    fun searchDefinitionContains(searchTerm: String,
//                                 definitionLanguageCode: String): LiveData<List<Vocabulary>>
//
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        JOIN Definition d
//            ON v.VocabularyID = d.VocabularyID
//        WHERE (v.LanguageCode = :wordLanguageCode AND v.Word LIKE '%'+:searchTerm+'%')
//            OR (d.LanguageCode = :definitionLanguageCode AND d.DefinitionText LIKE '%'+:searchTerm+'%') """)
//    fun searchWordOrDefinitionContains(searchTerm: String,
//                                       wordLanguageCode: String,
//                                       definitionLanguageCode: String): LiveData<List<Vocabulary>>
//
//
//    @Query(value = """
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.VocabularyID IN (:vocabularyIDs)""")
//    fun getRelatedVocabulary(vocabularyIDs : List<Int>) : LiveData<List<Vocabulary>>

    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        WHERE v.VocabularyID = :vocabularyID
        """)
    fun search(vocabularyID: Long) : LiveData<List<VocabularyInformation>>

    /**
     * TESTING GARBAGE HERE FOR ROOM CRAZY RELATIONSHIT
     */
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word = :searchTerm
            AND v.LanguageCode = :wordLanguageCode""")
    fun search(searchTerm : String,
               wordLanguageCode: String) : List<VocabularyInformation>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE  v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE :searchTerm""")
    fun searchWithWildcards(searchTerm: String,
                            wordLanguageCode: String): List<VocabularyInformation>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE :searchTerm+'%' """)
    fun searchStartsWith(searchTerm: String,
                         wordLanguageCode: String): List<VocabularyInformation>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE '%'+:searchTerm""")
    fun searchEndsWith(searchTerm: String,
                       wordLanguageCode: String): List<VocabularyInformation>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE '%'+:searchTerm+'%' """)
    fun searchContains(searchTerm: String,
                       wordLanguageCode: String): List<VocabularyInformation>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE d.LanguageCode = :definitionLanguageCode
            AND d.DefinitionText LIKE '%'+:searchTerm+'%' """)
    fun searchDefinitionContains(searchTerm: String,
                                 definitionLanguageCode: String): List<VocabularyInformation>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE (v.LanguageCode = :wordLanguageCode AND v.Word LIKE '%'+:searchTerm+'%')
            OR (d.LanguageCode = :definitionLanguageCode AND d.DefinitionText LIKE '%'+:searchTerm+'%') """)
    fun searchWordOrDefinitionContains(searchTerm: String,
                                       wordLanguageCode: String,
                                       definitionLanguageCode: String): List<VocabularyInformation>


    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        WHERE v.VocabularyID IN (:vocabularyIDs)""")
    fun getRelatedVocabulary(vocabularyIDs : List<Int>) : List<VocabularyInformation>





    //TODO: Fuzzy
//  http://randomrumenations.blogspot.com/2009/06/improved-t-sql-levenshtein-distance.html
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.Word = :searchTerm""")
//    fun searchFuzzy(searchTerm: String): LiveData<List<Vocabulary>>
}

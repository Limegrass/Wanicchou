package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import data.room.entity.Vocabulary
import data.room.entity.VocabularyInformation

@Dao
interface VocabularyDao : BaseDao<Vocabulary> {
    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        ORDER BY v.VocabularyID DESC
        LIMIT 1""")
    fun getLatest(): List<Vocabulary>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
    """)
    fun getAll(): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.Word = :searchTerm
        """)
    fun getVocabularyID(searchTerm: String): Long

    // The index constraint fails on insert
    // if it already exists. Until @Insert properly supports it, must use this.
    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.Word = :word
            AND v.LanguageCode = :wordLanguageCode
            AND v.Pronunciation = :pronunciation
            AND v.Pitch = :pitch
    """)
    fun getVocabularyID(word: String,
                        pronunciation: String,
                        wordLanguageCode: String,
                        pitch: String): Long

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.Word = :word
            AND v.LanguageCode = :wordLanguageCode
            AND v.Pronunciation = :pronunciation
    """)
    fun getVocabularyID(word: String,
                        pronunciation: String,
                        wordLanguageCode: String): Long

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN VocabularyRelation vr
            ON vr.ResultVocabularyID = v.VocabularyID
        WHERE vr.SearchVocabularyID = :vocabularyID """)
    fun getWordsRelatedToVocabularyID(vocabularyID: Long): List<Vocabulary>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word = :word""")
    fun getVocabulary(word: String) : LiveData<Vocabulary>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.VocabularyID = :vocabularyID""")
    fun getVocabulary(vocabularyID: Long) : List<Vocabulary>


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
    fun search(vocabularyID: Long) : List<VocabularyInformation>

    /**
     * TESTING GARBAGE HERE FOR ROOM CRAZY RELATIONSHIT
     */
    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageCode = :wordLanguageCode
            AND (v.Word = :searchTerm
                OR v.Pronunciation = :searchTerm)""")
    fun search(searchTerm : String,
               wordLanguageCode: String) : List<Vocabulary>

    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageCode = :wordLanguageCode
            AND (v.Word LIKE :searchTerm
                OR v.Pronunciation LIKE :searchTerm)""")
    fun searchWordLike(searchTerm : String,
               wordLanguageCode: String) : List<Vocabulary>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE d.LanguageCode = :definitionLanguageCode
            AND d.DefinitionText LIKE :searchTerm""")
    fun searchDefinitionLike(searchTerm: String,
                                 definitionLanguageCode: String): List<Vocabulary>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE
            (v.LanguageCode = :wordLanguageCode
                AND (v.Word LIKE :searchTerm
                    OR v.Pronunciation LIKE :searchTerm))
            OR (d.LanguageCode = :definitionLanguageCode
                AND d.DefinitionText LIKE :searchTerm) """)
    fun searchWordOrDefinitionLike(searchTerm: String,
                                   wordLanguageCode: String,
                                   definitionLanguageCode: String): List<Vocabulary>


    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        WHERE v.VocabularyID IN (:vocabularyIDs)""")
    fun getRelatedVocabulary(vocabularyIDs : List<Int>) : List<Vocabulary>





    //TODO: Fuzzy
//  http://randomrumenations.blogspot.com/2009/06/improved-t-sql-levenshtein-distance.html
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.Word = :searchTerm""")
//    fun searchFuzzy(searchTerm: String): LiveData<List<Vocabulary>>
}

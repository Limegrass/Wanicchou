package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import data.room.entity.Vocabulary

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
        INNER JOIN Definition d
            on v.VocabularyID = d.VocabularyID
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
            AND v.LanguageID = :wordLanguageID
            AND v.Pronunciation = :pronunciation
            AND v.Pitch = :pitch
    """)
    fun getVocabularyID(word: String,
                        pronunciation: String,
                        wordLanguageID: Long,
                        pitch: String): Long

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.Word = :word
            AND v.LanguageID = :wordLanguageID
            AND v.Pronunciation = :pronunciation
    """)
    fun getVocabularyID(word: String,
                        pronunciation: String,
                        wordLanguageID: Long): Long

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


    /**
     * TESTING GARBAGE HERE FOR ROOM CRAZY RELATIONSHIT
     */
    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageID = :wordLanguageID
            AND (v.Word = :searchTerm
                OR v.Pronunciation = :searchTerm)""")
    fun search(searchTerm : String,
               wordLanguageID: Long) : List<Vocabulary>

    @Transaction
    @Query(value = """
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageID = :wordLanguageID
            AND (v.Word LIKE :searchTerm
                OR v.Pronunciation LIKE :searchTerm)""")
    fun searchWordLike(searchTerm : String,
               wordLanguageID: Long) : List<Vocabulary>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE d.LanguageID = :definitionLanguageID
            AND d.DefinitionText LIKE :searchTerm""")
    fun searchDefinitionLike(searchTerm: String,
                                 definitionLanguageID: Long): List<Vocabulary>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE
            (v.LanguageID = :wordLanguageID
                AND (v.Word LIKE :searchTerm
                    OR v.Pronunciation LIKE :searchTerm))
            OR (d.LanguageID = :definitionLanguageID
                AND d.DefinitionText LIKE :searchTerm) """)
    fun searchWordOrDefinitionLike(searchTerm: String,
                                   wordLanguageID: Long,
                                   definitionLanguageID: Long): List<Vocabulary>


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

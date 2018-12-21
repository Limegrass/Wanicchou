package data.room.dao

import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.Query
import data.room.entity.Vocabulary

interface VocabularyDao : BaseDao<Vocabulary> {

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        ORDER BY v.VocabularyID
        LIMIT 1""")
    fun getLatest(): MutableLiveData<Vocabulary>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
    """)
    fun getAll(): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        WHERE v.Word = :searchTerm
        """)
    fun getVocabularyID(searchTerm: String): Int

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN VocabularyRelation vr
            ON vr.SearchVocabularyID = v.VocabularyID
        WHERE vr.SearchVocabularyID = :vocabularyID """)
    fun getWordsRelatedToVocabularyID(vocabularyID: Int): List<Vocabulary>


    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word = :word""")
    fun getVocabulary(word: String) : MutableLiveData<Vocabulary>


    // =========================== Searches =========================
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE  v.LanguageCode = :wordLanguageCode
            AND v.Word = :searchTerm""")
    fun search(searchTerm: String,
               wordLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE  v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE :searchTerm""")
    fun searchWithWildcards(searchTerm: String,
               wordLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE :searchTerm+'%' """)
    fun searchStartsWith(searchTerm: String,
                         wordLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE '%'+:searchTerm""")
    fun searchEndsWith(searchTerm: String,
                       wordLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageCode = :wordLanguageCode
            AND v.Word LIKE '%'+:searchTerm+'%' """)
    fun searchContains(searchTerm: String,
                       wordLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE d.LanguageCode = :definitionLanguageCode
            AND d.DefinitionText LIKE '%'+:searchTerm+'%' """)
    fun searchDefinitionContains(searchTerm: String,
                                 definitionLanguageCode: String): List<MutableLiveData<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE (v.LanguageCode = :wordLanguageCode AND v.Word LIKE '%'+:searchTerm+'%')
            OR (d.LanguageCode = :definitionLanguageCode AND d.DefinitionText LIKE '%'+:searchTerm+'%') """)
    fun searchWordOrDefinitionContains(searchTerm: String,
                                       wordLanguageCode: String,
                                       definitionLanguageCode: String): List<MutableLiveData<Vocabulary>>
    //TODO: Fuzzy
//  http://randomrumenations.blogspot.com/2009/06/improved-t-sql-levenshtein-distance.html
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.Word = :searchTerm""")
//    fun searchFuzzy(searchTerm: String): LiveData<List<Vocabulary>>
}

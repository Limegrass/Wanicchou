package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Query
import data.room.entity.Vocabulary

interface VocabularyDao : BaseDao<Vocabulary> {

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        ORDER BY v.VocabularyID
        LIMIT 1""")
    fun getLatest(): Vocabulary


    @Query("""
        SELECT v.*
        FROM Vocabulary v
    """)
    fun getAll(): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word LIKE :searchTerm+'%' """)
    fun searchStartsWith(searchTerm: String): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word LIKE '%'+:searchTerm""")
    fun searchEndsWith(searchTerm: String): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word LIKE '%'+:searchTerm+'%' """)
    fun searchContains(searchTerm: String): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.Word = :searchTerm""")
    fun search(searchTerm: String): Vocabulary

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        WHERE v.Word = :searchTerm""")
    fun getVocabularyID(searchTerm: String): Int

    //TODO: Fuzzy
//  http://randomrumenations.blogspot.com/2009/06/improved-t-sql-levenshtein-distance.html
//    @Query("""
//        SELECT v.*
//        FROM Vocabulary v
//        WHERE v.Word = :searchTerm""")
//    fun searchFuzzy(searchTerm: String): LiveData<List<Vocabulary>>
}

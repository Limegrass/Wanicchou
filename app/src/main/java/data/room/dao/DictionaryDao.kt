package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Dictionary

@Dao
interface DictionaryDao : BaseDao<Dictionary> {
    @Query("""
        SELECT d.*
        FROM Dictionary d
        WHERE d.DictionaryName = :dictionary""")
    fun getDictionaryByName(dictionary: String) : LiveData<Dictionary>

    @Query("""
        SELECT d.*
        FROM Dictionary d
        WHERE d.DictionaryID = :dictionaryID""")
    fun getDictionaryByID(dictionaryID : Int) : LiveData<Dictionary>

    @Query("""
        SELECT d.*
        FROM Dictionary d""")
    fun getAllDictionaries() : List<Dictionary>
}

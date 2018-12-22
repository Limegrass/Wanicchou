package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
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
        WHERE d.DictionaryID = :dictionaryID
    """)
    fun getDictionaryByID(dictionaryID : Int) : LiveData<Dictionary>
}

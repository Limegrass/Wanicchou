package data.room.dao

import android.arch.persistence.room.Query
import data.room.entity.Dictionary

interface DictionaryDao : BaseDao<Dictionary> {
    @Query("""
        SELECT d.*
        FROM Dictionary d
        WHERE d.DictionaryName = :dictionary""")
    fun getDictionaryByName(dictionary: String) : Dictionary

    @Query("""
        SELECT d.*
        FROM Dictionary d
        WHERE d.DictionaryID = :dictionaryID
    """)
    fun getDictionaryByID(dictionaryID : Int) : Dictionary
}

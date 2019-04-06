package data.room.dao

import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Language

@Dao
interface LanguageDao : BaseDao<Language> {
    @Query("""
        SELECT l.*
        FROM Language l
    """)
    fun getAllLanguages(): List<Language>


}


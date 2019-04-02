package data.room.dao

import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Translation

@Dao
interface TranslationDao : BaseDao<Translation> {
    @Query("""
        SELECT t.*
        FROM Translation t
        WHERE t.DictionaryID = :dictionaryID
    """)
    fun getDictionaryTranslations(dictionaryID: Long) : List<Translation>
}

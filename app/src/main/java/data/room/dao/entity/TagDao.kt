package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.room.dao.BaseDao
import data.room.dbo.entity.Tag

@Dao
interface TagDao : BaseDao<Tag> {
    @Query("""
        SELECT t.TagID
        FROM Tag t
        WHERE t.TagText = :tag""")
    fun getExistingTagID(tag: String) : Long?
}
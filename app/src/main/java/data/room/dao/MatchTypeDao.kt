package data.room.dao

import androidx.room.Dao
import androidx.room.Query
import data.room.entity.MatchType

@Dao
interface MatchTypeDao : BaseDao<MatchType> {
    @Query("""
        SELECT mt.TemplateString
        FROM MatchType mt
        WHERE mt.MatchTypeID = :matchTypeID """)
    fun getTemplateString(matchTypeID : Long): String
}
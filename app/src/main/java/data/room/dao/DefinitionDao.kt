package data.room.dao

import android.arch.persistence.room.Query
import data.room.entity.Definition

interface DefinitionDao : BaseDao<Definition> {
    @Query("""
        SELECT *
        FROM Definition
        ORDER BY DefinitionID DESC
        LIMIT 1""")
    fun getLatest()

    @Query("""
        SELECT *
        FROM Definition
        WHERE DefinitionText LIKE '%' + :searchTerm + '%'
    """)
    fun definitionContains(searchTerm: String): Definition
}

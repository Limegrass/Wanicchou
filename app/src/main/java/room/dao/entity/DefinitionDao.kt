package room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.enums.Dictionary
import data.enums.Language
import room.dao.BaseDao
import room.dbo.entity.Definition

@Dao
interface DefinitionDao : BaseDao<Definition> {
    @Query("""
        SELECT d.DefinitionID
        FROM Definition d
        WHERE d.DefinitionText = :definitionText
            AND d.DictionaryID = :dictionary
            AND d.LanguageID = :definitionLanguage """)
    fun getDefinitionID(definitionText: String, definitionLanguage: Language, dictionary: Dictionary) : Long?
}
package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.enums.Dictionary
import data.enums.Language
import data.room.dao.BaseDao
import data.room.dbo.entity.Definition

@Dao
interface DefinitionDao : BaseDao<Definition> {
    @Query("""
        SELECT d.DefinitionID
        FROM Definition d
        WHERE d.DefinitionText = :definitionText
            AND d.DictionaryID = :dictionary
            AND d.LanguageID = :definitionLanguage """)
    fun getDefinitionID(definitionText : String, dictionary: Dictionary, definitionLanguage : Language) : Long?

    @Query("""
        SELECT d.DefinitionID
        FROM Definition d
        WHERE d.VocabularyID = :vocabularyID """)
    fun getDefinedVocabularyID(vocabularyID : Long) : Long?
}
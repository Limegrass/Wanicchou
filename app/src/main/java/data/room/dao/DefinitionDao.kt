package data.room.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Definition

@Dao
interface DefinitionDao : BaseDao<Definition> {
    @Query("""
        SELECT *
        FROM Definition
        ORDER BY DefinitionID DESC
        LIMIT 1""")
    fun getLatest(): LiveData<Definition>

    @Query("""
        SELECT *
        FROM Definition
        WHERE DefinitionText LIKE '%' + :searchTerm + '%'
    """)
    fun definitionContains(searchTerm: String): LiveData<Definition>

    @Query("""
        SELECT d.*
        FROM Definition d
        WHERE d.VocabularyID = :vocabularyID
            AND d.LanguageCode = :definitionLanguageCode
            AND d.DictionaryID = :dictionaryID
    """)
    fun getVocabularyDefinitions(vocabularyID: Long,
                                 definitionLanguageCode: String,
                                 dictionaryID : Long): Definition?

    @Query("""
        SELECT d.*
        FROM Definition d
        WHERE d.VocabularyID = :vocabularyID
        ORDER BY d.DefinitionID DESC
        LIMIT 1
    """)
    fun getLatestDefinition(vocabularyID: Int): LiveData<List<Definition>>
}

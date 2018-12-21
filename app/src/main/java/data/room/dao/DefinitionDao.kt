package data.room.dao

import android.arch.lifecycle.MutableLiveData
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

    @Query("""
        SELECT d.*
        FROM Definition d
        WHERE d.VocabularyID = :vocabularyID
    """)
    fun getVocabularyDefinitions(vocabularyID: Int, definitionLanguageCode: String): List<MutableLiveData<Definition>>

    @Query("""
        SELECT d.*
        FROM Definition d
        WHERE d.VocabularyID = :vocabularyID
        ORDER BY d.DefinitionID DESC
        LIMIT 1
    """
    )
    fun getLatestDefinition(vocabularyID: Int): MutableLiveData<Definition>
}

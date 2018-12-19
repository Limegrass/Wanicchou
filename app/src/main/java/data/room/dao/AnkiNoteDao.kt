package data.room.dao

import android.arch.persistence.room.Query
import data.room.entity.AnkiNote

interface AnkiNoteDao : BaseDao<AnkiNote> {
    @Query("""
        SELECT *
        FROM AnkiNote an
        JOIN Definition d
            ON d.DefinitionID = an.DictionaryID
        JOIN Vocabulary v
            ON d.VocabularyID = v.VocabularyID
        WHERE d.LanguageCode = :definitionLanguageCode
            AND v.Word = :searchTerm
    """)
    fun getAnkiNoteID(searchTerm: String, definitionLanguageCode: String): List<AnkiNote>
}
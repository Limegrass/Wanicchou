package data.room.dao.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import data.enums.Language
import data.room.dao.BaseDao
import data.room.dbo.entity.Vocabulary

@Dao
interface VocabularyDao : BaseDao<Vocabulary> {

    @Query("""
        SELECT v.*
        FROM Vocabulary v
        INNER JOIN Definition d
            on v.VocabularyID = d.VocabularyID
    """)
    fun getAllWithDefinition(): LiveData<List<Vocabulary>>

    @Query("""
        SELECT v.VocabularyID
        FROM Vocabulary v
        WHERE v.Word = :word
            AND v.Pronunciation = :pronunciation
            AND v.Pitch = :pitch
            AND v.LanguageID = :wordLanguage
    """)
    fun getVocabularyID(word: String,
                        pronunciation: String,
                        pitch: String,
                        wordLanguage: Language): Long?
}
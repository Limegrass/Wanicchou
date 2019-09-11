package room.dao.composite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import data.enums.Language
import room.dbo.composite.DictionaryEntry

@Dao
interface DictionaryEntryDao {
    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageID = :vocabularyLanguage
            AND (v.Word LIKE :searchTerm
                OR v.Pronunciation LIKE :searchTerm)
        UNION
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageID = :vocabularyLanguage
            AND IFNULL(d.LanguageID, :definitionLanguage) = :definitionLanguage
            AND (v.Word LIKE :searchTerm
                OR v.Pronunciation LIKE :searchTerm)""")
    fun searchWordLike(searchTerm: String,
                       vocabularyLanguage: Language,
                       definitionLanguage: Language) : List<DictionaryEntry>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        WHERE v.LanguageID = :vocabularyLanguage
            AND (v.Word = :searchTerm
                OR v.Pronunciation = :searchTerm)
        UNION
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageID = :vocabularyLanguage
            AND IFNULL(d.LanguageID, :definitionLanguage) = :definitionLanguage
            AND (v.Word = :searchTerm
                OR v.Pronunciation = :searchTerm)""")
    fun searchWordEqual(searchTerm: String,
                        vocabularyLanguage: Language,
                        definitionLanguage: Language) : List<DictionaryEntry>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON v.VocabularyID = d.VocabularyID
        WHERE v.LanguageID = :vocabularyLanguage
            AND IFNULL(d.LanguageID, :definitionLanguage) = :definitionLanguage
            AND d.DefinitionText LIKE :searchTerm""")
    fun searchDefinitionLike(searchTerm: String,
                             vocabularyLanguage: Language,
                             definitionLanguage : Language) : List<DictionaryEntry>

    @Transaction
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN Definition d
            ON d.VocabularyID = v.VocabularyID
        WHERE v.LanguageID = :vocabularyLanguage
            AND IFNULL(d.LanguageID, :definitionLanguage) = :definitionLanguage
            AND (v.Word LIKE :searchTerm
                OR v.Pronunciation LIKE :searchTerm
                OR d.DefinitionText LIKE :searchTerm)""")
    fun searchWordOrDefinitionLike(searchTerm: String,
                                   vocabularyLanguage : Language,
                                   definitionLanguage: Language) : List<DictionaryEntry>
}
package room.database

import data.enums.Dictionary
import data.enums.Language
import room.dbo.entity.Vocabulary
import org.junit.Test
import kotlin.test.assertEquals

class ConvertersTest {
    @Test
    fun `fromVocabularyEntity returns vocabularyID`(){
        val vocabularyID = 111L
        val vocabulary = Vocabulary("", "", "", Language.JAPANESE, vocabularyID)
        val converters = Converters()
        assertEquals(vocabularyID, converters.fromVocabularyEntity(vocabulary))
    }

    @Test
    fun `fromLanguage returns languageID`(){
        val language = Language.ENGLISH
        val converters = Converters()
        assertEquals(language.languageID, converters.fromLanguage(language))
    }

    @Test
    fun `toLanguage returns Language enum`(){
        val language = Language.ENGLISH
        val converters = Converters()
        assertEquals(language, converters.toLanguage(language.languageID))
    }

    @Test
    fun `fromDictionary returns dictionaryID`(){
        val dictionary = Dictionary.SANSEIDO
        val converters = Converters()
        assertEquals(dictionary.dictionaryID, converters.fromDictionary(dictionary))
    }

    @Test
    fun `toDictionary returns Dictionary enum`(){
        val dictionary = Dictionary.SANSEIDO
        val converters = Converters()
        assertEquals(dictionary, converters.toDictionary(dictionary.dictionaryID))
    }
}
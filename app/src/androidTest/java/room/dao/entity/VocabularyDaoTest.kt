package room.dao.entity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import data.enums.Dictionary
import data.enums.Language
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import room.dao.AbstractDaoTest
import util.awaitValue
import kotlin.test.assertEquals

class VocabularyDaoTest : AbstractDaoTest() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getAllWithDefinition_NoResults(){
        runBlocking {
            db.vocabularyDao().insert(Vocabulary("1", "", "", Language.JAPANESE))
            db.vocabularyDao().insert(Vocabulary("2", "", "", Language.JAPANESE))
            db.vocabularyDao().insert(Vocabulary("3", "", "", Language.JAPANESE))
        }
        val liveData = db.vocabularyDao().getAllWithDefinition()
        val vocabularies = liveData.awaitValue()
        assertEquals(0, vocabularies.size)
    }

    @Test
    fun getAllWithDefinition_HasResult(){
        runBlocking {
            db.vocabularyDao().insert(Vocabulary("1", "", "", Language.JAPANESE))
            db.vocabularyDao().insert(Vocabulary("2", "", "", Language.JAPANESE))
            db.definitionDao().insert(Definition("Test", Language.JAPANESE, Dictionary.SANSEIDO, 1))
        }
        val liveData = db.vocabularyDao().getAllWithDefinition()
        val vocabularies = liveData.awaitValue()
        assertEquals(1, vocabularies.size)
    }

    @Test
    fun getVocabularyID_Exists_ReturnsID(){
        val word = "1"
        val pronunciation = ""
        val pitch = ""
        val language = Language.JAPANESE
        val vocabularyID = runBlocking {
            db.vocabularyDao().insert(Vocabulary(word, pronunciation, pitch, language))
        }
        val reacquiredID = runBlocking {
            db.vocabularyDao().getVocabularyID(word, pronunciation, pitch, language)
        }
        assertEquals(vocabularyID, reacquiredID)
    }

    @Test
    fun getVocabularyID_WordMismatch_ReturnsNull(){
        val word = "1"
        val pronunciation = ""
        val pitch = ""
        val language = Language.JAPANESE
        runBlocking {
            db.vocabularyDao().insert(Vocabulary(word, pronunciation, pitch, language))
        }
        val reacquiredID = runBlocking {
            db.vocabularyDao().getVocabularyID("One", pronunciation, pitch, language)
        }
        assertEquals(null, reacquiredID)
    }

    @Test
    fun getVocabularyID_PronunciationMismatch_ReturnsNull(){
        val word = "1"
        val pronunciation = ""
        val pitch = ""
        val language = Language.JAPANESE
        runBlocking {
            db.vocabularyDao().insert(Vocabulary(word, pronunciation, pitch, language))
        }
        val reacquiredID = runBlocking {
            db.vocabularyDao().getVocabularyID(word, "One", pitch, language)
        }
        assertEquals(null, reacquiredID)
    }

    @Test
    fun getVocabularyID_PitchMismatch_ReturnsNull(){
        val word = "1"
        val pronunciation = ""
        val pitch = ""
        val language = Language.JAPANESE
        runBlocking {
            db.vocabularyDao().insert(Vocabulary(word, pronunciation, pitch, language))
        }
        val reacquiredID = runBlocking {
            db.vocabularyDao().getVocabularyID(word, pronunciation, "1", language)
        }
        assertEquals(null, reacquiredID)
    }

    @Test
    fun getVocabularyID_LanguageMismatch_ReturnsNull(){
        val word = "1"
        val pronunciation = ""
        val pitch = ""
        val language = Language.JAPANESE
        runBlocking {
            db.vocabularyDao().insert(Vocabulary(word, pronunciation, pitch, language))
        }
        val reacquiredID = runBlocking {
            db.vocabularyDao().getVocabularyID(word, pronunciation, pitch, Language.ENGLISH)
        }
        assertEquals(null, reacquiredID)
    }
}
package room.dao.entity

import data.enums.Language
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import kotlin.test.assertEquals

class DefinitionDaoTest : AbstractDaoTest() {
    private val definitionText = "Test"
    private val dictionary = data.enums.Dictionary.SANSEIDO
    private val language = Language.JAPANESE
    private var definitionID : Long = 0
    private fun insertTestData(){
        val vocabularyID = runBlocking {
            db.vocabularyDao().insert(Vocabulary("", "", "", Language.JAPANESE))
        }

        val definition = Definition(definitionText, language, dictionary, vocabularyID)

        definitionID = runBlocking {
            db.definitionDao().insert(definition)
        }
    }

    @Test
    fun getDefinitionIDByDefinitionText_Exists_ReturnsID() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText(definitionText, language, dictionary)
        }
        assertEquals(definitionID, daoDefinitionID)
    }

    @Test
    fun getDefinitionIDByDefinitionText_DefinitionTextMismatch_ReturnsNull() {
        insertTestData()
        val daoDefinitionID = runBlocking{
            db.definitionDao().getDefinitionIDByDefinitionText("", language, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }

    @Test
    fun getDefinitionIDByDefinitionText_LanguageMismatch_ReturnsNull() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText("", Language.ENGLISH, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }

    @Test
    fun getDefinitionIDByVocabularyID_LanguageMismatch_ReturnsNull() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText("", Language.ENGLISH, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }
    @Test
    fun getDefinitionIDByVocabularyID_IDMismatch_ReturnsNull() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText("", Language.ENGLISH, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }
    @Test
    fun getDefinitionIDByVocabularyID_VocabularyIDMismatch_ReturnsNull() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText("", Language.ENGLISH, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }
    @Test
    fun getDefinitionIDByVocabularyID_Match_ReturnsID() {
        insertTestData()
        val daoDefinitionID = runBlocking {
            db.definitionDao().getDefinitionIDByDefinitionText("", Language.ENGLISH, dictionary)
        }
        assertEquals(null, daoDefinitionID)
    }
}
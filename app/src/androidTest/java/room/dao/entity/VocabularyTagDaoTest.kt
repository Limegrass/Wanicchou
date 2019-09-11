package room.dao.entity

import data.enums.Language
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.Tag
import room.dbo.entity.Vocabulary
import room.dbo.entity.VocabularyTag
import kotlin.test.assertEquals

class VocabularyTagDaoTest : AbstractDaoTest() {
    private fun insertVocabularyAndGetID() : Long {
        val vocabulary = Vocabulary("", "", "", Language.JAPANESE)
        return runBlocking {
            db.vocabularyDao().insert(vocabulary)
        }
    }
    @Test
    fun deleteVocabularyTagTest(){
        val vocabularyID = insertVocabularyAndGetID()
        val tagText = "Test"
        val tag = Tag(tagText)
        val tagID = runBlocking {
            db.tagDao().insert(tag)
        }
        val vocabularyTag = VocabularyTag(tagID, vocabularyID)
        runBlocking {
            db.vocabularyTagDao().insert(vocabularyTag)
        }
        val deletedCount = runBlocking {
            db.vocabularyTagDao().deleteVocabularyTag(tagText, vocabularyID)
        }
        assertEquals(1, deletedCount)
    }
}
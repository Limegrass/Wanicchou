package room.dao.composite

import data.enums.Language
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.Tag
import room.dbo.entity.Vocabulary
import room.dbo.entity.VocabularyTag
import kotlin.test.assertEquals

class VocabularyAndTagDaoTest : AbstractDaoTest() {
    private fun insertVocabularyAndGetID(word: String) : Long {
        val vocabulary = Vocabulary(word, "", "", Language.JAPANESE)
        return runBlocking {
            db.vocabularyDao().insert(vocabulary)
        }
    }
    @Test
    fun getVocabularyAndTag_FindsAll() {
        val vocabularyID = insertVocabularyAndGetID("")
        val tags = listOf(Tag("1"), Tag("2"), Tag("3"))
        runBlocking {
            for (tag in tags){
                val tagID = db.tagDao().insert(tag)
                db.vocabularyTagDao().insert(VocabularyTag(tagID, vocabularyID))
            }
        }

        val vocabularyAndTags = runBlocking {
            db.vocabularyAndTagDao().getVocabularyAndTag(vocabularyID)
        }
        assertEquals(tags.size, vocabularyAndTags.size)
        val tagTexts = tags.map { it.tagText }
        for(tag in vocabularyAndTags){
            assertEquals(vocabularyID, tag.vocabulary.vocabularyID)
            assert(tagTexts.contains(tag.tag))
        }
    }

    @Test
    fun getVocabularyAndTag_AvoidsOtherTags() {
        val vocabularyIDs = listOf(
                insertVocabularyAndGetID("1"),
                insertVocabularyAndGetID("2"))
        val tags = listOf(Tag("1"), Tag("2"), Tag("3"))
        runBlocking {
            for (vocabularyID in vocabularyIDs){
                for (tag in tags){
                    val tagID = db.tagDao().insert(tag)
                    db.vocabularyTagDao().insert(VocabularyTag(tagID, vocabularyID))
                }
            }
        }

        for (vocabularyID in vocabularyIDs){
            val vocabularyAndTags = runBlocking {
                db.vocabularyAndTagDao().getVocabularyAndTag(vocabularyID)
            }
            assertEquals(tags.size, vocabularyAndTags.size)
            val tagTexts = tags.map { it.tagText }
            for(tag in vocabularyAndTags){
                assertEquals(vocabularyID, tag.vocabulary.vocabularyID)
                assert(tagTexts.contains(tag.tag))
            }
        }
    }
}
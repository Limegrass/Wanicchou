package room.repository

import data.enums.Language
import data.models.TaggedItem
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.entity.TagDao
import room.dao.entity.VocabularyDao
import room.dao.entity.VocabularyTagDao
import room.database.WanicchouDatabase
import room.dbo.composite.VocabularyAndTag
import room.dbo.entity.Tag
import room.dbo.entity.Vocabulary
import kotlin.random.Random
import kotlin.test.assertEquals

class VocabularyTagRepositoryTest {
    @Test
    fun search_ReturnsNoResultsWithNoVocabularyID(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(any(), any(), any(), any())
            } returns null
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
        }
        val repository = VocabularyTagRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(0, searchResults.size)
        verifyAll {
            vocabularyDao.getVocabularyID(any(), any(), any(), any())
        }
    }

    @Test
    fun search_ReturnsNoResultsWithoutAnySaved(){
        val vocabularyID = Random.nextLong()
        val vocabulary = Vocabulary("", "", "", Language.JAPANESE)
        val tag = Tag("Test", 1)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                every {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID + 1 //Mismatch caused here
            }
            every {
                vocabularyAndTagDao()
            } returns mockk {
                coEvery {
                    getVocabularyAndTag(vocabularyID)
                } returns listOf(VocabularyAndTag(vocabulary, tag))
                coEvery {
                    getVocabularyAndTag(neq(vocabularyID))
                } returns listOf()
            }
        }
        val repository = VocabularyTagRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(0, searchResults.size)
    }

    @Test
    fun search_ReturnsRelatedTags(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val tag = Tag("Test", 1)
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns vocabularyID
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
            every {
                vocabularyAndTagDao()
            } returns mockk {
                coEvery {
                    getVocabularyAndTag(vocabularyID)
                } returns listOf(VocabularyAndTag(Vocabulary(vocabulary, 1), tag))
                coEvery {
                    getVocabularyAndTag(neq(vocabularyID))
                } returns listOf()
            }
        }
        val repository = VocabularyTagRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(1, searchResults.size)
        assertEquals("Test", searchResults.single().tag)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun insert_ThrowsIfVocabularyIDNotFound(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                every {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns null
            }
        }
        val repository = VocabularyTagRepository(db)
        runBlocking {
            repository.insert(TaggedItem(vocabulary, "Test"))
        }
    }

    @Test
    fun insert_CallsInsert(){
        val vocabularyID = Random.nextLong()
        val tagID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns vocabularyID
        }
        val vocabularyTagDao = mockk<VocabularyTagDao>{
            coEvery {
                insert(any())
            } returns vocabularyID
        }
        val tagDao = mockk<TagDao> {
            every {
                getExistingTagID(any())
            } returns tagID
        }

        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
            every {
                vocabularyTagDao()
            } returns vocabularyTagDao
            every {
                tagDao()
            } returns tagDao
        }
        val repository = VocabularyTagRepository(db)
        runBlocking {
            repository.insert(TaggedItem(vocabulary, "Test"))
        }

        coVerifyAll {
            vocabularyTagDao.insert(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun update_ThrowsIfTopicsMismatch(){
        val vocabularyID = Random.nextLong()
        val tagID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val other = data.models.Vocabulary("", "", "", Language.ENGLISH)
        val tagDao = mockk<TagDao> {
            every {
                getExistingTagID(any())
            } returns tagID
            coEvery {
                update(any())
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                every {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyTagDao()
            } returns mockk {
                every {
                    runBlocking { insert(any()) }
                } returns vocabularyID
            }
            every {
                tagDao()
            } returns tagDao
        }
        val repository = VocabularyTagRepository(db)
        runBlocking {
            repository.update(TaggedItem(vocabulary, "Test"),
                    TaggedItem(other, "Test2"))
        }
    }

    @Test
    fun update_CallsUpdate(){
        val vocabularyID = Random.nextLong()
        val tagID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val tagDao = mockk<TagDao> {
            every {
                getExistingTagID(any())
            } returns tagID
            coEvery {
                update(any())
            } just Runs
        }

        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                every {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                tagDao()
            } returns tagDao
        }
        val repository = VocabularyTagRepository(db)
        runBlocking {
            repository.update(TaggedItem(vocabulary, "Test"),
                    TaggedItem(vocabulary, "Test2"))
        }
        coVerify {
            tagDao.update(any())
        }
    }

    @Test
    fun delete_CallsDelete(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyTagDao = mockk<VocabularyTagDao>{
            coEvery {
                deleteVocabularyTag(any(), vocabularyID)
            } returns 1
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                every {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyTagDao()
            } returns vocabularyTagDao
        }
        val repository = VocabularyTagRepository(db)
        val noteText = "Test"
        runBlocking {
            repository.delete(TaggedItem(vocabulary, noteText))
        }
        coVerify {
            vocabularyTagDao.deleteVocabularyTag(noteText, vocabularyID)
        }
    }
}

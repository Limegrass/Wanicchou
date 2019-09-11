package room.repository

import data.enums.Language
import data.models.Note
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.entity.VocabularyDao
import room.dao.entity.VocabularyNoteDao
import room.database.WanicchouDatabase
import kotlin.random.Random
import kotlin.test.assertEquals

class VocabularyNoteRepositoryTest {
    @Test
    fun search_ReturnsNoResultsWithNoVocabularyID(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyDao = mockk<VocabularyDao>{
            coEvery {
                getVocabularyID(any(), any(), any(), any())
            } returns null
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
        }
        val repository = VocabularyNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(0, searchResults.size)
        coVerifyAll {
            vocabularyDao.getVocabularyID(any(), any(), any(), any())
        }
    }

    @Test
    fun search_ReturnsNoResultsWithoutAnySaved(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                                    vocabulary.pronunciation,
                                    vocabulary.pitch,
                                    vocabulary.language)
                } returns vocabularyID + 1
            }
            every {
                vocabularyNoteDao()
            } returns mockk {
                coEvery {
                    getNotesForVocabulary(vocabularyID)
                } returns listOf("Test")
                coEvery {
                    getNotesForVocabulary(neq(vocabularyID))
                } returns listOf()
            }
        }
        val repository = VocabularyNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(0, searchResults.size)
    }

    @Test
    fun search_ReturnsRelatedNotes(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyNoteDao()
            } returns mockk {
                coEvery {
                    getNotesForVocabulary(vocabularyID)
                } returns listOf("Test")
                coEvery {
                    getNotesForVocabulary(neq(vocabularyID))
                } returns listOf()
            }
        }
        val repository = VocabularyNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(vocabulary)
        }
        assertEquals(1, searchResults.size)
        assertEquals("Test", searchResults.single().noteText)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun insert_ThrowsIfVocabularyIDNotFound(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns null
            }
        }
        val repository = VocabularyNoteRepository(db)
        runBlocking {
            repository.insert(Note(vocabulary, "Test"))
        }
    }

    @Test
    fun insert_CallsInsert(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyDao = mockk<VocabularyDao>{
            coEvery {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns vocabularyID
        }
        val vocabularyNoteDao = mockk<VocabularyNoteDao>{
            coEvery {
                insert(any())
            } returns vocabularyID
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
            every {
                vocabularyNoteDao()
            } returns vocabularyNoteDao
        }
        val repository = VocabularyNoteRepository(db)
        runBlocking {
            repository.insert(Note(vocabulary, "Test"))
        }

        coVerifyAll {
            vocabularyNoteDao.insert(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun update_ThrowsIfTopicsMismatch(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val other = data.models.Vocabulary("", "", "", Language.ENGLISH)
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyNoteDao()
            } returns mockk {
                every {
                    runBlocking { insert(any()) }
                } returns vocabularyID
            }
        }
        val repository = VocabularyNoteRepository(db)
        runBlocking {
            repository.update(Note(vocabulary, "Test"),
                    Note(other, "Test2"))
        }
    }

    @Test
    fun update_CallsUpdate(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyNoteDao = mockk<VocabularyNoteDao>{
            coEvery {
                updateNote(any(), any(), vocabularyID)
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyNoteDao()
            } returns vocabularyNoteDao
        }
        val repository = VocabularyNoteRepository(db)
        runBlocking {
            repository.update(Note(vocabulary, "Test"),
                    Note(vocabulary, "Test2"))
        }
        coVerify {
            vocabularyNoteDao.updateNote(any(), any(), vocabularyID)
        }
    }

    @Test
    fun delete_CallsDelete(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyNoteDao = mockk<VocabularyNoteDao>{
            coEvery {
                deleteNote(any(), vocabularyID)
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns mockk {
                coEvery {
                    getVocabularyID(vocabulary.word,
                            vocabulary.pronunciation,
                            vocabulary.pitch,
                            vocabulary.language)
                } returns vocabularyID
            }
            every {
                vocabularyNoteDao()
            } returns vocabularyNoteDao
        }
        val repository = VocabularyNoteRepository(db)
        val noteText = "Test"
        runBlocking {
            repository.delete(Note(vocabulary, noteText))
        }
        coVerify {
            vocabularyNoteDao.deleteNote(noteText, vocabularyID)
        }
    }
}

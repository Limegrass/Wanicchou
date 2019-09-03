package room.repository

import data.enums.Dictionary
import data.enums.Language
import data.models.Note
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.entity.DefinitionDao
import room.dao.entity.DefinitionNoteDao
import room.database.WanicchouDatabase
import kotlin.random.Random
import kotlin.test.assertEquals

class DefinitionNoteRepositoryTest {
    @Test
    fun search_ReturnsNoResultsWithNoDefinitionID(){
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionDao = mockk<DefinitionDao>{
            every {
                getDefinitionID(any(), any(), any())
            } returns null
        }
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns definitionDao
        }
        val repository = DefinitionNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(definition)
        }
        assertEquals(0, searchResults.size)
        verifyAll {
            definitionDao.getDefinitionID(any(), any(), any())
        }
    }

    @Test
    fun search_ReturnsNoResultsWithoutAnySaved(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns definitionID + 1
            }
            every {
                definitionNoteDao()
            } returns mockk {
                coEvery {
                    getNotesForDefinition(definitionID)
                } returns listOf("Test")
                coEvery {
                    getNotesForDefinition(neq(definitionID))
                } returns listOf()
            }
        }
        val repository = DefinitionNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(definition)
        }
        assertEquals(0, searchResults.size)
    }

    @Test
    fun search_ReturnsRelatedNotes(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns definitionID
            }
            every {
                definitionNoteDao()
            } returns mockk {
                coEvery {
                    getNotesForDefinition(definitionID)
                } returns listOf("Test")
                coEvery {
                    getNotesForDefinition(neq(definitionID))
                } returns listOf()
            }
        }
        val repository = DefinitionNoteRepository(db)

        val searchResults = runBlocking {
            repository.search(definition)
        }
        assertEquals(1, searchResults.size)
        assertEquals("Test", searchResults.single().noteText)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun insert_ThrowsIfDefinitionIDNotFound(){
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns null
            }
        }
        val repository = DefinitionNoteRepository(db)
        runBlocking {
            repository.insert(Note(definition, "Test"))
        }
    }

    @Test
    fun insert_CallsInsert(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionDao = mockk<DefinitionDao>{
            every {
                getDefinitionID(any(), any(), any())
            } returns definitionID
        }
        val definitionNoteDao = mockk<DefinitionNoteDao>{
            coEvery {
                insert(any())
            } returns definitionID
        }
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns definitionDao
            every {
                definitionNoteDao()
            } returns definitionNoteDao
        }
        val repository = DefinitionNoteRepository(db)
        runBlocking {
            repository.insert(Note(definition, "Test"))
        }

        coVerifyAll {
            definitionNoteDao.insert(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun update_ThrowsIfTopicsMismatch(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val other = data.models.Definition("2", Language.JAPANESE, Dictionary.SANSEIDO)
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns definitionID
            }
            every {
                definitionNoteDao()
            } returns mockk {
                every {
                    runBlocking { insert(any()) }
                } returns definitionID
            }
        }
        val repository = DefinitionNoteRepository(db)
        runBlocking {
            repository.update(Note(definition, "Test"),
                              Note(other, "Test2"))
        }
    }

    @Test
    fun update_CallsUpdate(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionNoteDao = mockk<DefinitionNoteDao>{
            coEvery {
                updateNote(any(), any(), definitionID)
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns definitionID
            }
            every {
                definitionNoteDao()
            } returns definitionNoteDao
        }
        val repository = DefinitionNoteRepository(db)
        runBlocking {
            repository.update(Note(definition, "Test"),
                    Note(definition, "Test2"))
        }
        coVerify {
            definitionNoteDao.updateNote(any(), any(), definitionID)
        }
    }

    @Test
    fun delete_CallsDelete(){
        val definitionID = Random.nextLong()
        val definition = data.models.Definition("", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionNoteDao = mockk<DefinitionNoteDao>{
            coEvery {
                deleteNote(any(), definitionID)
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns mockk {
                every {
                    getDefinitionID(any(), any(), any())
                } returns definitionID
            }
            every {
                definitionNoteDao()
            } returns definitionNoteDao
        }
        val repository = DefinitionNoteRepository(db)
        val noteText = "Test"
        runBlocking {
            repository.delete(Note(definition, noteText))
        }
        coVerify {
            definitionNoteDao.deleteNote(noteText, definitionID)
        }
    }
}
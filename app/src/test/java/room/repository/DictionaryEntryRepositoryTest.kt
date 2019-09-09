package room.repository

import data.enums.Dictionary
import data.enums.Language
import data.models.Definition
import data.models.DictionaryEntry
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.entity.DefinitionDao
import room.dao.entity.VocabularyDao
import room.database.WanicchouDatabase
import room.dbo.entity.Definition.Companion.getDefinitionID
import room.dbo.entity.Vocabulary
import kotlin.random.Random
import kotlin.test.assertEquals

class DictionaryEntryRepositoryTest {
    @Test
    fun insert_AddsVocabularyIfMissing(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf())
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(any(), any(), any(), any())
            } returns null
            coEvery {
                insert(any())
            } returns Random.nextLong()
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.insert(dictionaryEntry)
        }
        coVerify {
            vocabularyDao.insert(any())
        }
    }

    @Test
    fun `insert adds all definitions`(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                                vocabulary.pronunciation,
                                vocabulary.pitch,
                                vocabulary.language)
            } returns vocabularyID
        }
        val definitionDao = mockk<DefinitionDao> {
            coEvery {
                insert(any())
            } returns Random.nextLong()
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
            every {
                definitionDao()
            } returns definitionDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.insert(dictionaryEntry)
        }
        coVerify(exactly = 2) {
            definitionDao.insert(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `update throws if definitions count are not equal`(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))
        val updatedDictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("Updated", Language.JAPANESE, Dictionary.SANSEIDO)
        ))
        val repository = DictionaryEntryRepository(mockk())
        runBlocking {
            repository.update(dictionaryEntry, updatedDictionaryEntry)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `update throws if vocabulary mismatch`(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val updatedVocabulary = data.models.Vocabulary("2", "", "", Language.JAPANESE)
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))

        val updatedDictionaryEntry = DictionaryEntry(updatedVocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))

        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns null
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.update(dictionaryEntry, updatedDictionaryEntry)
        }
    }

    @Test(expected = KotlinNullPointerException::class)
    fun `update throws if vocabulary is missing`(){
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))

        val updatedDictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns null
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.update(dictionaryEntry, updatedDictionaryEntry)
        }
    }

    @Test
    fun `update updates all given`(){
        val vocabularyID = Random.nextLong()
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val definitionIDs = listOf(Random.nextLong(), Random.nextLong())

        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("1", Language.JAPANESE, Dictionary.SANSEIDO),
                        Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        ))

        val vocabularySlot = slot<Vocabulary>()
        val definitionsSlot = mutableListOf<room.dbo.entity.Definition>()

        val updatedDefinitionList = dictionaryEntry.definitions.map {
            Definition("Updated" + it.definitionText, it.language, it.dictionary)
        }
        val updatedDictionaryEntry = DictionaryEntry(vocabulary, updatedDefinitionList)

        val definitionDao = mockk<DefinitionDao>{
            for (i in dictionaryEntry.definitions.indices){
                coEvery {
                    getDefinitionIDByDefinitionText(dictionaryEntry.definitions[i].definitionText,
                            dictionaryEntry.definitions[i].language,
                            dictionaryEntry.definitions[i].dictionary)
                } returns definitionIDs[i]
            }
            coEvery {
                update(capture(definitionsSlot))
            } just Runs
        }
        val vocabularyDao = mockk<VocabularyDao>{
            every {
                getVocabularyID(vocabulary.word,
                        vocabulary.pronunciation,
                        vocabulary.pitch,
                        vocabulary.language)
            } returns vocabularyID
            coEvery {
                update(capture(vocabularySlot))
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                vocabularyDao()
            } returns vocabularyDao
            every {
                definitionDao()
            } returns definitionDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.update(dictionaryEntry, updatedDictionaryEntry)
        }
        coVerify {
            vocabularyDao.update(any())
        }
        assertEquals(vocabulary.language, vocabularySlot.captured.language)
        assertEquals(vocabulary.word, vocabularySlot.captured.word)
        assertEquals(vocabulary.pronunciation, vocabularySlot.captured.pronunciation)
        assertEquals(vocabulary.pitch, vocabularySlot.captured.pitch)
        coVerify(exactly = 2) {
            definitionDao.update(any())
        }
        assertEquals(updatedDictionaryEntry.definitions.size, definitionsSlot.size)
        for (i in updatedDictionaryEntry.definitions.indices){
            assertEquals(updatedDefinitionList[i].definitionText,
                         definitionsSlot[i].definitionText)
            assertEquals(updatedDefinitionList[i].dictionary,
                    definitionsSlot[i].dictionary)
            assertEquals(updatedDefinitionList[i].language,
                    definitionsSlot[i].language)
            assertEquals(vocabularyID,
                    definitionsSlot[i].vocabularyID)
            assertEquals(definitionIDs[i],
                    definitionsSlot[i].definitionID)
        }
    }

    @Test
    fun `delete deletes only definition entry`(){
        //Could update this to delete the Vocabulary if the deleted definition was the last one.
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val definitionIDs = listOf(Random.nextLong(), Random.nextLong())
        val definitions = listOf(
                Definition("1", Language.JAPANESE, Dictionary.SANSEIDO),
                Definition("2", Language.ENGLISH, Dictionary.SANSEIDO))
        val dictionaryEntry = DictionaryEntry(vocabulary, definitions)

        val definitionDao = mockk<DefinitionDao>{
            for (i in definitions.indices){
                coEvery {
                    getDefinitionIDByDefinitionText(
                            definitions[i].definitionText,
                            definitions[i].language,
                            definitions[i].dictionary)
                } returns definitionIDs[i]
            }
            coEvery {
                delete(any())
            } just Runs
        }
        val db = mockk<WanicchouDatabase>{
            every {
                definitionDao()
            } returns definitionDao
        }
        val repository = DictionaryEntryRepository(db)
        runBlocking {
            repository.delete(dictionaryEntry)
        }
        coVerify(exactly = 2) {
            definitionDao.delete(any())
        }
    }
}
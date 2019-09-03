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
import kotlin.random.Random

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
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val vocabularyID = Random.nextLong()
        val definition1 = Definition("Updated", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionID1 = Random.nextLong()
        val definition2 = Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        val definitionID2 = Random.nextLong()
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                definition1,
                definition2
        ))

        val updatedDictionaryEntry = DictionaryEntry(vocabulary, listOf(
                Definition("Updated", definition1.language, definition1.dictionary),
                Definition("Updated2", definition2.language, definition2.dictionary)
        ))

        val definitionDao = mockk<DefinitionDao>{
            every {
                getDefinitionID(definition1.definitionText,
                                definition1.language,
                                definition1.dictionary)
            } returns definitionID1
            every {
                getDefinitionID(definition2.definitionText,
                                definition2.language,
                                definition2.dictionary)
            } returns definitionID2
            coEvery {
                update(any())
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
                update(any())
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
        coVerify(exactly = 2) {
            definitionDao.update(any())
        }
    }

    @Test
    fun `delete deletes only definition entry`(){
        //Could update this to delete the Vocabulary if the deleted definition was the last one.
        val vocabulary = data.models.Vocabulary("", "", "", Language.JAPANESE)
        val definition1 = Definition("Updated", Language.JAPANESE, Dictionary.SANSEIDO)
        val definitionID1 = Random.nextLong()
        val definition2 = Definition("2", Language.ENGLISH, Dictionary.SANSEIDO)
        val definitionID2 = Random.nextLong()
        val dictionaryEntry = DictionaryEntry(vocabulary, listOf(
                definition1,
                definition2
        ))

        val definitionDao = mockk<DefinitionDao>{
            every {
                getDefinitionID(definition1.definitionText,
                        definition1.language,
                        definition1.dictionary)
            } returns definitionID1
            every {
                getDefinitionID(definition2.definitionText,
                        definition2.language,
                        definition2.dictionary)
            } returns definitionID2
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
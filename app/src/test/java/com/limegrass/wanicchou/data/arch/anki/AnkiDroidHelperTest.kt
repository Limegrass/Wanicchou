package com.limegrass.wanicchou.data.arch.anki

import data.anki.*
import data.enums.Dictionary
import data.enums.Language
import data.models.Definition
import data.models.Vocabulary
import io.mockk.*
import org.junit.Test
import kotlin.random.Random

class AnkiDroidHelperTest {
    private val ankiEntry = run {
        val word = "テスト"
        val pronunciation = "てすと"
        val pitch = "3154"
        val vocabularyLanguage = Language.JAPANESE
        val vocabulary = Vocabulary(word, pronunciation, pitch, vocabularyLanguage)
        val definitionText = "this"
        val definitionLanguage = Language.ENGLISH
        val dictionary = Dictionary.SANSEIDO
        val definition = Definition(definitionText, definitionLanguage, dictionary)
        val notes = listOf("Whatever", "OK")
        WanicchouAnkiEntry(vocabulary, definition, notes)
    }


    @Test
    fun `addUpdateNote updates note`(){
        val ankiEntry = ankiEntry
        val noteID = Random.nextLong()
        val modelID = Random.nextLong()
        val deckID = Random.nextLong()
        val api = mockk<IAnkiDroidApi>{
            every {
                findDuplicateNotes(any(), any<String>())
            } returns listOf(mockk {
                every {
                    id
                } returns noteID
                every {
                    fields
                } returns arrayOf()
                every {
                    getFieldList(any())
                } returns arrayOf()
            })
            every {
                getModelName(any())
            } returns "Doesn't matter"
            every {
                updateNoteFields(any(), any())
            } returns true
            every {
                updateNoteTags(any(), any())
            } returns true
        }

        val config = mockk<IAnkiDroidConfig<WanicchouAnkiEntry>> {
            every {
                mapFromNoteFields(any())
            } returns ankiEntry
            every {
                fields
            } returns arrayOf()
            every {
                modelName
            } returns "Doesn't matter"
            every {
                mapToNoteFields(any())
            } returns arrayOf()
        }

        val storage = mockk<IAnkiDroidConfigIdentifierStorage>{
            every {
                getDeckID(any())
            } returns deckID
            every {
                getModelID(any(), any())
            } returns modelID
        }

        val ankiDroidHelper = AnkiDroidHelper(api, config, storage)
        ankiDroidHelper.addUpdateNote(ankiEntry, setOf())

        verify(exactly = 0){
            api.addNote(any(), any(), any(), any()) //wasNot Called
        }
        verify {
            api.updateNoteFields(any(), any())
            api.updateNoteTags(any(), any())
        }
    }

    @Test
    fun `addUpdateNote adds model and deck if it doesn't exist and saves IDs`(){
        val ankiEntry = ankiEntry
        val noteID = Random.nextLong()
        val modelID = Random.nextLong()
        val deckID = Random.nextLong()
        val name = "deckName"
        val api = mockk<IAnkiDroidApi>{
            every {
                findDuplicateNotes(any(), any<String>())
            } returns listOf()
            every {
                getModelName(any())
            } returns name
            every {
                getDeckName(any())
            } returns name
            every {
                getFieldList(any())
            } returns arrayOf()
            every {
                addNote(any(), any(), any(), any())
            } returns noteID
            every {
                getModelList(any())
            } returns mapOf()
            every {
                addNewCustomModel(any<IAnkiDroidConfig<WanicchouAnkiEntry>>(), any())
            } returns modelID
            every {
                addNewDeck(name)
            } returns deckID
            every {
                deckList
            } returns mapOf()
        }

        val config = mockk<IAnkiDroidConfig<WanicchouAnkiEntry>> {
            every {
                mapFromNoteFields(any())
            } returns ankiEntry
            every {
                fields
            } returns arrayOf()
            every {
                modelName
            } returns name
            every {
                mapToNoteFields(any())
            } returns arrayOf()
            every {
                deckName
            } returns name
        }

        val storage = mockk<IAnkiDroidConfigIdentifierStorage>{
            every {
                getDeckID(any())
            } returns null
            every {
                getModelID(any(), any())
            } returns null
            every {
                addModelID(any(), any())
            } just Runs
            every {
                addDeckID(any(), any())
            } just Runs
        }

        val ankiDroidHelper = AnkiDroidHelper(api, config, storage)
        ankiDroidHelper.addUpdateNote(ankiEntry, setOf())

        verify {
            api.addNewDeck(name)
            api.addNewCustomModel(config, deckID)
            storage.addModelID(name, modelID)
            storage.addDeckID(name, deckID)
        }
    }

    @Test
    fun `addUpdateNote adds note`(){
        val ankiEntry = ankiEntry
        val noteID = Random.nextLong()
        val modelID = Random.nextLong()
        val deckID = Random.nextLong()
        val api = mockk<IAnkiDroidApi>{
            every {
                findDuplicateNotes(any(), any<String>())
            } returns listOf()
            every {
                getModelName(any())
            } returns "Doesn't matter"
            every {
                getDeckName(any())
            } returns "Doesn't matter"
            every {
                getFieldList(modelID)
            } returns arrayOf()
            every {
                addNote(any(), any(), any(), any())
            } returns noteID
            every {
                deckList
            } returns mapOf()
        }

        val config = mockk<IAnkiDroidConfig<WanicchouAnkiEntry>> {
            every {
                mapFromNoteFields(any())
            } returns ankiEntry
            every {
                fields
            } returns arrayOf()
            every {
                modelName
            } returns "Doesn't matter"
            every {
                mapToNoteFields(any())
            } returns arrayOf()
            every {
                deckName
            } returns "Doesn't matter"
        }

        val storage = mockk<IAnkiDroidConfigIdentifierStorage>{
            every {
                getDeckID(any())
            } returns deckID
            every {
                getModelID(any(), any())
            } returns modelID
        }

        val ankiDroidHelper = AnkiDroidHelper(api, config, storage)
        ankiDroidHelper.addUpdateNote(ankiEntry, setOf())

        verify(exactly = 0){
            api.updateNoteFields(any(), any())
            api.updateNoteTags(any(), any())
        }
        verify {
            api.addNote(any(), any(), any(), any())
        }
    }

    @Test
    fun `addUpdateNote attempts to use API deck and model name match and saves IDs`(){
        val ankiEntry = ankiEntry
        val noteID = Random.nextLong()
        val modelID = Random.nextLong()
        val deckID = Random.nextLong()
        val name = "deckName"
        val api = mockk<IAnkiDroidApi>{
            every {
                findDuplicateNotes(any(), any<String>())
            } returns listOf()
            every {
                getModelName(any())
            } returns name
            every {
                getDeckName(any())
            } returns name
            every {
                getFieldList(any())
            } returns arrayOf()
            every {
                addNote(any(), any(), any(), any())
            } returns noteID
            every {
                getModelList(any())
            } returns mapOf(modelID to name)
            every {
                addNewCustomModel(any<IAnkiDroidConfig<WanicchouAnkiEntry>>(), any())
            } returns modelID
            every {
                addNewDeck(name)
            } returns deckID
            every {
                deckList
            } returns mapOf(deckID to name)
        }

        val config = mockk<IAnkiDroidConfig<WanicchouAnkiEntry>> {
            every {
                mapFromNoteFields(any())
            } returns ankiEntry
            every {
                fields
            } returns arrayOf()
            every {
                modelName
            } returns name
            every {
                mapToNoteFields(any())
            } returns arrayOf()
            every {
                deckName
            } returns name
        }

        val storage = mockk<IAnkiDroidConfigIdentifierStorage>{
            every {
                getDeckID(any())
            } returns null
            every {
                getModelID(any(), any())
            } returns null
            every {
                addModelID(any(), any())
            } just Runs
            every {
                addDeckID(any(), any())
            } just Runs
        }

        val ankiDroidHelper = AnkiDroidHelper(api, config, storage)
        ankiDroidHelper.addUpdateNote(ankiEntry, setOf())

        verify(exactly = 0) {
            api.addNewDeck(name)
            api.addNewCustomModel(config, deckID)
        }
        verify {
            storage.addModelID(name, modelID)
            storage.addDeckID(name, deckID)
        }
    }
}
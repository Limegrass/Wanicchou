package com.limegrass.wanicchou.data.arch.anki

import data.anki.AnkiDroidConfig
import data.anki.WanicchouAnkiEntry
import data.enums.Dictionary
import data.enums.Language
import data.models.Definition
import data.models.Vocabulary
import org.junit.Test
import kotlin.test.assertEquals

class AnkiDroidConfigTest {
    @Test
    fun `mapToNoteField maps all fields to proper labels`() {
        val ankiEntry = ankiEntry
        val fields = AnkiDroidConfig.fields
        val fieldValues = AnkiDroidConfig.mapToNoteFields(ankiEntry)
        val wordIndex = fields.indexOf("Word")
        val vocabularyLanguageIndex = fields.indexOf("Word Language")
        val definitionIndex = fields.indexOf("Definition")
        val definitionLanguageIndex = fields.indexOf("Definition Language")
        val dictionaryIndex = fields.indexOf("Dictionary")
        val pronunciationIndex = fields.indexOf("Pronunciation")
        val pitchIndex = fields.indexOf("Pitch")
        val notesIndex = fields.indexOf("Notes")
        assertEquals(fieldValues[wordIndex], ankiEntry.vocabulary.word)
        assertEquals(fieldValues[pronunciationIndex], ankiEntry.vocabulary.pronunciation)
        assertEquals(fieldValues[vocabularyLanguageIndex], ankiEntry.vocabulary.language.displayName)
        assertEquals(fieldValues[pitchIndex], ankiEntry.vocabulary.pitch)
        assertEquals(fieldValues[definitionIndex], ankiEntry.definition.definitionText)
        assertEquals(fieldValues[definitionLanguageIndex], ankiEntry.definition.language.displayName)
        assertEquals(fieldValues[dictionaryIndex], ankiEntry.definition.dictionary.dictionaryName)

        for (note in ankiEntry.notes){
            assert(fieldValues[notesIndex].contains(note))
        }
    }

    @Test
    fun `mapFromNoteField maps to and from as expected`() {
        val ankiEntry = ankiEntry
        val fieldValues = AnkiDroidConfig.mapToNoteFields(ankiEntry)
        val mappedEntry = AnkiDroidConfig.mapFromNoteFields(fieldValues)
        assertEquals(mappedEntry.vocabulary.word, ankiEntry.vocabulary.word)
        assertEquals(mappedEntry.vocabulary.pronunciation, ankiEntry.vocabulary.pronunciation)
        assertEquals(mappedEntry.vocabulary.language, ankiEntry.vocabulary.language)
        assertEquals(mappedEntry.vocabulary.pitch, ankiEntry.vocabulary.pitch)
        assertEquals(mappedEntry.definition.definitionText, ankiEntry.definition.definitionText)
        assertEquals(mappedEntry.definition.language, ankiEntry.definition.language)
        assertEquals(mappedEntry.definition.dictionary, ankiEntry.definition.dictionary)

        assertEquals(ankiEntry.notes.size, mappedEntry.notes.size)
        for (i in ankiEntry.notes.indices){
            assertEquals(ankiEntry.notes[i], mappedEntry.notes[i])
        }
    }

    @Test
    fun `mapToNoteFields maps vocabulary word as first field`() {
        val ankiEntry = ankiEntry
        val fieldValues = AnkiDroidConfig.mapToNoteFields(ankiEntry)
        assertEquals(fieldValues[0], ankiEntry.vocabulary.word)
    }

    @Test
    fun `mapToNoteFields has Anki furigana format`() {
        val furiganaField = AnkiDroidConfig.fields.indexOf("Furigana")
        val fieldValues = AnkiDroidConfig.mapToNoteFields(ankiEntry)
        assertEquals("${ankiEntry.vocabulary.word}[${ankiEntry.vocabulary.pronunciation}]",
                fieldValues[furiganaField])
    }

    @Test
    fun `mapToNoteFields has content of all notes`() {
        val notesField = AnkiDroidConfig.fields.indexOf("Notes")
        val fieldValues = AnkiDroidConfig.mapToNoteFields(ankiEntry)
        for (note in ankiEntry.notes){
            assert(fieldValues[notesField].contains(note))
        }
    }

    @Test
    fun `frontSideKey is same as first field name`() {
        assertEquals(AnkiDroidConfig.fields[0], AnkiDroidConfig.frontSideKey)
    }

    @Test
    fun `backSideKey is Definition`() {
        assertEquals("Definition", AnkiDroidConfig.backSideKey)
    }

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
}
package room.dao.composite

import data.enums.Dictionary
import data.enums.Language
import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary
import kotlin.test.assertEquals

// TODO: Revisit the expected behavior and reimplement?
//   The behaviors were not my first expectation when writing the tests
class DictionaryEntryDaoTest : AbstractDaoTest() {
    @Test
    fun searchWordLike_MatchesUnderscoreAndPercent(){
        val vocabularies = listOf(
                Vocabulary("T", "", "", Language.JAPANESE),
                Vocabulary("TE", "", "", Language.JAPANESE),
                Vocabulary("TES", "", "", Language.JAPANESE),
                Vocabulary("TEST", "", "", Language.JAPANESE),
                Vocabulary("WORD", "", "", Language.JAPANESE))
        runBlocking {
            for (vocabulary in vocabularies){
                db.vocabularyDao().insert(vocabulary)
            }
        }
        val entries = db.dictionaryEntryDao().searchWordLike("""__S%""", Language.JAPANESE, Language.JAPANESE)
        val expected = listOf("TES", "TEST")
        assertEquals(expected.size, entries.size)
        for (entry in entries){
            assertEquals(0, entry.definitions.size)
            assert(expected.contains(entry.vocabulary.word))
        }
    }

    @Test
    fun searchWordEqual_MatchesWordMatch(){
        val vocabularies = listOf(
                Vocabulary("T", "", "", Language.JAPANESE),
                Vocabulary("TE", "", "", Language.JAPANESE),
                Vocabulary("TES", "", "", Language.JAPANESE),
                Vocabulary("TEST", "", "", Language.JAPANESE),
                Vocabulary("WORD", "", "", Language.JAPANESE))
        runBlocking {
            for (vocabulary in vocabularies){
                db.vocabularyDao().insert(vocabulary)
            }
        }
        // Seems it's case sensitive
        val entries = db.dictionaryEntryDao().searchWordEqual("""TEST""", Language.JAPANESE, Language.JAPANESE)
        val expected = listOf("TEST")
        assertEquals(expected.size, entries.size)
        for (entry in entries){
            assertEquals(0, entry.definitions.size)
            assert(expected.contains(entry.vocabulary.word))
        }
    }

    @Test
    fun searchDefinitionLike_MatchesDefinitionUnderscoreAndPercent(){
        val vocabularies = listOf(
                Vocabulary("TEST", "", "", Language.JAPANESE, 1),
                Vocabulary("WORD", "", "", Language.JAPANESE, 2))
        runBlocking {
            for (vocabulary in vocabularies){
                db.vocabularyDao().insert(vocabulary)
            }
        }
        val definitions = listOf(
                Definition("JPN", Language.JAPANESE, Dictionary.SANSEIDO, 1),
                Definition("ENG", Language.ENGLISH, Dictionary.SANSEIDO, 1)
        )
        for(definition in definitions){
            runBlocking {
                db.definitionDao().insert(definition)
            }
        }
        val entries = db.dictionaryEntryDao().searchDefinitionLike("""_P%""", Language.JAPANESE, Language.JAPANESE)
        val expected = listOf("TEST")
        assertEquals(expected.size, entries.size)
        assert(entries.single().definitions.any { it.definitionText.contains(".P.*".toRegex()) } )
        for (entry in entries){
            // Looks like the @Relation tag brings back both definition matches despite language mismatch
            assertEquals(2, entry.definitions.size)
            assert(expected.contains(entry.vocabulary.word))
        }
    }

    @Test
    fun searchWordOrDefinitionLike_MatchesWordAndDefinitionUnderscoreAndPercent() {
        val vocabularies = listOf(
                Vocabulary("TEST", "", "", Language.JAPANESE, 1),
                Vocabulary("WORD", "", "", Language.JAPANESE, 2))
        runBlocking {
            for (vocabulary in vocabularies){
                db.vocabularyDao().insert(vocabulary)
            }
        }
        val definitions = listOf(
                Definition("JPN", Language.JAPANESE, Dictionary.SANSEIDO, 1),
                Definition("Test", Language.JAPANESE, Dictionary.SANSEIDO, 2)
        )
        for(definition in definitions){
            runBlocking {
                db.definitionDao().insert(definition)
            }
        }
        val entries = db.dictionaryEntryDao().searchWordOrDefinitionLike("""_ES%""",
                                                                         Language.JAPANESE,
                                                                         Language.JAPANESE)
        val expected = listOf("TEST", "WORD")
        val regex = """.ES.*""".toRegex()
        assertEquals(expected.size, entries.size)
        for (entry in entries){
            assertEquals(1, entry.definitions.size)
            assert(entry.vocabulary.word.contains(regex)
                    || entry.definitions.any { it.definitionText.contains(regex) })
        }
    }
}
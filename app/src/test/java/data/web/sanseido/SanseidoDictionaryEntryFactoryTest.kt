package data.web.sanseido

import data.enums.Dictionary
import data.enums.Language
import org.jsoup.Jsoup
import org.junit.Test
import kotlin.test.asserter

//TODO: Add more test cases
// One that has pitch and uses the exact regex matcher/other
class SanseidoDictionaryEntryFactoryTest {
    @Test
    fun getDictionaryEntries_ExactMatchWord(){
        val filePath = "/data/web/sanseido/JPN-ENGテスト.html" // /app/src/test/...
        val documentString = SanseidoDictionaryEntryFactoryTest::class.java
                .getResource(filePath)!!
                .readText()
        val document = Jsoup.parse(documentString)
        val dictionaryEntryFactory = SanseidoDictionaryEntryFactory()
        val dictionaryEntries = dictionaryEntryFactory.getDictionaryEntries(document,
                                                                          Language.JAPANESE,
                                                                          Language.ENGLISH)
        asserter.assertTrue("Empty DictionaryEntry array",
                            dictionaryEntries.isNotEmpty())
        asserter.assertTrue("Expected 2 elements",
                            dictionaryEntries.size == 2)
        val dictionaryEntry = dictionaryEntries.single{
            it.definitions.isNotEmpty()
        }
        asserter.assertEquals("Word does not match",
                "テスト",
                dictionaryEntry.vocabulary.word)
        asserter.assertEquals("Pronunciation does not match",
                "テスト",
                dictionaryEntry.vocabulary.pronunciation)
        asserter.assertEquals("Pitch not empty",
                "",
                dictionaryEntry.vocabulary.pitch)
        asserter.assertEquals("VocabularyLanguage should be Japanese",
                Language.JAPANESE,
                dictionaryEntry.vocabulary.language)

        asserter.assertEquals("Expected only 1 definition",
                1,
                dictionaryEntry.definitions.size)
        val definition = dictionaryEntry.definitions[0]
        asserter.assertEquals("DefinitionText does not match",
                "a test． ・～する（を受ける）　give (take) a test ((in, for))． " +
                        "◆テスト・ケース　a test case． " +
                        "◆テスト・パイロット　a test pilot． ◆テスト・パターン　a test pattern．",
                definition.definitionText)
        asserter.assertEquals("Expected definition to be English.",
                Language.ENGLISH,
                definition.language)
        asserter.assertEquals("Expected definition to be English.",
                Dictionary.SANSEIDO,
                definition.dictionary)

        val otherEntry = dictionaryEntries.single{
            it != dictionaryEntry
        }
        asserter.assertEquals("Expected no definitions",
                0,
                otherEntry.definitions.size)
        asserter.assertEquals("Expected repeat",
                dictionaryEntry.vocabulary.word,
                otherEntry.vocabulary.word)
        asserter.assertEquals("Expected repeat",
                dictionaryEntry.vocabulary.pronunciation,
                otherEntry.vocabulary.pronunciation)
        asserter.assertEquals("Expected repeat",
                dictionaryEntry.vocabulary.pitch,
                otherEntry.vocabulary.pitch)
    }
}
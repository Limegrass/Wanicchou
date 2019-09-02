package data.enums

import org.junit.Test
import kotlin.test.asserter

class LanguageTest {
    @Test
    fun displayName_Japanese(){
        asserter.assertEquals("Not translated to target language's alphabet",
                "日本語",
                Language.JAPANESE.displayName)
    }

    @Test
    fun displayName_English(){
        asserter.assertEquals("Not translated to target language's alphabet",
                "English",
                Language.ENGLISH.displayName)
    }

    @Test
    fun languageCode_Japanese(){
        asserter.assertEquals("Didn't get Japanese ISO3 code.",
                "jpn",
                Language.JAPANESE.languageCode)
    }

    @Test
    fun languageCode_English(){
        asserter.assertEquals("Didn't get English ISO3 code",
                "eng",
                Language.ENGLISH.languageCode)
    }

    @Test
    fun getLanguageID_Japanese(){
        asserter.assertEquals("Japanese LanguageID has changed from 1",
                1L,
                Language.JAPANESE.languageID)
    }

    @Test
    fun getLanguageID_English(){
        asserter.assertEquals("English LanguageID has changed from 2",
                2L,
                Language.ENGLISH.languageID)
    }

    @Test
    fun getLanguage_Japanese(){
        asserter.assertEquals("Expected Language.JAPANESE",
                Language.JAPANESE,
                Language.getLanguage(Language.JAPANESE.languageID))
    }

    @Test
    fun getLanguage_English(){
        asserter.assertEquals("Expected Language.ENGLISH",
                Language.ENGLISH,
                Language.getLanguage(Language.ENGLISH.languageID))
    }
}
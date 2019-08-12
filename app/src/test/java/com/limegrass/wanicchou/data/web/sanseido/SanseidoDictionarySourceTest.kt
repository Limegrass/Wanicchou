package com.limegrass.wanicchou.data.web.sanseido

import data.arch.search.SearchRequest
import data.enums.Language
import data.enums.MatchType
import data.web.sanseido.SanseidoSource
import org.junit.Test
import kotlin.test.asserter

// Reminder: unsupported language tests when new languages are introduced.
class SanseidoDictionarySourceTest {
    private val dictionarySource = SanseidoSource()
    @Test
    fun buildQueryURL_SupportedMatchType(){
        val searchTerm = "テスト"
        val wordLanguage = Language.JAPANESE
        val definitionLanguage = Language.ENGLISH
        val matchType = MatchType.WORD_EQUALS
        val searchRequest = SearchRequest(searchTerm,
                                          wordLanguage,
                                          definitionLanguage,
                                          matchType)
        val actual = dictionarySource.buildSearchQueryURL(searchRequest).toString()
        val expected = "https://www.sanseido.biz/User/Dic/Index.aspx" +
                "?st=1&TWords=%E3%83%86%E3%82%B9%E3%83%88&DORDER=151716&DailyJE=checkbox"
        val message = "Unexpected search URL"
        asserter.assertEquals(message, expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildQueryURL_UnsupportedMatchType(){
        val searchTerm = "Test"
        val wordLanguage = Language.JAPANESE
        val definitionLanguage = Language.ENGLISH
        val matchType = MatchType.DEFINITION_CONTAINS
        val searchRequest = SearchRequest(searchTerm, wordLanguage, definitionLanguage, matchType)
        dictionarySource.buildSearchQueryURL(searchRequest)
    }

    @Test
    fun supportedMatchTypes_HasWordEqual(){
        val hasMatchType = dictionarySource.supportedMatchTypes.contains(MatchType.WORD_EQUALS)
        asserter.assertTrue("Missing MatchType Equals", hasMatchType)
    }

    @Test
    fun supportedMatchTypes_HasWordStartsWith(){
        val hasMatchType = dictionarySource.supportedMatchTypes.contains(MatchType.WORD_STARTS_WITH)
        asserter.assertTrue("Missing MatchType Word Starts With", hasMatchType)
    }

    @Test
    fun supportedMatchTypes_HasWordEndsWith(){
        val hasMatchType = dictionarySource.supportedMatchTypes.contains(MatchType.WORD_ENDS_WITH)
        asserter.assertTrue("Missing MatchType Word Ends With", hasMatchType)
    }

    @Test
    fun supportedMatchTypes_HasWordContains(){
        val hasMatchType = dictionarySource.supportedMatchTypes.contains(MatchType.WORD_CONTAINS)
        asserter.assertTrue("Missing MatchType Word Or Definition Contains", hasMatchType)
    }

    @Test
    fun supportedMatchTypes_HasWordOrDefinitionContains(){
        val hasMatchType = dictionarySource.supportedMatchTypes.contains(MatchType.WORD_OR_DEFINITION_CONTAINS)
        asserter.assertTrue("Missing MatchType Word Or Definition Contains", hasMatchType)
    }

    @Test
    fun supportedTranslations_JapaneseInput(){
        val japaneseInputTranslations = dictionarySource.supportedTranslations[Language.JAPANESE]
                ?: error("Japanese Language Input missing")
        asserter.assertTrue("Missing MatchType Word Or Definition Contains",
                japaneseInputTranslations.contains(Language.JAPANESE))
        asserter.assertTrue("Missing MatchType Word Or Definition Contains",
                japaneseInputTranslations.contains(Language.ENGLISH))
    }

    @Test
    fun supportedTranslations_EnglishInput(){
        val englishInputTranslations = dictionarySource.supportedTranslations[Language.JAPANESE]
                ?: error("English Language input missing")
        asserter.assertTrue("Missing MatchType Word Or Definition Contains",
                englishInputTranslations.contains(Language.JAPANESE))
    }
}

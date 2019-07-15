package com.limegrass.wanicchou.data.web.sanseido

import data.enums.MatchType
import data.web.sanseido.SanseidoWebPage
import org.junit.Test
import kotlin.test.asserter

class SanseidoDictionaryWebPageTest {
    @Test
    fun buildQueryURL_SupportedMatchType(){
        val webPage = SanseidoWebPage()
        val searchTerm = "テスト"
        val wordLanguageID = 1L
        val definitionLanguageID = 1L
        val matchType = MatchType.WORD_STARTS_WITH
        val actual = webPage.buildQueryURL(searchTerm, wordLanguageID, definitionLanguageID, matchType)
        val expected = "https://www.sanseido.biz/User/Dic/Index.aspx?st=1&TWords=テスト&DORDER=151716&DailyJJ=checkbox"
        val message = "Expected: $expected, got $actual"
        asserter.assertEquals(message, expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildQueryURL_UnsupportedMatchType(){
        val webPage = SanseidoWebPage()
        val searchTerm = "Test"
        val wordLanguageID = 1L
        val definitionLanguageID = 1L
        val matchType = MatchType.DEFINITION_CONTAINS
        webPage.buildQueryURL(searchTerm, wordLanguageID, definitionLanguageID, matchType)
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildQueryURL_UnsupportedWordLanguageID(){
        val webPage = SanseidoWebPage()
        val searchTerm = "Test"
        val wordLanguageID = 1L
        val definitionLanguageID = -1L
        val matchType = MatchType.WORD_STARTS_WITH
        webPage.buildQueryURL(searchTerm, wordLanguageID, definitionLanguageID, matchType)
    }
}

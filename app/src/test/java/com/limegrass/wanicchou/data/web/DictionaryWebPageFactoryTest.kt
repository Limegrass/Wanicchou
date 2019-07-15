package com.limegrass.wanicchou.data.web

import data.web.DictionaryWebPageFactory
import data.web.sanseido.SanseidoWebPage
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.asserter

class DictionaryWebPageFactoryTest {
    @Test
    fun get_SanseidoDictionaryWebPage(){
        val factory = DictionaryWebPageFactory(SanseidoWebPage.DICTIONARY_ID)
        val webPage = factory.get()
        val webPageType = webPage::class.java.simpleName
        val message = "Expected SanseidoWebPage type, got $webPageType"
        asserter.assertEquals(message, webPageType, SanseidoWebPage::class.java.simpleName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun get_ThrowUnsupported(){
        DictionaryWebPageFactory(-1).get()
    }
}

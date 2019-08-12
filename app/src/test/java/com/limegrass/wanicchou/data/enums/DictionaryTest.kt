package com.limegrass.wanicchou.data.enums

import data.enums.Dictionary
import data.enums.Language
import org.junit.Test
import kotlin.test.asserter

class DictionaryTest {

    @Test
    fun dictionaryID_Sanseido(){
        asserter.assertEquals(
                "Sanseido DictionaryID changed",
                1L,
                Dictionary.SANSEIDO.dictionaryID)
    }

    @Test
    fun dictionaryName_Sanseido(){
        asserter.assertEquals(
                "Unexpected Sanseido Dictionary Name.",
                "三省堂",
                Dictionary.SANSEIDO.dictionaryName)
    }

    @Test
    fun defaultVocabularyLanguage_Sanseido(){
        asserter.assertEquals(
                "Sanseido default vocabulary language no longer Japanese",
                Language.JAPANESE,
                Dictionary.SANSEIDO.defaultVocabularyLanguage)
    }

    @Test
    fun defaultDefinitionLanguage_Sanseido(){
        asserter.assertEquals(
                "Sanseido default definition language no longer Japanese",
                Language.JAPANESE,
                Dictionary.SANSEIDO.defaultDefinitionLanguage)
    }

    @Test
    fun getDictionary_Sanseido(){
        asserter.assertEquals(
                "Didn't get Sanseido enum type from it's dictionary ID",
                        Dictionary.SANSEIDO,
                Dictionary.getDictionary(Dictionary.SANSEIDO.dictionaryID))
    }

}

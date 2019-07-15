package com.limegrass.wanicchou.data.arch.lang

import data.arch.lang.JapaneseVocabulary
import data.arch.lang.JapaneseVocabulary.Companion.isJapaneseInput
import org.junit.Test
import kotlin.test.asserter

class JapaneseVocabularyTest {

    @Test
    fun isolateWord_Empty(){
        val inputString = ""
        val isolatedString = JapaneseVocabulary.isolateWord(inputString)
        val message = "Expected $inputString, Got $isolatedString"
        asserter.assertEquals(message, inputString, isolatedString)
    }

    @Test
    fun isolateWord_NoJapanese(){
        val inputString = "123ABC123 Test BTW"
        val isolatedString = JapaneseVocabulary.isolateWord(inputString)
        val message = "Expected $inputString, Got $isolatedString"
        asserter.assertEquals(message, inputString, isolatedString)
    }

    @Test
    fun isolateWord_KanaMatch(){
        val inputString = "もうテストをかきたくない"
        val isolatedString = JapaneseVocabulary.isolateWord("I love testing! $inputString")
        val message = "Expected $inputString, Got $isolatedString"
        asserter.assertEquals(message, inputString, isolatedString)
    }

    @Test
    fun isolateWord_KanjiMatch(){
        val inputString = "書きたくない"
        val isolatedString = JapaneseVocabulary.isolateWord("もうテストを$inputString")
        val message = "Expected $inputString, Got $isolatedString"
        asserter.assertEquals(message, inputString, isolatedString)
    }

    @Test
    fun isolatePitch_FullWidthMatch(){
        val inputString = "ゆき ２［雪］"
        val isolatedString = JapaneseVocabulary.isolatePitch(inputString)
        val message = "Expected ２, Got $isolatedString"
        asserter.assertEquals(message, "２", isolatedString)
    }

    @Test
    fun isolatePitch_ASCIIMatch(){
        val inputString = "ゆき 2 ［雪］"
        val isolatedString = JapaneseVocabulary.isolatePitch(inputString)
        val message = "Expected 2, Got $isolatedString"
        asserter.assertEquals(message, "2", isolatedString)
    }

    // TODO: Expand on isJapaneseInput to allow romaaji one day.
    @Test
    fun isJapaneseInput_IsEnglish(){
        val input = "English"
        val isJapaneseInput = input.isJapaneseInput()
        asserter.assertTrue("$input.isJapaneseInput() returned $isJapaneseInput, " +
                "expected ${!isJapaneseInput}", !isJapaneseInput)
    }

    @Test
    fun isJapaneseInput_IsJapanese(){
        val input = "日本語"
        val isJapaneseInput = input.isJapaneseInput()
        asserter.assertTrue("$input.isJapaneseInput() returned ${!isJapaneseInput}, " +
                "expected $isJapaneseInput", isJapaneseInput)
    }
}
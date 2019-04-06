package com.limegrass.wanicchou

/**
 * Test for Sanseidou Search
 * Also somewhat indirectly Japanese DictionaryEntry
 */
//class SanseidoSearchTest {
//
//    @Test
//    @Throws(Exception::class)
//    fun testJJExact() {
//        val sanseidoSearch = SanseidoSearch("アニメ", JapaneseDictionaryType.JJ, SanseidouMatchType.EXACT)
//        assertEquals(sanseidoSearch.dictionaryEntry!!.word, "アニメ")
//        assertEquals(sanseidoSearch.dictionaryEntry!!.dictionaryType, JapaneseDictionaryType.JJ)
//        assertEquals(sanseidoSearch.dictionaryEntry!!.definition, "アニメーションの略．")
//        assertEquals(sanseidoSearch.dictionaryEntry!!.pitch, "1")
//        assertEquals(sanseidoSearch.relatedWords!!.size, 1)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testJEForwards() {
//        val sanseidoSearch = SanseidoSearch("雪", JapaneseDictionaryType.JE, SanseidouMatchType.FORWARDS)
//        assertEquals(sanseidoSearch.dictionaryEntry!!.word, "雪害")
//        assertEquals(sanseidoSearch.dictionaryEntry!!.dictionaryType, JapaneseDictionaryType.JE)
//        assertEquals(sanseidoSearch.dictionaryEntry!!.definition, "snow damage．")
//        assertEquals(sanseidoSearch.dictionaryEntry!!.pitch, "")
//        val size = sanseidoSearch.relatedWords!!.size
//        assertEquals(size, 20)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testEmptyWord() {
//        try {
//            val sanseidoSearch = SanseidoSearch("", JapaneseDictionaryType.EJ, SanseidouMatchType.BACKWARDS)
//            Assert.fail("Should have thrown IllegalArgumentException.")
//        } catch (e: IllegalArgumentException) {
//
//        }
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testNullDictionaryType() {
//        try {
//            val sanseidoSearch = SanseidoSearch("テスト", null, SanseidouMatchType.EXACT)
//            Assert.fail("Should have thrown IllegalArgumentException.")
//        } catch (e: IllegalArgumentException) {
//        }
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testNullMatchType() {
//        try {
//            val sanseidoSearch = SanseidoSearch("テスト", JapaneseDictionaryType.JJ, null)
//            Assert.fail("Should have thrown IllegalArgumentException.")
//        } catch (e: IllegalArgumentException) {
//        }
//
//    }
//}
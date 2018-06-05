package com.waifusims.wanicchou;

import junit.framework.Assert;

import org.junit.Test;

import data.vocab.DictionaryType;
import data.vocab.MatchType;
import data.vocab.search.SanseidoSearch;

import static junit.framework.Assert.assertEquals;

/**
 * Test for Sanseido Search
 * Also somehwat indirectly Japanese Vocabulary
 */
public class SanseidoSearchTest {

    @Test
    public void testJJExact() throws Exception {
        SanseidoSearch sanseidoSearch =
                new SanseidoSearch("アニメ", DictionaryType.JJ, MatchType.EXACT);
        assertEquals(sanseidoSearch.getVocabulary().getWord(), "アニメ");
        assertEquals(sanseidoSearch.getVocabulary().getDictionaryType(), DictionaryType.JJ);
        assertEquals(sanseidoSearch.getVocabulary().getDefintion(), "アニメーションの略．");
        assertEquals(sanseidoSearch.getVocabulary().getPitch(), "1");
        assertEquals(sanseidoSearch.getRelatedWords().size(), 1);
    }

    @Test
    public void testJEForwards() throws Exception {
        SanseidoSearch sanseidoSearch =
                new SanseidoSearch("雪", DictionaryType.JE, MatchType.FORWARDS);
        assertEquals(sanseidoSearch.getVocabulary().getWord(), "雪害");
        assertEquals(sanseidoSearch.getVocabulary().getDictionaryType(), DictionaryType.JE);
        assertEquals(sanseidoSearch.getVocabulary().getDefintion(), "snow damage．");
        assertEquals(sanseidoSearch.getVocabulary().getPitch(), "");
        int size = 0;
        for (DictionaryType dictionaryType : sanseidoSearch.getRelatedWords().keySet()){
            size += sanseidoSearch.getRelatedWords().get(dictionaryType).size();
        }
        assertEquals(size, 20);
    }

    @Test
    public void testEmptyWord() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("", DictionaryType.EJ, MatchType.BACKWARDS);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){

        }

    }

    @Test
    public void testNullDictionaryType() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("テスト", null, MatchType.EXACT);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }

    @Test
    public void testNullMatchType() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("テスト", DictionaryType.JJ, null);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }
}
package com.waifusims.wanicchou;

import junit.framework.Assert;

import org.junit.Test;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.search.sanseido.SanseidoMatchType;
import data.vocab.jp.search.sanseido.SanseidoSearch;

import static junit.framework.Assert.assertEquals;

/**
 * Test for Sanseido Search
 * Also somewhat indirectly Japanese Vocabulary
 */
public class SanseidoSearchTest {

    @Test
    public void testJJExact() throws Exception {
        SanseidoSearch sanseidoSearch =
                new SanseidoSearch("アニメ", JapaneseDictionaryType.JJ, SanseidoMatchType.EXACT);
        assertEquals(sanseidoSearch.getVocabulary().getWord(), "アニメ");
        assertEquals(sanseidoSearch.getVocabulary().getDictionaryType(), JapaneseDictionaryType.JJ);
        assertEquals(sanseidoSearch.getVocabulary().getDefinition(), "アニメーションの略．");
        assertEquals(sanseidoSearch.getVocabulary().getPitch(), "1");
        assertEquals(sanseidoSearch.getRelatedWords().size(), 1);
    }

    @Test
    public void testJEForwards() throws Exception {
        SanseidoSearch sanseidoSearch =
                new SanseidoSearch("雪", JapaneseDictionaryType.JE, SanseidoMatchType.FORWARDS);
        assertEquals(sanseidoSearch.getVocabulary().getWord(), "雪害");
        assertEquals(sanseidoSearch.getVocabulary().getDictionaryType(), JapaneseDictionaryType.JE);
        assertEquals(sanseidoSearch.getVocabulary().getDefinition(), "snow damage．");
        assertEquals(sanseidoSearch.getVocabulary().getPitch(), "");
        int size = sanseidoSearch.getRelatedWords().size();
        assertEquals(size, 20);
    }

    @Test
    public void testEmptyWord() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("", JapaneseDictionaryType.EJ, SanseidoMatchType.BACKWARDS);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){

        }

    }

    @Test
    public void testNullDictionaryType() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("テスト", null, SanseidoMatchType.EXACT);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }

    @Test
    public void testNullMatchType() throws Exception {
        try{
            SanseidoSearch sanseidoSearch =
                    new SanseidoSearch("テスト", JapaneseDictionaryType.JJ, null);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }
}
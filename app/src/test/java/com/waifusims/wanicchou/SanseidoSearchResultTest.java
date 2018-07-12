package com.waifusims.wanicchou;

import junit.framework.Assert;

import org.junit.Test;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.search.sanseido.SanseidoMatchType;
import data.vocab.jp.search.sanseido.SanseidoSearchResult;

import static junit.framework.Assert.assertEquals;

/**
 * Test for Sanseido SearchResult
 * Also somewhat indirectly Japanese Vocabulary
 */
public class SanseidoSearchResultTest {

    @Test
    public void testJJExact() throws Exception {
        SanseidoSearchResult sanseidoSearch =
                new SanseidoSearchResult("アニメ", JapaneseDictionaryType.JJ, SanseidoMatchType.EXACT);
        assertEquals(sanseidoSearch.getVocabulary().getWord(), "アニメ");
        assertEquals(sanseidoSearch.getVocabulary().getDictionaryType(), JapaneseDictionaryType.JJ);
        assertEquals(sanseidoSearch.getVocabulary().getDefinition(), "アニメーションの略．");
        assertEquals(sanseidoSearch.getVocabulary().getPitch(), "1");
        assertEquals(sanseidoSearch.getRelatedWords().size(), 1);
    }

    @Test
    public void testJEForwards() throws Exception {
        SanseidoSearchResult sanseidoSearch =
                new SanseidoSearchResult("雪", JapaneseDictionaryType.JE, SanseidoMatchType.FORWARDS);
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
            SanseidoSearchResult sanseidoSearch =
                    new SanseidoSearchResult("", JapaneseDictionaryType.EJ, SanseidoMatchType.BACKWARDS);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){

        }

    }

    @Test
    public void testNullDictionaryType() throws Exception {
        try{
            SanseidoSearchResult sanseidoSearch =
                    new SanseidoSearchResult("テスト", null, SanseidoMatchType.EXACT);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }

    @Test
    public void testNullMatchType() throws Exception {
        try{
            SanseidoSearchResult sanseidoSearch =
                    new SanseidoSearchResult("テスト", JapaneseDictionaryType.JJ, null);
            Assert.fail("Should have thrown IllegalArgumentException.");
        }
        catch (IllegalArgumentException e){ }
    }
}
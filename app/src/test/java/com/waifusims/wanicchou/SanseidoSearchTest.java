package com.waifusims.wanicchou;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import data.vocab.search.SanseidoSearch;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SanseidoSearchTest extends InstrumentationTestCase {
    Context mContext;

    @Before
    public void setUp() throws  Exception{
        super.setUp();
        mContext = new MockContext();
        assertNotNull(mContext);
    }

    @Test
    public void urlGenerated() throws Exception {
        System.out.println(SanseidoSearch.buildQueryURL("アニメ", true).toString());
        assertEquals(SanseidoSearch.buildQueryURL("アニメ", true).toString(),
                "https://www.sanseido.biz/User/Dic/Index.aspx?st=0&DORDER=171615&DailyJJ=checkbox&TWords=%E3%82%A2%E3%83%8B%E3%83%A1");
    }
}
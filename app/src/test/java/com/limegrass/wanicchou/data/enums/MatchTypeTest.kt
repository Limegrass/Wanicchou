package com.limegrass.wanicchou.data.enums

import data.enums.MatchType
import org.junit.Test
import kotlin.test.asserter


class MatchTypeTest {
    @Test
    fun templateString_WordEquals(){
        asserter.assertEquals("Unexpected template string",
                "%s",
                MatchType.WORD_EQUALS.templateString)
    }

    @Test
    fun templateString_WordStartsWith(){
        asserter.assertEquals("Unexpected template string",
                "%s%%",
                MatchType.WORD_STARTS_WITH.templateString)
    }

    @Test
    fun templateString_WordEndsWith(){
        asserter.assertEquals("Unexpected template string",
                "%%%s",
                MatchType.WORD_ENDS_WITH.templateString)
    }

    @Test
    fun templateString_WordContains(){
        asserter.assertEquals("Unexpected template string",
                "%%%s%%",
                MatchType.WORD_CONTAINS.templateString)
    }

    @Test
    fun templateString_WordWildcards(){
        asserter.assertEquals("Unexpected template string",
                "%s",
                MatchType.WORD_WILDCARDS.templateString)
    }

    @Test
    fun templateString_DefinitionContains(){
        asserter.assertEquals("Unexpected template string",
                "%%%s%%",
                MatchType.DEFINITION_CONTAINS.templateString)
    }

    @Test
    fun templateString_WordOrDefinitionContains(){
        asserter.assertEquals("Unexpected template string",
                "%%%s%%",
                MatchType.WORD_OR_DEFINITION_CONTAINS.templateString)
    }
}
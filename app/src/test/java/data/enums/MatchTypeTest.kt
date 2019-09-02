package data.enums

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

    @Test
    fun matchTypeID_WordEquals(){
        asserter.assertEquals("MatchTypeID changed",
                1L,
                MatchType.WORD_EQUALS.matchTypeID)
    }

    @Test
    fun matchTypeID_WordStartsWith(){
        asserter.assertEquals("MatchTypeID changed",
                2L,
                MatchType.WORD_STARTS_WITH.matchTypeID)
    }
    @Test
    fun matchTypeID_WordEndsWith(){
        asserter.assertEquals("MatchTypeID changed",
                3L,
                MatchType.WORD_ENDS_WITH.matchTypeID)
    }
    @Test
    fun matchTypeID_WordContains(){
        asserter.assertEquals("MatchTypeID changed",
                4L,
                MatchType.WORD_CONTAINS.matchTypeID)
    }
    @Test
    fun matchTypeID_WordWildcards(){
        asserter.assertEquals("MatchTypeID changed",
                5L,
                MatchType.WORD_WILDCARDS.matchTypeID)
    }
    @Test
    fun matchTypeID_DefinitionContains(){
        asserter.assertEquals("MatchTypeID changed",
                6L,
                MatchType.DEFINITION_CONTAINS.matchTypeID)
    }
    @Test
    fun matchTypeID_WordOrDefinitionContains(){
        asserter.assertEquals("MatchTypeID changed",
                7L,
                MatchType.WORD_OR_DEFINITION_CONTAINS.matchTypeID)
    }

    @Test
    fun getMatchType_WordEquals(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_EQUALS,
                MatchType.getMatchType(MatchType.WORD_EQUALS.matchTypeID))
    }
    @Test
    fun getMatchType_WordStartsWith(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_STARTS_WITH,
                MatchType.getMatchType(MatchType.WORD_STARTS_WITH.matchTypeID))
    }
    @Test
    fun getMatchType_WordEndsWith(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_ENDS_WITH,
                MatchType.getMatchType(MatchType.WORD_ENDS_WITH.matchTypeID))
    }
    @Test
    fun getMatchType_WordContains(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_CONTAINS,
                MatchType.getMatchType(MatchType.WORD_CONTAINS.matchTypeID))
    }
    @Test
    fun getMatchType_WordWildcards(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_WILDCARDS,
                MatchType.getMatchType(MatchType.WORD_WILDCARDS.matchTypeID))
    }
    @Test
    fun getMatchType_DefinitionContains(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.DEFINITION_CONTAINS,
                MatchType.getMatchType(MatchType.DEFINITION_CONTAINS.matchTypeID))
    }
    @Test
    fun getMatchType_WordOrDefinitionContains(){
        asserter.assertEquals("getMatchType didn't return same enum type as ID",
                MatchType.WORD_OR_DEFINITION_CONTAINS,
                MatchType.getMatchType(MatchType.WORD_OR_DEFINITION_CONTAINS.matchTypeID))
    }
}
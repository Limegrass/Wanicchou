package room.search

import data.enums.MatchType
import org.junit.Test
import kotlin.test.asserter

class DatabaseSearchStrategyFactoryTest {
    @Test
    fun get_WordEquals(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_EQUALS).get()
        val actualClass = searchStrategy::class
        val expectedClass = WordEqualsDatabaseSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }

    @Test
    fun get_WordWildCards(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_WILDCARDS).get()
        val actualClass = searchStrategy::class
        val expectedClass = WordLikeDatabaseSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }

    @Test
    fun get_WordStartsWith(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_STARTS_WITH).get()
        val actualClass = searchStrategy::class

        val expectedClass = WordLikeDatabaseSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }
    @Test
    fun get_WordEndsWith(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_ENDS_WITH).get()
        val actualClass = searchStrategy::class
        val expectedClass = WordLikeDatabaseSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }
    @Test
    fun get_WordContains(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_CONTAINS).get()
        val actualClass = searchStrategy::class
        val expectedClass = WordLikeDatabaseSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }
    @Test
    fun get_DefinitionContains(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.DEFINITION_CONTAINS).get()
        val actualClass = searchStrategy::class
        val expectedClass = DefinitionLikeSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }
    @Test
    fun get_WordOrDefinitionContains(){
        val searchStrategy = DatabaseSearchStrategyFactory(MatchType.WORD_OR_DEFINITION_CONTAINS).get()
        val actualClass = searchStrategy::class
        val expectedClass = WordOrDefinitionLikeSearchStrategy::class
        asserter.assertEquals("Wrong search strategy returned",
                expectedClass,
                actualClass)
    }
}
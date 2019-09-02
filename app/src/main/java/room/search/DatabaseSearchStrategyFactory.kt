package room.search

import data.architecture.IFactory
import data.enums.MatchType

class DatabaseSearchStrategyFactory(private val matchType: MatchType)
    : IFactory<IDatabaseSearchStrategy> {
    override fun get(): IDatabaseSearchStrategy {
        return when (matchType) {
            MatchType.WORD_EQUALS -> WordEqualsDatabaseSearchStrategy()
            MatchType.WORD_WILDCARDS -> WordLikeDatabaseSearchStrategy()
            MatchType.WORD_STARTS_WITH -> WordLikeDatabaseSearchStrategy()
            MatchType.WORD_ENDS_WITH -> WordLikeDatabaseSearchStrategy()
            MatchType.WORD_CONTAINS -> WordLikeDatabaseSearchStrategy()
            MatchType.DEFINITION_CONTAINS -> DefinitionLikeSearchStrategy()
            MatchType.WORD_OR_DEFINITION_CONTAINS -> WordOrDefinitionLikeSearchStrategy()
        }
    }
}
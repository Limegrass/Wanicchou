package data.enums

enum class MatchType {
    WORD_EQUALS,
    WORD_STARTS_WITH,
    WORD_ENDS_WITH,
    WORD_CONTAINS,
    WORD_WILDCARDS,
    DEFINITION_CONTAINS,
    WORD_OR_DEFINITION_CONTAINS;
    fun getBitMask() : Int {
        return when (this) {
            WORD_EQUALS -> 1
            WORD_STARTS_WITH -> 2
            WORD_ENDS_WITH -> 4
            WORD_CONTAINS -> 8
            WORD_WILDCARDS -> 16
            DEFINITION_CONTAINS -> 32
            WORD_OR_DEFINITION_CONTAINS -> 64
        }
    }
    fun getMatchTypeID() : Long {
        return when (this) {
            WORD_EQUALS -> 1
            WORD_STARTS_WITH -> 2
            WORD_ENDS_WITH -> 3
            WORD_CONTAINS -> 4
            WORD_WILDCARDS -> 5
            DEFINITION_CONTAINS -> 6
            WORD_OR_DEFINITION_CONTAINS -> 7
        }
    }
}
//    WORD_FUZZY_CONTAINS,

// 2+ 1 + 4     + 32 + 8

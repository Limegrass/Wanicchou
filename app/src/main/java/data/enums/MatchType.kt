package data.enums

enum class MatchType {
    WORD_EQUALS,
    WORD_STARTS_WITH,
    WORD_ENDS_WITH,
    WORD_CONTAINS,
    WORD_WILDCARDS,
    DEFINITION_CONTAINS,
    WORD_OR_DEFINITION_CONTAINS;
    fun getBitmask() : Long {
        return when (this) {
            WORD_EQUALS -> 1L
            WORD_STARTS_WITH -> 2L
            WORD_ENDS_WITH -> 4L
            WORD_CONTAINS -> 8L
            WORD_WILDCARDS -> 16L
            DEFINITION_CONTAINS -> 32L
            WORD_OR_DEFINITION_CONTAINS -> 64L
        }
    }
}

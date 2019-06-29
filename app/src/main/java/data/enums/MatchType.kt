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
            WORD_ENDS_WITH -> 3L
            WORD_CONTAINS -> 4L
            WORD_WILDCARDS -> 5L
            DEFINITION_CONTAINS -> 6L
            WORD_OR_DEFINITION_CONTAINS -> 7L
        }
    }
}

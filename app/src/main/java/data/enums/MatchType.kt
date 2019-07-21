package data.enums

enum class MatchType {
    WORD_EQUALS,
    WORD_STARTS_WITH,
    WORD_ENDS_WITH,
    WORD_CONTAINS,
    WORD_WILDCARDS,
    DEFINITION_CONTAINS,
    WORD_OR_DEFINITION_CONTAINS;

    val id : Long
        get() {
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

    val templateString : String
        get() {
            return when (this){
                WORD_EQUALS -> "%s"
                WORD_STARTS_WITH -> "%s%%"
                WORD_ENDS_WITH -> "%%%s"
                WORD_CONTAINS -> "%%%s%%"
                WORD_WILDCARDS -> "%s"
                DEFINITION_CONTAINS -> "%%%s%%"
                WORD_OR_DEFINITION_CONTAINS -> "%%%s%%"
            }
        }
}

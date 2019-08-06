package data.enums

enum class MatchType {
    WORD_EQUALS,
    WORD_STARTS_WITH,
    WORD_ENDS_WITH,
    WORD_CONTAINS,
    WORD_WILDCARDS,
    DEFINITION_CONTAINS,
    WORD_OR_DEFINITION_CONTAINS;

    val matchTypeID : Long
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
                WORD_EQUALS -> NO_APPENDED_WILDCARDS_TEMPLATE_STRING
                WORD_STARTS_WITH -> TRAIL_WILDCARD_TEMPLATE_STRING
                WORD_ENDS_WITH -> LEAD_WILDCARD_TEMPLATE_STRING
                WORD_CONTAINS -> LEAD_AND_TRAIL_WILDCARD_TEMPLATE_STRING
                WORD_WILDCARDS -> NO_APPENDED_WILDCARDS_TEMPLATE_STRING
                DEFINITION_CONTAINS -> LEAD_AND_TRAIL_WILDCARD_TEMPLATE_STRING
                WORD_OR_DEFINITION_CONTAINS -> LEAD_AND_TRAIL_WILDCARD_TEMPLATE_STRING
            }
        }

    companion object {

        private const val LEAD_AND_TRAIL_WILDCARD_TEMPLATE_STRING = "%%%s%%"
        private const val TRAIL_WILDCARD_TEMPLATE_STRING = "%s%%"
        private const val LEAD_WILDCARD_TEMPLATE_STRING = "%%%s"
        private const val NO_APPENDED_WILDCARDS_TEMPLATE_STRING = "%s"

        fun getMatchType(matchTypeID : Long): MatchType {
            return values().single{
                it.matchTypeID == matchTypeID
            }
        }
    }
}

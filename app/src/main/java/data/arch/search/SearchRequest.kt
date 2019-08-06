package data.arch.search

import data.enums.*

class SearchRequest(val searchTerm: String,
                    val wordLanguage: Language,
                    val definitionLanguage: Language,
                    val matchType : MatchType)
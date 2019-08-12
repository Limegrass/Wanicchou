package data.arch.search

import data.enums.*

class SearchRequest(val searchTerm: String,
                    val vocabularyLanguage: Language,
                    val definitionLanguage: Language,
                    val matchType : MatchType)
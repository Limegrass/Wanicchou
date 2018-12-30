package data.arch.search

import data.enums.MatchType
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.WordListEntry
import org.jsoup.nodes.Document

interface IDictionaryWebPage {
    fun getVocabulary(document: Document, wordLanguageCode : String) : Vocabulary
    fun getDefinition(document: Document, definitionLanguageCode: String) : Definition

    fun getRelatedWords(document: Document,
                        wordLanguageCode: String,
                        definitionLanguageCode: String) : List<WordListEntry>

    fun getRelatedWords(relatedWords : List<Vocabulary>,
                        definitionLanguageCode: String) : List<WordListEntry>

    fun loadUrl(url : String,
                wordLanguageCode: String,
                definitionLanguageCode: String,
                pageLoadedCallback: OnPageParsed)

    fun search(searchTerm: String,
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType,
               pageLoadedCallback: OnPageParsed)

    interface OnPageParsed {
        fun onPageParsed(document: Document,
                         wordLanguageCode: String,
                         definitionLanguageCode: String)
    }


}
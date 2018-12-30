package data.arch.search

import data.arch.vocab.IDefinitionFactory
import data.arch.vocab.IRelatedWordFactory
import data.arch.vocab.IVocabularyFactory
import data.enums.MatchType
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.WordListEntry

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

//TODO: Change lazy public vals to private and use the SearchProvider
abstract class JsoupDictionaryWebPage(val vocabularyFactory: IVocabularyFactory,
                                      val definitionFactory: IDefinitionFactory,
                                      val relatedWordFactory: IRelatedWordFactory)
    : IDictionaryWebPage {
    // ====================== ABSTRACT ======================
    abstract fun buildQueryURL(searchTerm: String,
                                        wordLanguageCode: String,
                                        definitionLanguageCode: String,
                                        matchType: MatchType): URL

    // ====================== PUBLIC =====================

    override fun getVocabulary(document: Document, wordLanguageCode: String) : Vocabulary {
        return vocabularyFactory.getVocabulary(document, wordLanguageCode)
    }

    override fun getDefinition(document: Document, definitionLanguageCode: String): Definition {
        return definitionFactory.getDefinition(document, definitionLanguageCode)
    }

    override fun getRelatedWords(document: Document,
                                 wordLanguageCode: String,
                                 definitionLanguageCode: String): List<WordListEntry> {
        return relatedWordFactory.getRelatedWords(document, wordLanguageCode, definitionLanguageCode)
    }

    override fun getRelatedWords(relatedWords: List<Vocabulary>, definitionLanguageCode: String): List<WordListEntry> {
        return relatedWordFactory.getRelatedWords(relatedWords, definitionLanguageCode)
    }

    override fun loadUrl(url: String,
                         wordLanguageCode: String,
                         definitionLanguageCode: String,
                         pageLoadedCallback: IDictionaryWebPage.OnPageParsed) {
        val userAgent = "Mozilla"
        val document = Jsoup.connect(url).userAgent(userAgent).data().get()
        pageLoadedCallback.onPageParsed(document, wordLanguageCode, definitionLanguageCode)
    }

    //TODO: Try to find another alternative to page parsing again
    override fun search(searchTerm: String,
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType,
               pageLoadedCallback: IDictionaryWebPage.OnPageParsed) {

        val url = buildQueryURL(searchTerm, wordLanguageCode, definitionLanguageCode, matchType)
                .toString()
        val userAgent = "Mozilla"
        val document = Jsoup.connect(url).userAgent(userAgent).data().get()
        pageLoadedCallback.onPageParsed(document, wordLanguageCode, definitionLanguageCode)
    }
}

package data.arch.search

import android.os.AsyncTask
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
abstract class JsoupDictionaryWebPage(private val vocabularyFactory: IVocabularyFactory,
                                      private val definitionFactory: IDefinitionFactory,
                                      private val relatedWordFactory: IRelatedWordFactory)
    : IDictionaryWebPage {
    // ====================== ABSTRACT ======================
    abstract fun buildQueryURL(searchTerm: String,
                                        wordLanguageCode: String,
                                        definitionLanguageCode: String,
                                        matchType: MatchType): URL

    abstract override fun getSupportedMatchTypes(): Set<MatchType>

    abstract override val dictionaryName: String

    abstract override fun getVocabulary(document: Document,
                                        wordLanguageCode: String) : Vocabulary

    abstract override fun getDefinition(document: Document,
                                        definitionLanguageCode: String): Definition

    abstract override fun getRelatedWords(document: Document,
                                          wordLanguageCode: String): List<Vocabulary>

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
                         onPageParsed: IDictionaryWebPage.OnPageParsed) {
        val userAgent = "Mozilla"
        val webPage = this
        ConnectAsyncTask(url,
                         userAgent,
                         wordLanguageCode,
                         definitionLanguageCode,
                         onPageParsed,
                         webPage).execute()
    }

    //TODO: Try to find another alternative to page parsing again
    override fun search(searchTerm: String,
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType,
               onPageParsed: IDictionaryWebPage.OnPageParsed) {

        val url = buildQueryURL(searchTerm,
                                wordLanguageCode,
                                definitionLanguageCode,
                                matchType)
                                .toString()
        val userAgent = "Mozilla"
        val webPage = this
        ConnectAsyncTask(url,
                         userAgent,
                         wordLanguageCode,
                         definitionLanguageCode,
                         onPageParsed,
                         webPage).execute()
    }

    //TODO: Remove AsyncTask for proper implementation with coroutines
    private class ConnectAsyncTask(val url : String,
                                   val userAgent: String,
                                   val wordLanguageCode: String,
                                   val definitionLanguageCode: String,
                                   val onPageParsed: IDictionaryWebPage.OnPageParsed,
                                   val webPage : IDictionaryWebPage)
        : AsyncTask<Void, Void, Document>() {
        override fun doInBackground(vararg params: Void?): Document {
            return Jsoup.connect(url).userAgent(userAgent).data().get()
        }

        override fun onPostExecute(result: Document?) {
            super.onPostExecute(result)
            if (result != null){
                onPageParsed.onPageParsed(result, wordLanguageCode, definitionLanguageCode, webPage)
            }
        }
    }
}

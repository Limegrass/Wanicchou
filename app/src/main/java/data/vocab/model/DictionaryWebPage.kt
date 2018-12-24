package data.vocab.model

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import data.vocab.shared.MatchType

import data.vocab.shared.WordListEntry
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL

//TODO: Change lazy public vals to private and use the SearchProvider
abstract class DictionaryWebPage(val IDictionaryEntryFactory: IDictionaryEntryFactory,
                                 val relatedWordFactory: IRelatedWordFactory) {
    companion object {
        private const val HTML_PARSER_NAME = "HtmlParser"
        private const val PAGE_PARSE_URL = "javascript:window.HtmlParser.parsePage" +
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
    }

    // ====================== ABSTRACT ======================
    abstract fun getSupportedMatchTypes(): Set<MatchType>

    abstract fun buildQueryURL(searchTerm: String,
                                         wordLanguageCode: String,
                                         definitionLanguageCode: String,
                                         matchType: MatchType): URL

    // ====================== PUBLIC =====================


    //TODO: Try to find another alternative to page parsing again
    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    @Throws(IOException::class)
    fun search(searchTerm: String,
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType,
               webView: WebView,
               pageParsed: OnPageParsed) {

        val searchUrl = buildQueryURL(searchTerm, wordLanguageCode, definitionLanguageCode, matchType)
        webView.addJavascriptInterface(HtmlParserInterface(pageParsed,
                                                           wordLanguageCode,
                                                           definitionLanguageCode),
                                       HTML_PARSER_NAME)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                //Calls parsePage, which calls the parseRelatedWordsPage
                parsePage(view)
            }
        }
        webView.loadUrl(searchUrl.toString())
    }

    //TODO: Decide if this should be abstract or not
    // And also deal with the searching if link is blank ()
    fun navigateRelatedWord(webView: WebView,
                            relatedWord: WordListEntry): Boolean {
        //TODO: When navigating to Related Word, the related words doesn't change.
        val link = relatedWord.link
        if (!link.trim().isBlank()) {
            webView.loadUrl(link)
            return true
        }
        return false
    }

//    fun getSearch(html: String, wordLanguageCode: String, definitionLanguageCode: String): Search {
//        return searchFactory.getSearch(html, wordLanguageCode, definitionLanguageCode)
//    }

    // ============= PRIVATE ==============
    //TODO: Potentially refactor this into just passing the string back to the listener, but I'd have to
    // guarantee that the definition language code and search provider does not change in the mean time.
    private inner class HtmlParserInterface (
            private val pageParsed: OnPageParsed,
            private val wordLanguageCode: String,
            private val definitionLanguageCode: String) {
        @JavascriptInterface
        fun parsePage(html: String) {
            val document = Jsoup.parse(html)
            val dictionaryEntry = IDictionaryEntryFactory
                    .getDictionaryEntry(document, wordLanguageCode, definitionLanguageCode)
            val relatedWords = relatedWordFactory
                    .getRelatedWords(document, wordLanguageCode, definitionLanguageCode)
            pageParsed.onPageParsed(dictionaryEntry, relatedWords)
        }
    }

    private fun parsePage(webView: WebView) {
        webView.loadUrl(PAGE_PARSE_URL)
    }


    interface OnPageParsed {
        fun onPageParsed(dictionaryEntry: DictionaryEntry,
                         relatedWords: List<WordListEntry>)
    }



    //TODO: Move this logic to try again on its caller
//            try {
//                val webURL = buildQueryURL(
//                        relatedWord.relatedWord,
//
//                        matchType as SanseidouMatchType)
//                this.loadUrl(webURL.toString())
//            } catch (cce: ClassCastException) {
//                cce.printStackTrace()
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//            }


//    //TODO: Maybe don't ned DicType, just grab from page
//    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
//    constructor(context: Context,
//                baseUrl: String,
//                pageSource: String,
//                dictionaryType: DictionaryType,
//                listener: OnPageParsed) : super(context) {
//        search = SanseidoSearch(pageSource, dictionaryType)
//        this.settings.javaScriptEnabled = true
//        this.addJavascriptInterface(HtmlParserInterface(listener), HTML_PARSER_NAME)
//
//        this.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                parsePage()
//            }
//        }
//
//        val mimeType = "text/html" //Defaults to text/html if null
//        val encoding = "UTF-8"
//        val historyUrl: String? = null
//        this.loadDataWithBaseURL(baseUrl, pageSource, mimeType, encoding, historyUrl)
//    }
}

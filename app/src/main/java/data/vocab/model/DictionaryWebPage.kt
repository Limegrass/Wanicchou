package data.vocab.model

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import data.core.OnJavaScriptCompleted
import data.vocab.shared.MatchType

import data.vocab.shared.WordListEntry
import java.io.IOException
import java.net.URL

abstract class DictionaryWebPage(private val webView: WebView,
                                 val listener: OnJavaScriptCompleted,
                                 private val searchFactory: SearchFactory) {
    companion object {
        private const val HTML_PARSER_NAME = "HtmlParser"
        private const val PAGE_PARSE_URL = "javascript:window.HtmlParser.parsePage" +
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
    }

    // ====================== ABSTRACT ======================
    abstract fun getSupportedMatchType(): Set<MatchType>

    protected abstract fun buildQueryURL(searchTerm: String,
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
                    matchType: MatchType) {

        val searchUrl = buildQueryURL(searchTerm, wordLanguageCode, definitionLanguageCode, matchType)
        webView.addJavascriptInterface(HtmlParserInterface(listener, searchFactory), HTML_PARSER_NAME)
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
        if (link.trim().isBlank()) {
            webView.loadUrl(link)
            return true
        }
        return false
    }

//    fun getSearch(html: String, wordLanguageCode: String, definitionLanguageCode: String): Search {
//        return searchFactory.getSearch(html, wordLanguageCode, definitionLanguageCode)
//    }

    // ============= PRIVATE ==============
    //TODO: maybe i need to change OnJSComplete to work properly
    private inner class HtmlParserInterface internal constructor(internal val listener: OnJavaScriptCompleted, val searchFactory: SearchFactory) {
        @JavascriptInterface
        fun parsePage(html: String) {
            listener.onJavaScriptCompleted(html)
        }
    }
    private fun parsePage(webView: WebView) {
        webView.loadUrl(PAGE_PARSE_URL)
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


    // Maybe I don't need this and just let people implement search however they want
//    interface MatchTypeSupport {
//        val SUPPORTED_MATCH_TYPES: HashMap<MatchType, Int>

//    }
//    //TODO: Maybe don't ned DicType, just grab from page
//    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
//    constructor(context: Context,
//                baseUrl: String,
//                pageSource: String,
//                dictionaryType: DictionaryType,
//                listener: OnJavaScriptCompleted) : super(context) {
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

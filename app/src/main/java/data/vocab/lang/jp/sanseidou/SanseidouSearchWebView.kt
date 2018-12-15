package data.vocab.lang.jp.sanseidou

import android.webkit.WebView

import org.jsoup.nodes.Document

import data.vocab.shared.WordListEntry
import data.vocab.model.DictionaryWebPage
import data.vocab.shared.MatchType
import data.vocab.model.SearchResult
import data.vocab.model.DictionaryEntry
import kotlinx.android.parcel.Parcelize

@Parcelize
class SanseidouSearchWebView(override var dictionaryEntry: DictionaryEntry, override var search: SearchResult) : DictionaryWebPage() {
    override fun search(webView: WebView, searchTerm: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateRelatedWord(webView: WebView, relatedWord: WordListEntry, matchType: MatchType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val SUPPORTED_MATCH_TYPES = arrayOf(MatchType.WORD_CONTAINS,
                                            MatchType.WORD_STARTS_WITH,
                                            MatchType.WORD_ENDS_WITH,
                                            MatchType.WORD_EQUALS,
                                            MatchType.DEFINITION_CONTAINS)
    }

    override val supportedMatchType: Array<MatchType>
        get() = SUPPORTED_MATCH_TYPES
//    FORWARDS -> "0"
//    EXACT -> "1"
//    BACKWARDS -> "2"
//    FULL_TEXT -> "3"
//    PARTIAL -> "5"

//    override var htmlDocument: Document? = null
//        private set
//    override var currentDictionaryType: DictionaryType? = null
//        private set
//    override var search: SearchResult? = null
//        private set
//
//
//    val dictionaryEntry: DictionaryEntry
//        get() = search!!.dictionaryEntry
//
//    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
//    @Throws(IOException::class)
//    constructor(context: Context,
//                wordToSearch: String,
//                dictionaryType: DictionaryType,
//                matchType: SanseidouMatchType,
//                listener: OnJavaScriptCompleted) : super(context) {
//        currentDictionaryType = dictionaryType
//
//        val searchUrl = SanseidouSearchResult.buildQueryURL(wordToSearch, dictionaryType, matchType)
//        this.addJavascriptInterface(HtmlParserInterface(listener), HTML_PARSER_NAME)
//        this.settings.javaScriptEnabled = true
//        this.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                //Calls parsePage, which calls the parseRelatedWordsPage
//                parsePage()
//            }
//        }
//        this.loadUrl(searchUrl.toString())
//    }
//
//    //TODO: Maybe don't ned DicType, just grab from page
//    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
//    constructor(context: Context,
//                baseUrl: String,
//                pageSource: String,
//                dictionaryType: DictionaryType,
//                listener: OnJavaScriptCompleted) : super(context) {
//        search = SanseidouSearchResult(pageSource, dictionaryType)
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
//
//    fun parsePage() {
//        this.loadUrl("javascript:window.HtmlParser.parsePage" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
//    }
//
//
//    private inner class HtmlParserInterface internal constructor(internal var listener: OnJavaScriptCompleted) {
//        @JavascriptInterface
//        fun parsePage(html: String) {
//            htmlDocument = Jsoup.parse(html)
//            search = SanseidouSearchResult(htmlDocument, currentDictionaryType)
//            listener.onJavaScriptCompleted()
//        }
//    }
//
//    override fun navigateRelatedWord(relatedWord: WordListEntry, matchType: MatchType) {
//        //TODO: When navigating to Related Word, the related words doesn't change.
//        currentDictionaryType = relatedWord.dictionaryType
//        val link = relatedWord.link
//        if (!TextUtils.isEmpty(link!!.trim { it <= ' ' })) {
//            this.loadUrl(link)
//        } else {
//            try {
//                val webURL = SanseidouSearchResult.buildQueryURL(
//                        relatedWord.relatedWord,
//                        relatedWord.dictionaryType!!,
//                        matchType as SanseidouMatchType)
//                this.loadUrl(webURL.toString())
//            } catch (cce: ClassCastException) {
//                cce.printStackTrace()
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    override fun setCurrentDictionaryType(dictionaryType: JapaneseDictionaryType) {
//        this.currentDictionaryType = dictionaryType
//    }
//
//    override fun getUrl(): String {
//        return super.getUrl()
//    }
//
//    companion object {
//
//        private val RELATED_WORDS_PAGER_ID = "_ctl0_ContentPlaceHolder1_ibtGoNext"
//        private val HTML_PARSER_NAME = "HtmlParser"
//    }
//
//    fun assignTypeByInput(input: String): DictionaryType? {
//        return if (isEnglishInput(input)) {
//            EJ
//        } else null
//    }
}

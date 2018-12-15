package data.vocab.model

import android.os.Parcelable
import android.webkit.WebView
import data.vocab.shared.MatchType
import org.jsoup.nodes.Document

import data.vocab.shared.WordListEntry

abstract class DictionaryWebPage : Parcelable {
    abstract var dictionaryEntry: DictionaryEntry
    abstract var search: SearchResult
//    abstract var htmlDocument: Document
    abstract val supportedMatchType: Array<MatchType>

    abstract fun search(webView: WebView, searchTerm: String)
    abstract fun navigateRelatedWord(webView: WebView, relatedWord: WordListEntry, matchType: MatchType)
}

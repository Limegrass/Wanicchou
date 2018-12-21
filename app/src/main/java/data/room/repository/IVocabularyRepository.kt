package data.room.repository

import android.webkit.WebView
import data.core.OnDatabaseQuery
import data.core.OnJavaScriptCompleted
import data.vocab.shared.MatchType


interface IVocabularyRepository : OnJavaScriptCompleted {
    fun getLatest(onDatabaseQuery: OnDatabaseQuery)
    fun search(webView: WebView,
               databaseCallback: OnDatabaseQuery,
               dictionary: String,
               searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS)
}
package data.arch.search

import androidx.annotation.WorkerThread
import data.arch.models.IDictionaryEntry
import data.enums.Language
import data.enums.MatchType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

abstract class JsoupDictionarySource
    : IDictionarySource {
    // ====================== ABSTRACT ======================
    protected abstract fun buildQueryURL(searchTerm: String,
                                         wordLanguage: Language,
                                         definitionLanguage: Language,
                                         matchType: MatchType): URL
    protected abstract fun getDictionaryEntry(document : Document,
                                              searchRequest: SearchRequest) : List<IDictionaryEntry>
    // ====================== PUBLIC =====================

    @WorkerThread
    override suspend fun search(request: SearchRequest): List<IDictionaryEntry> {
        val url = buildQueryURL(request.searchTerm,
                request.wordLanguage,
                request.definitionLanguage,
                request.matchType)
                .toString()
        val document =  Jsoup.connect(url).data().get()
        return getDictionaryEntry(document, request)
    }
    //TODO: Bring Jsoup out of this class so I can unit test easier.
    // Making a class that brings out the strings needed breaks encapsulation
    // but not doing it is difficult to test. Regardless
}

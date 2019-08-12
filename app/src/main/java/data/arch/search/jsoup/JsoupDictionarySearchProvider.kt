package data.arch.search.jsoup

import androidx.annotation.WorkerThread
import data.arch.models.IDictionaryEntry
import data.arch.search.IDictionarySearchProvider
import data.arch.search.IDictionarySource
import data.arch.search.SearchRequest
import data.enums.Language
import data.enums.MatchType
import org.jsoup.Jsoup
import java.net.URL

class JsoupDictionarySearchProvider(private val dictionarySource : IDictionarySource,
                                    private val dictionaryEntryFactory: IJsoupDictionaryEntryFactory)
    : IDictionarySearchProvider {

    override fun buildSearchQueryURL(searchRequest: SearchRequest): URL {
        return dictionarySource.buildSearchQueryURL(searchRequest)
    }
    override val supportedMatchTypes: Set<MatchType>
        get() = dictionarySource.supportedMatchTypes
    override val supportedTranslations: Map<Language, Set<Language>>
        get() = dictionarySource.supportedTranslations
    // ====================== PUBLIC =====================

    @WorkerThread
    override suspend fun search(request: SearchRequest): List<IDictionaryEntry> {
        val url = dictionarySource.buildSearchQueryURL(request).toString()
        val document =  Jsoup.connect(url).data().get()
        return dictionaryEntryFactory.getDictionaryEntries(document,
                                                           request.vocabularyLanguage,
                                                           request.definitionLanguage)
    }
}

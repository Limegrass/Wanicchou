package data.arch.search

import androidx.annotation.WorkerThread
import data.enums.MatchType
import data.room.entity.Definition
import data.room.entity.Vocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

//TODO: Change lazy public vals to private and use the SearchProvider
abstract class JsoupDictionaryWebPage
    : IDictionaryWebPage {
    // ====================== ABSTRACT ======================
    abstract fun buildQueryURL(searchTerm: String,
                                        wordLanguageCode: String,
                                        definitionLanguageCode: String,
                                        matchType: MatchType): URL

    abstract override fun getSupportedMatchTypes(): Set<MatchType>

    abstract override val dictionaryID: Long

    // ====================== PUBLIC =====================


    //TODO: Try to find another alternative to page parsing again
    @WorkerThread
    override suspend fun search(searchTerm: String,
                                wordLanguageCode: String,
                                definitionLanguageCode: String,
                                matchType: MatchType): Document {
        val url = buildQueryURL(searchTerm,
                                wordLanguageCode,
                                definitionLanguageCode,
                                matchType)
                                .toString()
        val userAgent = "Mozilla"
        return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .data()
                    .get()
    }

}

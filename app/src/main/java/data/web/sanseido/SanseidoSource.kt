package data.web.sanseido

import android.net.Uri
import data.search.IDictionarySource
import data.search.SearchRequest
import data.enums.Language
import data.enums.MatchType
import java.net.MalformedURLException
import java.net.URL

class SanseidoSource : IDictionarySource {
    override val supportedMatchTypes: Set<MatchType>
        get() = SUPPORTED_MATCH_TYPES.keys

    override val supportedTranslations: Map<Language, Set<Language>>
        get() = SUPPORTED_TRANSLATIONS

    @Throws(MalformedURLException::class)
    override fun buildSearchQueryURL(searchRequest : SearchRequest): URL {
        val uriBuilder = Uri.parse(SANSEIDO_BASE_URL).buildUpon()
        uriBuilder.setSearchType(searchRequest.matchType)
        uriBuilder.setSearchTerm(searchRequest.searchTerm)
        uriBuilder.setDictionaryOrder(DORDER_DEFAULT)
        uriBuilder.setQueryLanguage(searchRequest.vocabularyLanguage, searchRequest.definitionLanguage)
        return URL(uriBuilder.build().toString())
    }

    // ====================== PRIVATE ======================
    companion object {

        private val TAG = SanseidoSource::class.java.simpleName

        private const val SANSEIDO_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx"
        private const val PARAM_WORD_QUERY = "TWords"
        // Order of dictionaries under select dictionaries
        private const val PARAM_DICTIONARY_ORDER = "DORDER"
        private const val DORDER_JJ = "15"
        private const val DORDER_JE = "17"
        private const val DORDER_EJ = "16"
        private const val DORDER_DEFAULT = DORDER_JJ + DORDER_JE + DORDER_EJ
        private const val PARAM_SEARCH_TYPE = "st"
        // Enabling and disabling of languages
        // Display will go by DORDER
        private const val PARAM_DIC_PREFIX = "Daily"
        private const val SET_LANG = "checkbox"

        private val SUPPORTED_MATCH_TYPES = hashMapOf(
                MatchType.WORD_STARTS_WITH to "0",
                MatchType.WORD_EQUALS to "1",
                MatchType.WORD_ENDS_WITH to "2",
                MatchType.WORD_OR_DEFINITION_CONTAINS to "3",
                MatchType.WORD_CONTAINS to "5")

        private fun Uri.Builder.setSearchType(matchType: MatchType) {
            val sanseidoMatchTypeID = SUPPORTED_MATCH_TYPES[matchType]
                    ?: throw IllegalArgumentException("Unsupported MatchType: $matchType")
            appendQueryParameter(PARAM_SEARCH_TYPE, sanseidoMatchTypeID )
        }

        private fun Uri.Builder.setDictionaryOrder(dictionaryOrder : String) {
            appendQueryParameter(PARAM_DICTIONARY_ORDER, dictionaryOrder)
        }

        private fun Uri.Builder.setQueryLanguage(wordLanguage: Language, definitionLanguage: Language){
            appendQueryParameter(
                    PARAM_DIC_PREFIX
                            + getLanguagePrefix(wordLanguage)
                            + getLanguagePrefix(definitionLanguage),
                    SET_LANG)
        }

        private fun Uri.Builder.setSearchTerm(searchTerm: String){
            appendQueryParameter(PARAM_WORD_QUERY, searchTerm)
        }

        private fun getLanguagePrefix(language : Language) : Char {
            return when (language){
                Language.ENGLISH-> 'E'
                Language.JAPANESE -> 'J'
            }
        }

        private val SUPPORTED_TRANSLATIONS : Map<Language, Set<Language>> = hashMapOf(
                Language.JAPANESE to hashSetOf(Language.JAPANESE, Language.ENGLISH),
                Language.ENGLISH to hashSetOf(Language.JAPANESE)
        )
    }
}
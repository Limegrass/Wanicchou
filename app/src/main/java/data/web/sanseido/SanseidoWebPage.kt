package data.web.sanseido

import android.net.Uri
import data.arch.lang.EnglishVocabulary
import data.arch.lang.JapaneseVocabulary
import data.arch.search.JsoupDictionaryWebPage
import data.enums.MatchType
import java.lang.UnsupportedOperationException
import java.net.MalformedURLException
import java.net.URL

/**
 * Purpose: Implementation for a Jsoup web page
 */
class SanseidoWebPage
    : JsoupDictionaryWebPage() {

    override val dictionaryID: Long
        get() = DICTIONARY_ID

    // ====================== PRIVATE ======================


    companion object {
        private val TAG = SanseidoWebPage::class.java.simpleName

        private const val SANSEIDO_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx"
        private const val PARAM_WORD_QUERY = "TWords"
        // Order of dictionaries under select dictionaries
        // First is the one that displays
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

        const val DICTIONARY_NAME = "三省堂"
        const val DICTIONARY_ID = 1L

        private fun Uri.Builder.setSearchType(matchType: MatchType) {
            val sanseidoMatchTypeID = SUPPORTED_MATCH_TYPES[matchType]
                    ?: throw IllegalArgumentException("Unsupported MatchType: $matchType")
            appendQueryParameter(PARAM_SEARCH_TYPE, sanseidoMatchTypeID )
        }

        private fun Uri.Builder.setDictionaryOrder(dictionaryOrder : String) {
            appendQueryParameter(PARAM_DICTIONARY_ORDER, dictionaryOrder)
        }

        private fun Uri.Builder.setQueryLanguage(wordLanguageID: Long, definitionLanguageID: Long){
            appendQueryParameter(
                    PARAM_DIC_PREFIX
                            + getLanguagePrefix(wordLanguageID)
                            + getLanguagePrefix(definitionLanguageID),
                    SET_LANG)
        }

        private fun Uri.Builder.setSearchTerm(searchTerm: String){
            appendQueryParameter(PARAM_WORD_QUERY, searchTerm)
        }

        private fun getLanguagePrefix(languageID : Long) : Char {
            return when (languageID){
                EnglishVocabulary.LANGUAGE_ID -> 'E'
                JapaneseVocabulary.LANGUAGE_ID -> 'J'
                else -> throw IllegalArgumentException("Unsupported Language ID: $languageID.")
            }
        }
    }

    override fun getSupportedMatchTypes(): Set<MatchType> {
        return SUPPORTED_MATCH_TYPES.keys
    }

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     * @return the Sanseido url created
     * @throws MalformedURLException if a search url cannot be built
     */
    @Throws(MalformedURLException::class)
    override fun buildQueryURL(searchTerm: String,
                               wordLanguageID: Long,
                               definitionLanguageID: Long,
                               matchType: MatchType): URL {

        val uriBuilder = Uri.parse(SANSEIDO_BASE_URL).buildUpon()
        uriBuilder.setSearchType(matchType)
        uriBuilder.setSearchTerm(searchTerm)
        uriBuilder.setDictionaryOrder(DORDER_DEFAULT)
        uriBuilder.setQueryLanguage(wordLanguageID, definitionLanguageID)
        return URL(uriBuilder.build().toString())
    }
}

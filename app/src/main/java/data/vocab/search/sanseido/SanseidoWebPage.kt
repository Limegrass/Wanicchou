package data.vocab.search.sanseido

import android.net.Uri
import android.webkit.WebView
import data.core.OnJavaScriptCompleted
import data.vocab.model.DictionaryWebPage
import data.vocab.shared.MatchType
import java.net.MalformedURLException
import java.net.URL

//TODO: Singleton Access
class SanseidoWebPage
    : DictionaryWebPage(SanseidoDictionaryEntryFactory, SanseidoRelatedWordFactory) {

    companion object {
        val SUPPORTED_MATCH_TYPES = hashMapOf(
                MatchType.WORD_STARTS_WITH to 0,
                MatchType.WORD_EQUALS to 1,
                MatchType.WORD_ENDS_WITH to 2,
                MatchType.DEFINITION_CONTAINS to 3,
                MatchType.WORD_CONTAINS to 5)

        //TODO: Use pager
        private const val RELATED_WORDS_PAGER_ID = "_ctl0_ContentPlaceHolder1_ibtGoNext"
        private val TAG = SanseidoWebPage::class.java.simpleName

        private const val SANSEIDO_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx"
        private const val PARAM_WORD_QUERY = "TWords"

        // Order of dictionaries under select dictionaries
        // First is the one that displays
        private const val PARAM_DORDER = "DORDER"
        private const val DORDER_JJ = "15"
        private const val DORDER_EJ = "16"
        private const val DORDER_JE = "17"
        private const val DORDER_DEFAULT = DORDER_JJ + DORDER_JE + DORDER_EJ

        // ST is the behavior of the search
        private const val PARAM_ST = "st"

        // Enabling and disabling of languages
        // Display will go by DORDER
        private const val PARAM_DIC_PREFIX = "Daily"
        private const val SET_LANG = "checkbox"

    }


    override fun getSupportedMatchType(): Set<MatchType> {
        return SUPPORTED_MATCH_TYPES.keys
    }

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     *
     * @return the Sanseido url created
     * @throws MalformedURLException if a search url cannot be built
     */
    @Throws(MalformedURLException::class)
    override fun buildQueryURL(searchTerm: String,
                               wordLanguageCode: String,
                               definitionLanguageCode: String,
                               matchType: MatchType): URL {

        val uriBuilder = Uri.parse(SANSEIDO_BASE_URL).buildUpon()

        uriBuilder.appendQueryParameter(PARAM_ST,
                SUPPORTED_MATCH_TYPES[matchType].toString())
        uriBuilder.appendQueryParameter(PARAM_DORDER, DORDER_DEFAULT)
        uriBuilder.appendQueryParameter(PARAM_WORD_QUERY, searchTerm)
        uriBuilder.appendQueryParameter(
                PARAM_DIC_PREFIX
                        + wordLanguageCode[0].toUpperCase()
                        + definitionLanguageCode[0].toUpperCase(),
                SET_LANG)


        try {
            return URL(uriBuilder.build().toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw MalformedURLException(
                    "Word: " + searchTerm
                            + " MatchType " + matchType.toString()
                            + " Word Language " + wordLanguageCode
                            + " Definition Language " + definitionLanguageCode
                            + "%n"
                            + e)
        }

    }
}

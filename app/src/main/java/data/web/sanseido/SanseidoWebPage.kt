package data.web.sanseido

import android.net.Uri
import data.arch.search.JsoupDictionaryWebPage
import data.arch.vocab.IDefinitionFactory
import data.arch.vocab.IVocabularyFactory
import data.enums.MatchType
import data.room.entity.Definition
import data.room.entity.Vocabulary
import org.jsoup.nodes.Document
import java.net.MalformedURLException
import java.net.URL

//TODO: Singleton Access
//TODO: Maybe it could be good to move all the objects inside of this since it's only used here.
//It'll also help the problem of the Related Words having to use the private const vars
class SanseidoWebPage
    : JsoupDictionaryWebPage() {
    private val vocabularyFactory: IVocabularyFactory = SanseidoVocabularyFactory
    private val definitionFactory: IDefinitionFactory = SanseidoDefinitionFactory
    override fun getRelatedWords(document: Document,
                                 wordLanguageCode: String): List<Vocabulary> {
        val relatedWordEntries = ArrayList<Vocabulary>()
        val table = document.select("table")[RELATED_WORDS_TABLE_INDEX]
        val rows = table.select("tr")

        for (row in rows) {
            val columns = row.select("td")
            val tableEntry = columns[RELATED_WORDS_VOCAB_INDEX].text()
            val relatedVocabulary = vocabularyFactory.getVocabulary(tableEntry, wordLanguageCode)
            relatedWordEntries.add(relatedVocabulary)
        }

        return relatedWordEntries
    }

    override val dictionaryName: String
        get() = DICTIONARY_NAME

    // ====================== PRIVATE ======================
    /**
     * Retrieve the searched word from the html source
     * @param html the html source
     * @return the word searched for
     */
    private fun findWordSource(html: Document): String {
        return html.getElementById(SANSEIDO_WORD_ID).text()
    }

    override fun getDefinition(document: Document,
                               definitionLanguageCode: String): Definition {
        val definitionSource = getDefinitionSource(document)
        return definitionFactory.getDefinition(definitionLanguageCode, definitionSource)
    }

    private fun getDefinitionSource(html : Document) : String {
        val definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID)
        return if (definitionParentElement.children().size > 0) {
            definitionParentElement.child(0).text()
        }
        else {
            ""
        }
    }


    override fun getVocabulary(document: Document, wordLanguageCode: String): Vocabulary {
        val wordSource = findWordSource(document)
        return vocabularyFactory.getVocabulary(wordSource, wordLanguageCode)
    }

    companion object {
        //TODO: Use pager
        private const val RELATED_WORDS_VOCAB_INDEX = 1
        private const val RELATED_WORDS_TABLE_INDEX = 0

        private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
        private const val SANSEIDO_WORD_ID = "word"
        private val TAG = SanseidoWebPage::class.java.simpleName

        private const val RELATED_WORDS_PAGER_ID = "_ctl0_ContentPlaceHolder1_ibtGoNext"
        private const val SANSEIDO_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx"
        private const val PARAM_WORD_QUERY = "TWords"
        // Order of dictionaries under select dictionaries
        // First is the one that displays
        private const val PARAM_DORDER = "DORDER"
        private const val DORDER_JJ = "15"
        private const val DORDER_JE = "17"
        private const val DORDER_EJ = "16"
        private const val DORDER_DEFAULT = DORDER_JJ + DORDER_JE + DORDER_EJ
        // ST is the behavior of the search
        private const val PARAM_ST = "st"
        // Enabling and disabling of languages
        // Display will go by DORDER
        private const val PARAM_DIC_PREFIX = "Daily"
        private const val SET_LANG = "checkbox"

        private val SUPPORTED_MATCH_TYPES = hashMapOf(
                MatchType.WORD_STARTS_WITH to 0,
                MatchType.WORD_EQUALS to 1,
                MatchType.WORD_ENDS_WITH to 2,
                MatchType.DEFINITION_CONTAINS to 3,
                MatchType.WORD_CONTAINS to 5)
        private const val DICTIONARY_NAME = "Sanseido"
    }


    override fun getSupportedMatchTypes(): Set<MatchType> {
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

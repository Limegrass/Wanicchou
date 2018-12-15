package data.vocab.lang.jp.sanseidou

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.waifusims.wanicchou.databinding.WordListItemBinding

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

import data.vocab.lang.jp.JapaneseVocabulary
import data.vocab.model.DictionaryEntry
import data.vocab.shared.WordListEntry
import data.vocab.model.SearchResult
import kotlinx.android.parcel.Parcelize

//TODO: Equals, Hashcode methods

/**
 * Created by Limegrass on 3/19/2018.
 */
@Parcelize
class SanseidouSearchResult(override val relatedWords: Array<WordListEntry> = arrayOf(),
                            override val dictionaryEntry: DictionaryEntry) : SearchResult {

//    private val SANSEIDO_WORD_ID = "word"
//    private val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
    //TODO: Maybe something more graceful or renaming
    // TODO: Find these characters individually?
//    private val MULTIPLE_DEFINITION_REGEX = "▼"
//    private val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"

    //TODO: Refactor to it uses the enum type
    // ======================== GETTERS AND SETTERS ==================================


//    // TODO: Fix 宝物 not being able to redirect to itself properly. Goes to 宝
//    // I can do this by stripping all non kana/kanji chars from the word source and putting it in the search.
//
//    /**
//     * Constructor to create an object containing the information retrieved from Sanseidou from
//     * a search given a word to search.
//     * @param wordToSearch the desired word to search.
//     * @throws IOException if the search cannot be completed
//     */
//    @Throws(IOException::class)
//    constructor(wordToSearch: String,
//                dictionaryType: DictionaryType?,
//                matchType: SanseidouMatchType?) {
//        //TODO: Fix relatedWords searching, as it is not working properly for forwards search
//        if (TextUtils.isEmpty(wordToSearch)) {
//            throw IllegalArgumentException("SearchResult term cannot be empty!")
//        }
//        if (dictionaryType == null) {
//            throw IllegalArgumentException("Dictionary Type cannot be null!")
//        }
//        if (matchType == null) {
//            throw IllegalArgumentException("Match Type cannot be null!")
//        }
//
//        val url = buildQueryURL(wordToSearch, dictionaryType, matchType)
//        val html = fetchSanseidoSource(url)
//        relatedWords = findRelatedWords(html)
//        dictionaryEntry = JapaneseVocabulary(
//                findWordSource(html),
//                findDefinitionSource(html),
//                dictionaryType)
//    }
//
//    constructor(html: String, dictionaryType: DictionaryType) : this(Jsoup.parse(html), dictionaryType) {}
//
//    constructor(html: Document, dictionaryType: DictionaryType) {
//        relatedWords = findRelatedWords(html)
//        dictionaryEntry = JapaneseVocabulary(findWordSource(html),
//                findDefinitionSource(html),
//                dictionaryType)
//    }
//
//    /**
//     * Constructs a search object from a given vocab and it's related words
//     * @param japaneseVocabulary The dictionaryEntry with it's word-definition pair
//     * @param relatedWords Words related to the dictionaryEntry specific to it's search type.
//     */
//    constructor(japaneseVocabulary: JapaneseVocabulary, relatedWords: MutableList<WordListEntry>) {
//        this.dictionaryEntry = japaneseVocabulary
//        this.relatedWords = relatedWords
//    }
//
//    override fun getRelatedWords(): List<WordListEntry>? {
//        return relatedWords
//    }
//
//    protected fun setRelatedWords(relatedWords: MutableList<WordListEntry>) {
//        this.relatedWords = relatedWords
//    }
//
//
//    /**
//     * Helper method to create an HTTP request to Sanseidou for a given URL.
//     * @param searchURL the URL of the word search to be performed
//     * @return A Jsoup html document tree of the html source from the search.
//     * @throws IOException if the search cannot be completed.
//     */
//    @Throws(IOException::class)
//    private fun fetchSanseidoSource(searchURL: URL): Document {
//        return Jsoup.connect(searchURL.toString()).get()
//    }
//
//    /**
//     * Helper method to isolate the related words of a search from the raw html source.
//     * @param html the raw html jsoup document tree.
//     * @return a map of related words in a set with the key being the dictionary they exist in.
//     */
//    private fun findRelatedWords(html: Document): MutableList<WordListEntry> {
//        val relatedWordEntries = ArrayList<WordListEntry>()
//
//        // The related words table is the first table in the HTML
//        val table = html.select("table")[RELATED_WORDS_TABLE_INDEX]
//        val rows = table.select("tr")
//
//        // Preferred selecting by exact word first, but attempt others if it is a message input
//        // like many that exist in the Sanseidou searches that are not exact or if kana is
//        // inputted
//
//        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
//        Log.d(TAG, rows.size.toString())
//        for (row in rows) {
//
//            val columns = row.select("td")
//
//            var dictionaryTypeString: String? = columns[RELATED_WORDS_TYPE_CLASS_INDEX].text()
//
//            //Essentially using JapaneseDictionaryType enum by substringing out the brackets 【 】
//            if (dictionaryTypeString != null) {
//                dictionaryTypeString = dictionaryTypeString.substring(1, dictionaryTypeString.length - 1)
//
//                val dictionaryType = JapaneseDictionaryType.fromJapaneseDictionaryKanji(dictionaryTypeString)
//
//                val tableEntry = columns[RELATED_WORDS_VOCAB_INDEX].text()
//                val isolatedWord = JapaneseVocabulary.isolateWord(tableEntry, dictionaryType)
//
//                val link = columns.select("a").first().attr("href")
//
//                relatedWordEntries.add(WordListEntry(isolatedWord, dictionaryType, link))
//            }
//        }
//        return relatedWordEntries
//    }
//
//    //TODO: Add all related words by navigating through the web view and appending.
//    fun addRelatedWords(html: Document) {
//        if (relatedWords == null) {
//            relatedWords = ArrayList()
//        }
//        relatedWords!!.addAll(findRelatedWords(html))
//    }
//
//
//    /**
//     * Retrieve the searched word from the html source
//     * @param html the html source
//     * @return the word searched for
//     */
//    private fun findWordSource(html: Document): String {
//        val word = html.getElementById(SANSEIDO_WORD_ID)
//
//        return word.text()
//    }
//
//    /**
//     * A helper method to isolate the source text of the definition of the word searched.
//     * @param html the jsoup html document tree.
//     * @return the raw definition source
//     */
//    private fun findDefinitionSource(html: Document): String {
//        val definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID)
//        // The definition is in a further div, single child
//        var definition = ""
//
//        if (definitionParentElement.children().size > 0) {
//            definition = definitionParentElement.child(0).text()
//        }
//
//        //TODO: FIX REGEX
//        definition = definition.replace(MULTIPLE_DEFINITION_REGEX.toRegex(), MULTIPLE_DEFINITION_SEPARATOR)
//        definition = definition.replaceFirst(MULTIPLE_DEFINITION_REGEX.toRegex(), MULTIPLE_DEFINITION_SEPARATOR)
//
//        return definition
//    }
//
//
//    // ==================================== PARCELABLE ========================================
//    /**
//     * Describes contents for Parcelable.
//     * @return The hashcode of the object.
//     */
//    override fun describeContents(): Int {
//        return hashCode()
//    }
//
//
//    /**
//     * Parcelization of the search object
//     * @param parcel The parcel to write to.
//     * @param i Flags for parcelization.
//     */
//    override fun writeToParcel(parcel: Parcel, i: Int) {
//        parcel.writeValue(dictionaryEntry)
//        parcel.writeValue(relatedWords)
//    }
//
//    /**
//     * Constructor from a parcel.
//     * @param parcel The parcel to read from.
//     */
//    private constructor(parcel: Parcel) {
//        val classLoader = javaClass.getClassLoader()
//        dictionaryEntry = parcel.readValue(classLoader) as JapaneseVocabulary
//        relatedWords = parcel.readValue(classLoader) as List<WordListEntry>
//    }
//
//    companion object {
//        private val TAG = SanseidouSearchResult::class.java!!.getSimpleName()
//
//        private val SANSEIDOU_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx"
//        private val PARAM_WORD_QUERY = "TWords"
//
//        // Order of dictionaries under select dictionaries
//        // First is the one that displays
//        private val PARAM_DORDER = "DORDER"
//        private val DORDER_JJ = "15"
//        private val DORDER_EJ = "16"
//        private val DORDER_JE = "17"
//        private val DORDER_DEFAULT = DORDER_JJ + DORDER_JE + DORDER_EJ
//
//        // ST is the behavior of the search
//        private val PARAM_ST = "st"
//
//        // Enabling and disabling of languages
//        // Display will go by DORDER
//        private val PARAM_DIC_PREFIX = "Daily"
//        private val SET_LANG = "checkbox"
//
//        private val RELATED_WORDS_TYPE_CLASS_INDEX = 0
//        private val RELATED_WORDS_VOCAB_INDEX = 1
//        private val RELATED_WORDS_TABLE_INDEX = 0
//
//
//        // ================================ HELPERS ===================================
//
//        /**
//         * Builds the URL for the desired word(s) to search for on Sanseidou.
//         *
//         * @param word the Japanese word to search for
//         * @param dictionaryType which dictionary to search from Sanseidou
//         * @return the Sanseidou url created
//         * @throws MalformedURLException if a search url cannot be built
//         */
//        @Throws(MalformedURLException::class)
//        fun buildQueryURL(word: String,
//                          dictionaryType: DictionaryType,
//                          matchType: SanseidouMatchType): URL {
//
//            val uriBuilder = Uri.parse(SANSEIDOU_BASE_URL).buildUpon()
//
//            uriBuilder.appendQueryParameter(PARAM_ST, matchType.toKey())
//            uriBuilder.appendQueryParameter(PARAM_DORDER, DORDER_DEFAULT)
//            uriBuilder.appendQueryParameter(PARAM_WORD_QUERY, word)
//            uriBuilder.appendQueryParameter(PARAM_DIC_PREFIX + dictionaryType.toString(), SET_LANG)
//
//
//            try {
//                return URL(uriBuilder.build().toString())
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//                throw MalformedURLException(
//                        "Word: " + word
//                                + " MatchType " + matchType.toString()
//                                + " DictionaryType " + dictionaryType.toString()
//                                + "%n"
//                                + e)
//            }
//
//        }
//
//        /**
//         * Creator for parcelization.
//         */
//        val CREATOR: Parcelable.Creator<SanseidouSearchResult> = object : Parcelable.Creator<SanseidouSearchResult> {
//            override fun createFromParcel(parcel: Parcel): SanseidouSearchResult {
//                return SanseidouSearchResult(parcel)
//            }
//
//            override fun newArray(size: Int): Array<SanseidouSearchResult> {
//                return arrayOfNulls(size)
//            }
//        }
//    }


}

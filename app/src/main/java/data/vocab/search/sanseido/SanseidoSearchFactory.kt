package data.vocab.search.sanseido

import data.vocab.model.Search
import data.vocab.model.SearchFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


object SanseidoSearchFactory : SearchFactory {

    // TODO: Fix 宝物 not being able to redirect to itself properly. Goes to 宝
    // I can do this by stripping all non kana/kanji chars from the word source and putting it in the search.
    // TODO: Maybe something more graceful or renaming
    // TODO: Find these characters individually?
    private val TAG = SanseidoSearchFactory::class.java.simpleName

    //TODO: Refactor to it uses the enum type
    private const val SANSEIDO_WORD_ID = "word"
    private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
    private const val MULTIPLE_DEFINITION_REGEX = "▼"
    private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"

    override fun getSearch(html: String, wordLanguageCode: String, definitionLanguageCode: String): Search {
        val document = Jsoup.parse(html)
        val wordSource = findWordSource(document)
        val definitionSource = findDefinitionSource(document)
        val dictionaryEntry = if (definitionSource.isNullOrBlank()) {
            SanseidoDictionaryEntryFactory.getInvalidDictionaryEntry(
                    wordSource,
                    wordLanguageCode,
                    definitionLanguageCode
            )
        } else{
            SanseidoDictionaryEntryFactory.getDictionaryEntry(
                    wordSource,
                    wordLanguageCode,
                    definitionSource,
                    definitionLanguageCode
            )
        }
        val relatedWords = SanseidoRelatedWordFactory
                .getRelatedWords(document, wordLanguageCode, definitionLanguageCode)
        return Search(dictionaryEntry, relatedWords)

    }

    /**
     * Retrieve the searched word from the html source
     * @param html the html source
     * @return the word searched for
     */
    private fun findWordSource(html: Document): String {
        return html.getElementById(SANSEIDO_WORD_ID).text()
    }

    /**
     * A helper method to isolate the source text of the definition of the word searched.
     * @param html the jsoup html document tree.
     * @return the raw definition source
     */
    private fun findDefinitionSource(html: Document): String? {
        val definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID)
        // The definition is in a further div, single child
        var definitionSource : String? = null

        if (definitionParentElement.children().size > 0) {
            definitionSource = definitionParentElement.child(0).text()
            //TODO: FIX REGEX
            definitionSource = definitionSource.replace(MULTIPLE_DEFINITION_REGEX.toRegex(), MULTIPLE_DEFINITION_SEPARATOR)
            definitionSource = definitionSource.replaceFirst(MULTIPLE_DEFINITION_REGEX.toRegex(), MULTIPLE_DEFINITION_SEPARATOR)
        }

        return definitionSource
    }


//    /**
//     * Constructor to create an object containing the information retrieved from Sanseidou from
//     * a search given a word to search.
//     * @param wordToSearch the desired word to search.
//     * @throws IOException if the search cannot be completed
//     */
//    @Throws(IOException::class)
//    constructor(wordToSearch: String,
//                wordLanguageCode: String,
//                definitionLanguageCode: String,
//                matchType: MatchType) {
//        //TODO: Fix relatedWords searching, as it is not working properly for forwards search
//        if (TextUtils.isEmpty(wordToSearch)) {
//            throw IllegalArgumentException("Search term cannot be empty!")
//        }
//        if (wordLanguageCode.isBlank() || definitionLanguageCode.isBlank()){
//            throw IllegalArgumentException("Language codes cannot be blank!")
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
//    /**
//     * Helper method to create an HTTP request to Sanseido for a given URL.
//     * @param searchURL the URL of the word search to be performed
//     * @return A Jsoup html document tree of the html source from the search.
//     * @throws IOException if the search cannot be completed.
//     */
//    @Throws(IOException::class)
//    private fun fetchSanseidoSource(searchURL: URL): Document {
//        return Jsoup.connect(searchURL.toString()).get()
//    }

    // ================================ HELPERS ===================================

}

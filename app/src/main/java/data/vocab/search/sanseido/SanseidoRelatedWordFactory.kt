package data.vocab.search.sanseido

import data.vocab.model.RelatedWordFactory
import data.vocab.model.lang.JapaneseVocabulary
import data.vocab.shared.WordListEntry
import org.jsoup.nodes.Document
import java.util.regex.Pattern

object SanseidoRelatedWordFactory : RelatedWordFactory{
    private const val EXACT_WORD_REGEX = "(?<=［).*(?=］)"
//    private const val RELATED_WORDS_TYPE_CLASS_INDEX = 0
    private const val RELATED_WORDS_VOCAB_INDEX = 1
    private const val RELATED_WORDS_TABLE_INDEX = 0

    //    //TODO: Add all related words by navigating through the web view and appending.
//    fun addRelatedWords(html: Document) {
//        if (relatedWords == null) {
//            relatedWords = ArrayList()
//        }
//        relatedWords!!.addAll(findRelatedWords(html))
//    }
//    /**
//     * Helper method to isolate the related words of a search from the raw html source.
//     * @param html the raw html jsoup document tree.
//     * @return a map of related words in a set with the key being the dictionary they exist in.
//     */
    override fun getRelatedWords(html: Document,
                                 wordLanguageCode: String,
                                 definitionLanguageCode: String)
            : Array<WordListEntry> {
        val relatedWordEntries = ArrayList<WordListEntry>()

        // The related words table is the first table in the HTML
        val table = html.select("table")[RELATED_WORDS_TABLE_INDEX]
        val rows = table.select("tr")

        // Preferred selecting by exact word first, but attempt others if it is a message input
        // like many that exist in the Sanseidou searches that are not exact or if kana is
        // inputted

        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
        // TODO: Add UI element that allows user to modify their entry from the word source
        for (row in rows) {

            val columns = row.select("td")

//            var dictionaryTypeString: String = columns[RELATED_WORDS_TYPE_CLASS_INDEX].text()
//
//            //Essentially using JapaneseDictionaryType enum by substringing out the brackets 【 】
//            if (dictionaryTypeString != null) {
//                dictionaryTypeString = dictionaryTypeString.substring(1, dictionaryTypeString.length - 1)

            val tableEntry = columns[RELATED_WORDS_VOCAB_INDEX].text()
            val exactMatcher = Pattern
                    .compile(EXACT_WORD_REGEX)
                    .matcher(tableEntry)

            val isolatedWord =  if(exactMatcher.find()) {
                                    exactMatcher.group(0)
                                } else{
                                    JapaneseVocabulary.isolateWord(tableEntry)
                                }


            val link = columns.select("a").first().attr("href")

            relatedWordEntries.add(WordListEntry(isolatedWord,
                                                wordLanguageCode,
                                                definitionLanguageCode,
                                                link))
//            }
        }
        //TODO: Check the extension method to see if this is expensive
        return relatedWordEntries.toTypedArray()
    }
}
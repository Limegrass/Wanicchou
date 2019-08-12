package data.web.sanseido

import data.arch.lang.JapaneseVocabulary
import data.arch.util.IFactory
import data.enums.Dictionary
import data.enums.Language
import data.models.Definition
import data.models.DictionaryEntry
import data.models.Vocabulary
import org.jsoup.nodes.Document
import java.util.regex.Pattern

// Not great exposing Document to another class, but allows it to be testable and
// theoretically there are other ways to generate Jsoup documents.
class SanseidoDictionaryEntryFactory (private val document : Document,
                                      private val wordLanguage: Language,
                                      private val definitionLanguage: Language)
    : IFactory<List<DictionaryEntry>> {

    override fun get(): List<DictionaryEntry> {
        val searchWordSource = document.getElementById(SANSEIDO_WORD_ID).text()
        val vocabulary = getVocabulary(searchWordSource, wordLanguage)
        if (vocabulary.word == ""){
            return listOf()
        }
        val definition = getDefinition(document, definitionLanguage)
        val relatedVocabulary = getRelatedVocabulary(document, wordLanguage)
        val relatedDictionaryEntries = relatedVocabulary.map {
            DictionaryEntry(it)
        }
        val dictionaryEntries = mutableListOf(DictionaryEntry(vocabulary, listOf(definition)))
        dictionaryEntries.addAll(relatedDictionaryEntries)

        return dictionaryEntries
    }

    companion object {
        private const val EXACT_WORD_REGEX = "(?<=［).*(?=］)"
        private const val EXACT_EJ_REGEX = ".*(?=［.*］)"
        private const val SEPARATOR_FRAGMENTS_REGEX = "[△▲･・]"
        private const val PRONUNCIATION_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+" +
                "($|[\\p{script=Han}０-９]|\\d|\\s)*?"
        private const val SANSEIDO_WORD_ID = "word"

        private const val RELATED_WORDS_VOCAB_INDEX = 1
        private const val RELATED_WORDS_TABLE_INDEX = 0

        private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
        private const val MULTIPLE_DEFINITION_REGEX = "▼"
        private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"
    }

    //<editor-fold desc="Vocabulary">
    private fun getVocabulary(wordSource: String,
                              wordLanguage: Language) : Vocabulary {
        val word = isolateWord(wordSource, wordLanguage)
        val pronunciation = isolateReading(wordSource, wordLanguage)
        val pitch = JapaneseVocabulary.isolatePitch(wordSource)
        return Vocabulary(word,
                pronunciation,
                pitch,
                wordLanguage)
    }

    /**
     * Isolates the full word from the possibly messy Sanseido html source.
     * This prioritizes Sanseido's formatting with square brackets first, then
     * uses the JapaneseVocabulary.isolateWord helper methods if that fails.
     */
    @Throws(IllegalArgumentException::class)
    private fun isolateWord(wordSource: String, wordLanguage: Language): String {
        val cleanedWordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
        //TODO: Move use make english vocab if it is
        if (wordLanguage == Language.ENGLISH) {
            val ejMatcher = Pattern.compile(EXACT_EJ_REGEX).matcher(cleanedWordSource)
            if (ejMatcher.find()) {
                return ejMatcher.group(0)
            }
            return cleanedWordSource
        }
        else if (wordLanguage == Language.JAPANESE) {
            val exactMatcher = Pattern
                    .compile(EXACT_WORD_REGEX)
                    .matcher(cleanedWordSource)

            return if(exactMatcher.find()) {
                exactMatcher.group(0)
            } else{
                JapaneseVocabulary.isolateWord(cleanedWordSource)
            }
        }
        throw IllegalArgumentException("Invalid Language Code: $wordLanguage for Sanseido." +
                " Source: $cleanedWordSource.")
    }

    /**
     * Helper method to isolate the reading of a Japanese word from its source string,
     * often enclosed on Sanseido within square brackets
     */
    private fun isolateReading(wordSource: String, wordLanguage: Language): String {
        if (wordSource == "") {
            return ""
        }
        // Dic uses images to show pronunciations in the International Phonetic Alphabet
        // Maybe work around some other time
        if (wordLanguage == Language.ENGLISH) {
            var splitPosition = wordSource.indexOf('[')
            if (splitPosition < 0) {
                splitPosition = wordSource.indexOf('［')
            }
            if (splitPosition > 0) {
                return wordSource.substring(0, splitPosition)
            }
        }

        val strippedWordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
        val readingMatcher = Pattern.compile(PRONUNCIATION_REGEX).matcher(strippedWordSource)
        return if (readingMatcher.find()) {
            readingMatcher.group(0)
        } else strippedWordSource
    }
    //</editor-fold>
    //<editor-fold desc="Definition">
    private fun getDefinition(htmlDocument: Document,
                              definitionLanguage: Language) : Definition {
        val definitionSource = getDefinitionSource(htmlDocument)
        val definition = formatDefinitionSource(definitionSource)
        return Definition(definition,
                definitionLanguage,
                Dictionary.SANSEIDO)
    }

    /**
     * Helper method which accesses Sanseido's definition tag and formats its new line characters.
     */
    private fun formatDefinitionSource(definitionSource : String): String {
        var formattedDefinition = definitionSource
        //TODO: FIX REGEX
        formattedDefinition = formattedDefinition.replace(MULTIPLE_DEFINITION_REGEX.toRegex(),
                MULTIPLE_DEFINITION_SEPARATOR)

        return formattedDefinition.trim()
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
    //</editor-fold>

    //<editor-fold desc="Related Vocabulary">
    private fun getRelatedVocabulary(document: Document,
                                     wordLanguage : Language): List<Vocabulary> {
        val relatedWordEntries = ArrayList<Vocabulary>()
        val table = document.select("table")[RELATED_WORDS_TABLE_INDEX]
        val rows = table.select("tr")

        for (row in rows) {
            val columns = row.select("td")
            val tableEntry = columns[RELATED_WORDS_VOCAB_INDEX].text()
            val relatedVocabulary = getVocabulary(tableEntry, wordLanguage)
            relatedWordEntries.add(relatedVocabulary)
        }

        return relatedWordEntries
    }
    //</editor-fold>
}
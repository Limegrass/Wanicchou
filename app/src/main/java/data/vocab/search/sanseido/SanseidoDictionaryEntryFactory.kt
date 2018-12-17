package data.vocab.search.sanseido

import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.vocab.model.lang.EnglishVocabulary
import data.vocab.model.lang.JapaneseVocabulary
import data.vocab.model.DictionaryEntry
import data.vocab.model.DictionaryEntryFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

//TODO: Figure out if this singleton method is even the right way
// To handle it in Kotlin. Worried about start time if there are many factories
// Words to test: 計る, アニメ、animation, 雪害
// Should these be in the Android Strings file?
// Regexes, not sure if they should be const static.
// Most vocab are enclosed in the braces.
object SanseidoDictionaryEntryFactory: DictionaryEntryFactory {

    private const val EXACT_WORD_REGEX = "(?<=［).*(?=］)"
    private const val EXACT_EJ_REGEX = ".*(?=［.*］)"
    private const val SEPARATOR_FRAGMENTS_REGEX = "[△▲･・]"
    private const val PRONUNCIATION_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+" +
            "($|[\\p{script=Han}０-９]|\\d|\\s)*?"
    private const val DICTIONARY_NAME = "Sanseido"

    private const val SANSEIDO_WORD_ID = "word"
    private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
    private const val MULTIPLE_DEFINITION_REGEX = "▼"
    private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"

    override fun getDictionaryEntry(html: String,
                                    wordLanguageCode: String,
                                    definitionLanguageCode: String): DictionaryEntry {
        val document = Jsoup.parse(html)
        val wordSource = findWordSource(document)
        val definitionSource = findDefinitionSource(document)
        return if (definitionSource.isNullOrBlank()) {
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
    }

    /**
     * Constructor given a string containing the word and a string containing the definition.
     * @param wordSource a string that contains the source of the word.
     * @param definitionSource a string containing the definition of the word.
     */
    override fun getDictionaryEntry(wordSource: String,
                                    wordLanguageCode: String,
                                    definitionSource: String,
                                    definitionLanguageCode: String) : DictionaryEntry {
//        TODO("Get dictionaryID from dictionary name instead of passing it in?")
        val wordStripped = wordSource.trim { it <= ' ' }
        val definition = definitionSource.trim { it <= ' ' }
        val word = isolateWord(wordStripped, wordLanguageCode)
        val pronunciation = isolateReading(wordStripped, wordLanguageCode)
        val pitch = isolatePitch(wordStripped)

        return DictionaryEntry(DICTIONARY_NAME,
                wordLanguageCode,
                word,
                pronunciation,
                pitch,
                definitionLanguageCode,
                definition)
    }


    /**
     * Constructs a Japanese Vocab object from an element from the Vocabulary database.
     */
    override fun getDictionaryEntry(vocab: Vocabulary, def: Definition): DictionaryEntry {
        val wordLanguageCode = vocab.languageCode
        val word = vocab.word
        val pronunciation = vocab.pronunciation
        val pitch = vocab.pitch
        val definitionLanguageCode = def.languageCode
        val definition = def.definitionText
        return DictionaryEntry(DICTIONARY_NAME,
                wordLanguageCode,
                word,
                pronunciation,
                pitch,
                definitionLanguageCode,
                definition)
    }

    /**
     * Constructor for invalid word searches, to avoid repeatedly requesting invalid searches.
     * @param invalidWord The word whose search completed but failed.
     */
    override fun getInvalidDictionaryEntry(invalidWord: String,
                                           wordLanguageCode: String,
                                           definitionLanguageCode: String): DictionaryEntry {
        val word = invalidWord
        val pronunciation = "N/A"
        val pitch = "N/A"
        val definition = "N/A"

        return DictionaryEntry(DICTIONARY_NAME,
                wordLanguageCode,
                word,
                pronunciation,
                pitch,
                definitionLanguageCode,
                definition)
    }


    /**
     * Isolates the full word from the possibly messy Sanseidou html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    @Throws(IllegalArgumentException::class)
    private fun isolateWord(wordSource: String, wordLanguageCode: String): String {
        val cleanedWordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
        //TODO: Move use make english vocab if it is
        if (wordLanguageCode == EnglishVocabulary.LANGUAGE_CODE) {
            val ejMatcher = Pattern.compile(EXACT_EJ_REGEX).matcher(cleanedWordSource)
            if (ejMatcher.find()) {
                return ejMatcher.group(0)
            }
            return cleanedWordSource
        }
        else if (wordLanguageCode == JapaneseVocabulary.LANGUAGE_CODE) {
            val exactMatcher = Pattern
                    .compile(EXACT_WORD_REGEX)
                    .matcher(cleanedWordSource)

            return if(exactMatcher.find()) {
                exactMatcher.group(0)
            } else{
                JapaneseVocabulary.isolateWord(cleanedWordSource)
            }
        }
        throw IllegalArgumentException("Invalid Language Code: $wordLanguageCode for Sanseidou." +
                " Source $cleanedWordSource")
    }

    /**
     * Helper method to isolate the reading of a Japanese dictionaryEntry word from its source string.
     * @param wordSource the raw string containing the dictionaryEntry word.
     * @return a string with the isolated kana reading of the word.
     */
    private fun isolateReading(wordSource: String, wordLanguageCode: String): String {
        if (wordSource == "") {
            return ""
        }
        // Dic uses images to show pronunciations in the International Phonetic Alphabet
        // Maybe work around some other time
        if (wordLanguageCode == EnglishVocabulary.LANGUAGE_CODE) {
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

    private fun isolatePitch(wordSource: String): String {
        if (wordSource.isBlank()) {
            return ""
        }

        var pitch = ""
        val toneMatcher = Pattern.compile(JapaneseVocabulary.TONE_REGEX).matcher(wordSource)
        if (toneMatcher.find()) {
            pitch = toneMatcher.group(0)
        }
        return pitch
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
            definitionSource = definitionSource.replace(MULTIPLE_DEFINITION_REGEX.toRegex(),
                                                        MULTIPLE_DEFINITION_SEPARATOR)
            definitionSource = definitionSource.replaceFirst(MULTIPLE_DEFINITION_REGEX.toRegex(),
                                                        MULTIPLE_DEFINITION_SEPARATOR)
        }

        return definitionSource
    }
}

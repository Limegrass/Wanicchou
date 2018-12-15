package data.vocab.lang.jp

import android.os.Parcelable
import data.vocab.model.DictionaryEntry
import kotlinx.android.parcel.Parcelize

/**
 * Created by Limegrass on 4/4/2018.
 */

@Parcelize
class JapaneseVocabulary(override val wordLanguageID: Int,
                         override val definitionLanguageID: Int,
                         override val word: String = "",
                         override val definition: String,
                         override val pronunciation: String = "",
                         override val pitch: String = "") : Parcelable, DictionaryEntry {

//    /**
//     * Generates an Anki format furigana string from the word and reading saved.
//     * @return a string for Anki's furigana display.
//     */
//    val furigana: String
//        get() = if (word == pronunciation) {
//            pronunciation
//        } else "$word[$pronunciation]"
//

//    /**
//     * Constructor given a string containing the word and a string containing the definition.
//     * @param wordSource a string that contains the source of the word.
//     * @param definitionSource a string containing the definition of the word.
//     */
//    constructor(wordSource: String, definitionSource: String) {
//        var wordSource = wordSource
//        var definitionSource = definitionSource
//        wordSource = wordSource.trim { it <= ' ' }
//        definitionSource = definitionSource.trim { it <= ' ' }
//
//        definition = definitionSource
//        word = isolateWord(wordSource, dictionaryType)
//        pronunciation = isolateReading(wordSource, dictionaryType)
//        pitch = isolatePitch(wordSource)
//        this.dictionaryType = dictionaryType
//    }
//
//    /**
//     * Constructs a Japanese Vocab object from an element from the DictionaryEntry database.
//     * @param entity An entry from the DictionaryEntry Database
//     */
//    constructor(vocab: Vocabulary, def: Definition) {
//        word = vocab.word
//        pronunciation = vocab.pronunciation
//        definition = def.definitionText
//        pitch = vocab.pitch
//        vocab.languageID
//
//    }
//
//    /**
//     * Constructor for invalid word searches, to avoid repeatedly requesting invalid searches.
//     * @param invalidWord The word whose search completed but failed.
//     * @param dictionaryType The dictionary type the search completed but failed under.
//     */
//    constructor(invalidWord: String, vocabLanguageID: ) {
//        definition = "N/A"
//        word = invalidWord
//        pronunciation = "N/A"
//        pitch = "N/A"
//        this.dictionaryType = dictionaryType
//    }
//
//    /**
//     * Finds the tone from the given word source information
//     * @param wordSource the raw information about the word
//     * @return a string of the pitch of the word
//     */
//    private fun isolatePitch(wordSource: String?): String {
//        if (wordSource == null || wordSource == "") {
//            return ""
//        }
//
//        var tone = ""
//        val toneMatcher = Pattern.compile(TONE_REGEX).matcher(wordSource)
//        if (toneMatcher.find()) {
//            tone = toneMatcher.group(0)
//        }
//        return tone
//    }
//
//    /**
//     * Helper method to isolate the reading of a Japanese dictionaryEntry word from its source string.
//     * @param wordSource the raw string containing the dictionaryEntry word.
//     * @return a string with the isolated kana reading of the word.
//     */
//    private fun isolateReading(wordSource: String?, dictionaryType: DictionaryType): String {
//        var wordSource = wordSource
//        if (wordSource == null || wordSource == "") {
//            return ""
//        }
//        // Dic uses images to show pronunciations in the International Phonetic Alphabet
//        // Maybe work around some other time
//        if (dictionaryType === JapaneseDictionaryType.EJ) {
//            var splitPos = wordSource.indexOf('[')
//            if (splitPos < 0) {
//                splitPos = wordSource.indexOf('［')
//            }
//            if (splitPos > 0) {
//                return wordSource.substring(0, splitPos)
//            }
//        }
//
//
//        wordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
//        val readingMatcher = Pattern.compile(READING_REGEX).matcher(wordSource)
//        return if (readingMatcher.find()) {
//            readingMatcher.group(0)
//        } else wordSource
//    }
//
//
//    companion object {
//        // Words to test: 計る, アニメ、animation, 雪害
//
//        // Should these be in the Android Strings file?
//        // Regexes, not sure if they should be const static.
//        // Most vocab are enclosed in the braces.
//        private val EXACT_WORD_REGEX = "(?<=［).*(?=］)"
//        private val EXACT_EJ_REGEX = ".*(?=［.*］)"
//
//        // Try to find a word beginning with or enclosed with Kanji
//        private val WORD_WITH_KANJI_REGEX = "\\p{script=Han}+[\\p{script=Hiragana}|\\p{script=Katakana}]*\\p{script=Han}*"
//
//        // For finding only the kana of a word.
//        private val KANA_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+"
//
//        private val READING_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+($|[\\p{script=Han}０-９]|\\d|\\s)*?"
//        private val TONE_REGEX = "[\\d０-９]+"
//
//        // Some messy dictionary entries have triangles in
//        private val SEPARATOR_FRAGMENTS_REGEX = "[△▲･・]"
//
//        private val languageID = 1 // Japanese, assuming I populate the DB
//
//        // TODO: Maybe do something in Sanseidou search for related words so this can be private
//
//        /**
//         * Isolates the full word from the possibly messy Sanseidou html source
//         * @param wordSource the raw string from the html source
//         * @return The full word isolated from any furigana readings or tones
//         */
//        fun isolateWord(wordSource: String, dictionaryType: DictionaryType): String {
//            var wordSource = wordSource
//            wordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
//            if (dictionaryType === JapaneseDictionaryType.EJ) {
//                val ejMatcher = Pattern.compile(EXACT_EJ_REGEX).matcher(wordSource)
//                if (ejMatcher.find()) {
//                    return ejMatcher.group(0)
//                }
//            }
//            val exactMatcher = Pattern.compile(EXACT_WORD_REGEX).matcher(wordSource)
//            val kanjiMatcher = Pattern.compile(WORD_WITH_KANJI_REGEX).matcher(wordSource)
//            val kanaMatcher = Pattern.compile(KANA_REGEX).matcher(wordSource)
//
//            return if (exactMatcher.find()) {
//                exactMatcher.group(0)
//            } else if (kanjiMatcher.find()) {
//                kanjiMatcher.group(0)
//            } else if (kanaMatcher.find()) {
//                kanaMatcher.group(0)
//            } else {
//                wordSource
//            }
//        }
//    }

    /**
     * Compares by word, pronunciation, definition, and pitch
     * @param other another JapaneseVocabulary instance
     * @return true if word, pronunciation, definition, and pitch are all the same value
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is JapaneseVocabulary) {
            return false
        }
        val otherVocabulary = other as JapaneseVocabulary?

        //Maybe not include definition in case of different site definitions or formatting.
        return (word == otherVocabulary!!.word
                && pronunciation == otherVocabulary.pronunciation
                && definition == otherVocabulary.definition
                && pitch == otherVocabulary.pitch)
    }

    /**
     * Hashes by word, pronunciation, definition, and pitch
     * @return a hashcode for the dictionaryEntry word
     */
    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + word.hashCode()
        hash = 31 * hash + pronunciation.hashCode()
        hash = 31 * hash + definition.hashCode()
        hash = 31 * hash + pitch.hashCode()
        return hash
    }

}

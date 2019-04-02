package data.arch.info.vocabulary.shared

import data.arch.lang.EnglishVocabulary
import data.arch.lang.JapaneseVocabulary
import data.room.entity.Vocabulary
import java.util.regex.Pattern

internal class SanseidoVocabularyStrategy : IVocabularyStrategy {
    companion object {
        private const val EXACT_WORD_REGEX = "(?<=［).*(?=］)"
        private const val EXACT_EJ_REGEX = ".*(?=［.*］)"
        private const val SEPARATOR_FRAGMENTS_REGEX = "[△▲･・]"
        private const val PRONUNCIATION_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+" +
                "($|[\\p{script=Han}０-９]|\\d|\\s)*?"
    }

    override fun get(wordSource: String,
                     wordLanguageID: Long) : Vocabulary {
        val word = isolateWord(wordSource, wordLanguageID)
        val pronunciation = isolateReading(wordSource, wordLanguageID)
        val pitch = JapaneseVocabulary.isolatePitch(wordSource)
        return Vocabulary(word,
                wordLanguageID,
                pronunciation,
                pitch)
    }

    /**
     * Isolates the full word from the possibly messy Sanseidou html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    @Throws(IllegalArgumentException::class)
    private fun isolateWord(wordSource: String, wordLanguageID: Long): String {
        val cleanedWordSource = wordSource.replace(SEPARATOR_FRAGMENTS_REGEX.toRegex(), "")
        //TODO: Move use make english vocab if it is
        if (wordLanguageID == EnglishVocabulary.LANGUAGE_ID) {
            val ejMatcher = Pattern.compile(EXACT_EJ_REGEX).matcher(cleanedWordSource)
            if (ejMatcher.find()) {
                return ejMatcher.group(0)
            }
            return cleanedWordSource
        }
        else if (wordLanguageID == JapaneseVocabulary.LANGUAGE_ID) {
            val exactMatcher = Pattern
                    .compile(EXACT_WORD_REGEX)
                    .matcher(cleanedWordSource)

            return if(exactMatcher.find()) {
                exactMatcher.group(0)
            } else{
                JapaneseVocabulary.isolateWord(cleanedWordSource)
            }
        }
        throw IllegalArgumentException("Invalid Language Code: $wordLanguageID for Sanseido." +
                " Source: $cleanedWordSource.")
    }

    /**
     * Helper method to isolate the reading of a Japanese dictionaryEntry word from its source string.
     * @param wordSource the raw string containing the dictionaryEntry word.
     * @return a string with the isolated kana reading of the word.
     */
    private fun isolateReading(wordSource: String, wordLanguageID: Long): String {
        if (wordSource == "") {
            return ""
        }
        // Dic uses images to show pronunciations in the International Phonetic Alphabet
        // Maybe work around some other time
        if (wordLanguageID == EnglishVocabulary.LANGUAGE_ID) {
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

}
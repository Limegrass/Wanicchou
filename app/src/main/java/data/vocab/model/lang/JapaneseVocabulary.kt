package data.vocab.model.lang

import data.vocab.model.DictionaryEntry
import java.util.regex.Pattern

/**
 * Created by Limegrass on 4/4/2018.
 */

class JapaneseVocabulary {
    companion object {
        /**
         * Generates an Anki format furigana string if word is not its pronunciation
         * @return a string for Anki's furigana display.
         */
        fun getFurigana(vocabulary: DictionaryEntry): String {
            return if (vocabulary.word == vocabulary.pronunciation) {
                vocabulary.pronunciation
            } else "$vocabulary.word[${vocabulary.pronunciation}]"
        }


        // Kanji followed by Kana
        const val WORD_WITH_KANJI_REGEX = "\\p{script=Han}+[\\p{script=Hiragana}|\\p{script=Katakana}]*\\p{script=Han}*"
        // Pure Kana
        const val KANA_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+"
        // Tone, accounting for full-width numbers
        const val TONE_REGEX = "[\\d０-９]+"

        const val LANGUAGE_CODE = "jp" // Japanese, assuming I populate the DB

        fun isolateWord(wordSource: String): String {
            val kanjiMatcher = Pattern
                    .compile(JapaneseVocabulary.WORD_WITH_KANJI_REGEX)
                    .matcher(wordSource)
            val kanaMatcher = Pattern
                    .compile(JapaneseVocabulary.KANA_REGEX)
                    .matcher(wordSource)
            return when {
                kanjiMatcher.find() -> kanjiMatcher.group(0)
                kanaMatcher.find() -> kanaMatcher.group(0)
                else -> wordSource
            }
        }
    }
}

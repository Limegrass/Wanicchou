package data.arch.lang

import java.util.regex.Pattern

/**
 * Created by Limegrass on 4/4/2018.
 */

class JapaneseVocabulary {
    companion object {
        // Kanji followed by Kana
        private const val WORD_WITH_KANJI_REGEX = "\\p{script=Han}+[\\p{script=Hiragana}|\\p{script=Katakana}]*\\p{script=Han}*"
        // Pure Kana
        private const val KANA_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+"
        // Tone, accounting for full-width numbers
        private const val TONE_REGEX = "[\\d０-９]+"

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

        fun isolatePitch(wordSource: String): String {
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

    }
}

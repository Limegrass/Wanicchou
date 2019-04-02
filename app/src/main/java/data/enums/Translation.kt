package data.enums

import android.os.Build
import data.arch.lang.EnglishVocabulary
import data.arch.lang.JapaneseVocabulary
import java.lang.UnsupportedOperationException
import java.util.*

/**
 * I can make these into android strings as overrides for the resulting voacb lang id -> dic lang id
 * If the base is not overridden, then it will use the java Locale names
 * I should add all the languages to the database on install form
 *
String[] test = Locale.getISOLanguages();
for (String language : test) {
System.out.println(Locale.forLanguageTag(language).getDisplayLanguage());
}
 * */
/**
 * Enum listing all available languages.
 */

class Translation {
    companion object {
        fun getTranslationDisplay(wordLanguageID: Long, definitionLanguageID: Long): String {
            /**
             * Query the DB for the vocab language IDs and get their language codes
             * pass it in
             * Use the language codes to get the locales
             * Return the locale
             */
            return when (wordLanguageID) {
                JapaneseVocabulary.LANGUAGE_ID -> getJapaneseDisplayDictionary(definitionLanguageID)
                EnglishVocabulary.LANGUAGE_ID -> getEnglishDisplayDictionary(definitionLanguageID)
                else -> throw UnsupportedOperationException("Unknown word language ID: [$wordLanguageID]")
            }
        }
        //TODO("Move strings to android string resources")
        //TODO("Maybe this whole thing can change but I'll think about it some other time since it's isolated")
        private fun getJapaneseDisplayDictionary(definitionLanguageID: Long): String {
            return when (definitionLanguageID) {
                EnglishVocabulary.LANGUAGE_ID -> "和英"
                JapaneseVocabulary.LANGUAGE_ID -> "国語"
                else -> throw UnsupportedOperationException("Unknown definition language ID for Japanese: [$definitionLanguageID]")
            }
        }
        private fun getEnglishDisplayDictionary(definitionLanguageID: Long): String {
            return when (definitionLanguageID) {
                JapaneseVocabulary.LANGUAGE_ID -> "英和"
                else -> throw UnsupportedOperationException("Unknown definition language ID for English: [$definitionLanguageID]")
            }
        }

        private fun getDefaultDisplay(vocabLanguageCode: String, defLanguageCode: String): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Locale.forLanguageTag(vocabLanguageCode).displayLanguage +
                        " -> " + Locale.forLanguageTag(defLanguageCode).displayLanguage
            } else {
                vocabLanguageCode.toUpperCase() + " -> " + defLanguageCode.toUpperCase()
            }
        }

//        //TODO: Actually make this method instead of hacky assumptions, move them somewhere else?
//        private fun assignLanguageCode(str: String,
//                                       default: String = JapaneseVocabulary.LANGUAGE_ID): String {
//            return when {
//                JapaneseVocabulary.isJapaneseInput(str) -> JapaneseVocabulary.LANGUAGE_ID
//                //TODO: use kana/jp regex for jp
//                else -> default
//            }
//        }
    }

}

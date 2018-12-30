package data.enums

import android.os.Build
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
        fun getTranslationDisplay(vocabLanguageCode: String, defLanguageCode: String): String {
            /**
             * Query the DB for the vocab language IDs and get their language codes
             * pass it in
             * Use the language codes to get the locales
             * Return the locale
             */
            return when (vocabLanguageCode) {
                "jp" -> getJapaneseDisplayDictionary(defLanguageCode)
                "en" -> getEnglishDisplayDictionary(defLanguageCode)
                else -> getDefaultDisplay(vocabLanguageCode, defLanguageCode)
            }
        }
        //TODO("Move strings to android string resources")
        //TODO("Maybe this whole thing can change but I'll think about it some other time since it's isolated")
        private fun getJapaneseDisplayDictionary(defLanguageCode: String): String {
            return when (defLanguageCode) {
                "en" -> "和英"
                "jp" -> "国語"
                else -> getDefaultDisplay("jp", defLanguageCode)
            }
        }
        private fun getEnglishDisplayDictionary(defLanguageCode: String): String {
            return when (defLanguageCode) {
                "jp" -> "英和"
                else -> getDefaultDisplay("en", defLanguageCode)
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
    }

}

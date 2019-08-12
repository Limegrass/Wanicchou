package data.enums

import java.util.Locale

enum class Language {
    JAPANESE,
    ENGLISH;

    val languageID : Long
        get() {
        return when (this) {
            JAPANESE -> 1L
            ENGLISH -> 2L
        }
    }

    val languageCode : String
        get() {
            return when (this) {
                JAPANESE -> Locale.JAPANESE.isO3Language
                ENGLISH -> Locale.ENGLISH.isO3Language
            }
        }

    val displayName : String
        get(){
            return when (this){
                JAPANESE -> Locale.JAPANESE.getDisplayLanguage(Locale.JAPANESE)
                ENGLISH -> Locale.ENGLISH.getDisplayLanguage(Locale.ENGLISH)
            }
        }

    companion object {
        fun getLanguage(languageID : Long) : Language {
            return values().single {
                it.languageID == languageID
            }
        }
    }
}
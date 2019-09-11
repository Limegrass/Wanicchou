package data.enums

enum class Dictionary {
    SANSEIDO;

    val dictionaryID : Long
        get() {
            return when (this) {
                SANSEIDO -> 1L
            }
        }
    val dictionaryName : String
        get() {
            return when (this) {
                SANSEIDO -> "三省堂"
            }
        }

    val defaultVocabularyLanguage : Language
        get() {
            return when (this) {
                SANSEIDO -> Language.JAPANESE
            }
        }

    val defaultDefinitionLanguage : Language
        get() {
            return when (this) {
                SANSEIDO -> Language.JAPANESE
            }
        }

    companion object {
        fun getDictionary(dictionaryID : Long) : Dictionary {
            return values().single{ it.dictionaryID == dictionaryID }
        }
    }
}
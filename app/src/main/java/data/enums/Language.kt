package data.enums

enum class Language {
    JAPANESE,
    ENGLISH;

    val id : Long
        get() {
        return when (this) {
            JAPANESE -> 1L
            ENGLISH -> 2L
        }
    }

    val code : String
        get() {
            return when (this) {
                JAPANESE -> java.util.Locale.JAPANESE.isO3Language
                ENGLISH -> java.util.Locale.ENGLISH.isO3Language
            }
        }

    val displayName : String
        get(){
            return when (this){
                JAPANESE -> java.util.Locale.JAPANESE.displayName
                ENGLISH -> java.util.Locale.ENGLISH.displayName
            }
        }
}
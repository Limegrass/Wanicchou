package data.vocab.model

import android.os.Parcelable

/**
 * Interface to program a language's dictionaryEntry entry to
 */
interface DictionaryEntry : Parcelable {
    val word: String
    val pronunciation: String
    val definition: String
    val pitch: String
    val wordLanguageID: Int
    val definitionLanguageID: Int
}

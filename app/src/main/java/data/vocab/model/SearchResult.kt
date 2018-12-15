package data.vocab.model

import android.os.Parcelable

import data.vocab.shared.WordListEntry

/**
 * Interface for the necessary components to return from a search.
 */
interface SearchResult : Parcelable {
    val dictionaryEntry: DictionaryEntry
    val relatedWords: Array<WordListEntry>
}

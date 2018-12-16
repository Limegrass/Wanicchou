package data.vocab.model

import android.os.Parcelable

import data.vocab.shared.WordListEntry
import kotlinx.android.parcel.Parcelize

/**
 * Interface for the necessary components to return from a search.
 */
@Parcelize
class Search(val dictionaryEntry: DictionaryEntry,
             val relatedWords: Array<WordListEntry>) : Parcelable {
//    override fun getRelatedWords(): List<WordListEntry>? {
//        return relatedWords
//    }
//
//    protected fun setRelatedWords(relatedWords: MutableList<WordListEntry>) {
//        this.relatedWords = relatedWords
//    }
//
}

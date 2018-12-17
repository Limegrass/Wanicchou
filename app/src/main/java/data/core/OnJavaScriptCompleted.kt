package data.core

import data.vocab.model.DictionaryEntry
import data.vocab.shared.WordListEntry

/**
 * Interface used as a callback, executed after a page finishes loading
 */
interface OnJavaScriptCompleted {
    fun onJavaScriptCompleted(dictionaryEntry: DictionaryEntry,
                              relatedWords: List<WordListEntry>,
                              definitionLanguageCode: String,
                              onDatabaseQuery: OnDatabaseQuery)
}

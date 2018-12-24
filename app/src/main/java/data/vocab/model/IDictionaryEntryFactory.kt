package data.vocab.model

import data.room.entity.Definition
import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

/**
 * Interface for creating a factory responsible for generating DictionaryEntries
 */
interface IDictionaryEntryFactory {
    fun getDictionaryEntry(document: Document,
                           wordLanguageCode: String,
                           definitionLanguageCode: String) : DictionaryEntry

    fun getDictionaryEntry(vocab: Vocabulary, def: Definition): DictionaryEntry
}
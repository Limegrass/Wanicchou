package data.vocab.model

import data.room.entity.Vocabulary
import data.vocab.shared.WordListEntry
import org.jsoup.nodes.Document

interface IRelatedWordFactory{
    fun getRelatedWords(document: Document,
                        wordLanguageCode: String,
                        definitionLanguageCode: String): List<WordListEntry>
    fun getRelatedWords(databaseList: List<Vocabulary>,
                        definitionLanguageCode: String) : List<WordListEntry>
}
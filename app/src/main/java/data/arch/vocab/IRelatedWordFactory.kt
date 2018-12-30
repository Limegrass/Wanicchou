package data.arch.vocab

import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

interface IRelatedWordFactory{
    fun getRelatedWords(document: Document,
                        wordLanguageCode: String,
                        definitionLanguageCode: String): List<WordListEntry>
    fun getRelatedWords(databaseList: List<Vocabulary>,
                        definitionLanguageCode: String) : List<WordListEntry>
}
package data.vocab.model

import data.room.entity.Vocabulary
import data.vocab.shared.WordListEntry

interface RelatedWordFactory{
    fun getRelatedWords(html: String,
                        wordLanguageCode: String,
                        definitionLanguageCode: String): List<WordListEntry>
    fun getRelatedWords(databaseList: List<Vocabulary>,
                        definitionLanguageCode: String) : List<WordListEntry>
}
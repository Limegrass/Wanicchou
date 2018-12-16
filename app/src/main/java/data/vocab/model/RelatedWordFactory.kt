package data.vocab.model

import data.vocab.shared.WordListEntry
import org.jsoup.nodes.Document

interface RelatedWordFactory{
    fun getRelatedWords(html: Document,
                        wordLanguageCode: String,
                        definitionLanguageCode: String): Array<WordListEntry>
}
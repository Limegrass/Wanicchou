package data.arch.vocab

import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

interface IVocabularyFactory {
    fun getVocabulary(document: Document,
                      wordLanguageCode: String) : Vocabulary
}
package data.arch.info.vocabulary.related

import data.arch.util.IFactory
import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

internal class RelatedVocabularyFactory(
        private val htmlDocument: Document,
        private val wordLanguageID : Long,
        private val dictionaryID : Long)
    : IFactory<List<Vocabulary>> {
    override fun get(): List<Vocabulary> {
        val strategy = RelatedVocabularyStrategyFactory(dictionaryID).get()
        return strategy.getRelatedVocabulary(htmlDocument, wordLanguageID)
    }
}


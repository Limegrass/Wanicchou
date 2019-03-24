package data.arch.info.vocabulary.search

import data.arch.info.vocabulary.shared.VocabularyStrategyFactory
import data.arch.util.IFactory
import data.room.entity.Vocabulary
import org.jsoup.nodes.Document

class SearchWordVocabularyFactory(private val htmlDocument : Document,
                                  private val wordLanguageCode : String,
                                  private val dictionaryID : Long) : IFactory<Vocabulary> {
    override fun get(): Vocabulary {
        val wordSource = SearchWordSourceFactory(htmlDocument, dictionaryID).get()
        val strategy = VocabularyStrategyFactory(dictionaryID).get()
        return strategy.get(wordSource, wordLanguageCode)
    }
}
package data.arch.info.vocabulary.search

import data.arch.util.IFactory
import org.jsoup.nodes.Document

internal class SearchWordSourceFactory(private val htmlDocument : Document,
                                private val dictionaryID : Long) : IFactory<String> {
    override fun get(): String {
        val sourceFactory = SearchWordStrategyFactory(dictionaryID)
        val strategy = sourceFactory.get()
        return strategy.getSource(htmlDocument)
    }
}


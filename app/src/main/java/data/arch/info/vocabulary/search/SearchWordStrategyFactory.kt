package data.arch.info.vocabulary.search

import data.arch.info.vocabulary.shared.ISourceStringStrategy
import data.arch.util.IFactory
import data.web.sanseido.SanseidoWebPage

internal class SearchWordStrategyFactory(private val dictionaryID : Long)
    : IFactory<ISourceStringStrategy> {
    override fun get(): ISourceStringStrategy {
        return when (dictionaryID){
            SanseidoWebPage.DICTIONARY_ID -> SanseidoSearchedWordStrategy()
            else -> throw UnsupportedOperationException("Unsupported DICTIONARY_ID $dictionaryID.")
        }
    }
}
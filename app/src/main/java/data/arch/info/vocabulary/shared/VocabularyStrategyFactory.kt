package data.arch.info.vocabulary.shared

import data.arch.util.IFactory
import data.web.sanseido.SanseidoWebPage

// Document => RelatedElementFactory => SearchWordVocabularyFactory
// Document => SearchedWordElementFactory => SearchWordVocabularyFactory
internal class VocabularyStrategyFactory(private val dictionaryID : Long)
    : IFactory<IVocabularyStrategy> {
    override fun get(): IVocabularyStrategy {
        return when (dictionaryID){
            SanseidoWebPage.DICTIONARY_ID -> SanseidoVocabularyStrategy()
            else -> throw UnsupportedOperationException("Unknown dictionary $dictionaryID provided.")
        }
    }
}
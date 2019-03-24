package data.arch.info.vocabulary.related

import data.arch.util.IFactory
import data.web.sanseido.SanseidoWebPage

internal class RelatedVocabularyStrategyFactory(
        private val dictionaryID: Long
) : IFactory<IRelatedVocabularyStrategy> {
    override fun get(): IRelatedVocabularyStrategy {
        return when (dictionaryID){
            SanseidoWebPage.DICTIONARY_ID -> SanseidoRelatedVocabularyStrategy()
            else -> throw UnsupportedOperationException("Unsupported dictionary ID $dictionaryID")
        }
    }
}
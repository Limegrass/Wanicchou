package data.arch.info.definition

import data.arch.util.IFactory
import data.room.entity.Definition
import org.jsoup.nodes.Document

class DefinitionFactory(private val htmlDocument: Document,
                        private val definitionLanguageID : Long,
                        private val dictionaryID: Long,
                        private val vocabularyID: Long): IFactory<Definition>{
    override fun get(): Definition {
        val strategyFactory = DefinitionStrategyFactory(dictionaryID)
        val strategy = strategyFactory.get()
        return strategy.get(htmlDocument, definitionLanguageID, dictionaryID, vocabularyID)
    }
}
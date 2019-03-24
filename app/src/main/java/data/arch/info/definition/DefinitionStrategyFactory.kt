package data.arch.info.definition

import data.arch.util.IFactory
import data.web.sanseido.SanseidoWebPage

internal class DefinitionStrategyFactory(private val dictionaryID: Long) : IFactory<IDefinitionStrategy>{
    override fun get(): IDefinitionStrategy {
        return when (dictionaryID){
            SanseidoWebPage.DICTIONARY_ID -> SanseidoDefinitionStrategy()
            else -> throw UnsupportedOperationException("Unknown dictionary $dictionaryID provided.")
        }
    }

}
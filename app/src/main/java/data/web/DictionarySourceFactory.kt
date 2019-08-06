package data.web

import data.arch.search.IDictionarySource
import data.arch.util.IFactory
import data.enums.Dictionary
import data.web.sanseido.SanseidoSource

class DictionarySourceFactory(private val dictionary : Dictionary)
    : IFactory<IDictionarySource>{
    override fun get(): IDictionarySource {
        return when (dictionary) {
            Dictionary.SANSEIDO -> SanseidoSource()
        }
    }
}
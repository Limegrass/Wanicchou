package data.web

import data.arch.search.IDictionarySearchProvider
import data.arch.search.jsoup.JsoupDictionarySearchProvider
import data.arch.util.IFactory
import data.enums.Dictionary
import data.web.sanseido.SanseidoDictionaryEntryFactory
import data.web.sanseido.SanseidoSource

class DictionarySearchProviderFactory(private val dictionary : Dictionary)
    : IFactory<IDictionarySearchProvider>{
    override fun get(): IDictionarySearchProvider {
        return when (dictionary) {
            Dictionary.SANSEIDO -> {
                val factory = SanseidoDictionaryEntryFactory()
                val source = SanseidoSource()
                JsoupDictionarySearchProvider(source, factory)
            }
        }
    }
}
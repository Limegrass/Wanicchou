package data.web

import data.arch.search.IDictionaryWebPage
import data.arch.util.IFactory
import data.web.sanseido.SanseidoWebPage

class DictionaryWebPageFactory(private val dictionaryID : Long) : IFactory<IDictionaryWebPage>{
    override fun get(): IDictionaryWebPage {
        return when (dictionaryID) {
            SanseidoWebPage.DICTIONARY_ID -> SanseidoWebPage()
            else -> throw UnsupportedOperationException("Unsupported Dictionary with ID $dictionaryID.")
        }
    }
}
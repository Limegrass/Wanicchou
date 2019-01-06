package data.search

import data.arch.lang.EnglishVocabulary
import data.arch.lang.JapaneseVocabulary
import data.arch.search.IDictionaryWebPage
import data.search.sanseido.SanseidoWebPage
import data.enums.MatchType
import java.lang.IllegalArgumentException

//TODO: Make this somehow take into account some pages possibly needing JSwith webview.
// Maybe the builder pattern could be used?

object SearchProvider {
    private lateinit var webPage : IDictionaryWebPage

    fun getWebPage(dictionary: String): IDictionaryWebPage {
        when (dictionary) {
            "Sanseido" -> webPage = SanseidoWebPage()
            else -> throw IllegalArgumentException("Dictionary $dictionary not available.")
        }
        return webPage
    }

    //TODO: Actually make this method instead of hacky assumptions, move them somewhere else?
    private fun assignWordLanguageCodeByInput(input: String,
                                              default: String = JapaneseVocabulary.LANGUAGE_CODE): String {
        return when {
            isEnglishInput(input) -> EnglishVocabulary.LANGUAGE_CODE
            //TODO: use kana/jp regex for jp
            else -> default
        }
    }

    private fun isEnglishInput(input :String): Boolean {
        return input.trim()[0].toInt() < 255
    }

    //TODO: I hate how this is implemented, need some way of forcing abstract static members/fun
    fun getCurrentDictionarySupportedMatchTypes() : Set<MatchType> {
        return webPage.getSupportedMatchTypes()
    }

//    fun getSearch(webPage: WebViewDictionaryWebPage,
//                  html: String,
//                  wordLanguageCode: String,
//                  definitionLanguageCode: String): Search {
//        return webPage.getSearch(html, wordLanguageCode, definitionLanguageCode)
//    }
}
package data.vocab.search

import data.vocab.model.DictionaryWebPage
import data.vocab.model.lang.EnglishVocabulary
import data.vocab.model.lang.JapaneseVocabulary
import data.vocab.search.sanseido.SanseidoWebPage
import java.lang.IllegalArgumentException


//AS in the design method. Takes configuration decisions and provides either a Sanseido web view or another one after
// Use it to remove reflection


// Is this a factory or a provider la
object SearchProvider {

    fun getWebPage(dictionary: String): DictionaryWebPage {
        return when (dictionary) {
            "Sanseido" -> SanseidoWebPage
            else -> throw IllegalArgumentException("Dictionary $dictionary not available.")
        }
    }

//    fun getSearch(webPage: DictionaryWebPage,
//                  html: String,
//                  wordLanguageCode: String,
//                  definitionLanguageCode: String): Search {
//        return webPage.getSearch(html, wordLanguageCode, definitionLanguageCode)
//    }


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
}
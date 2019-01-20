package data.arch.vocab

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.arch.search.IDictionaryWebPage
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.graveyard.DictionaryEntry
import data.enums.MatchType

interface IVocabularyRepository {
    fun getLatest(onQueryFinish: OnQueryFinish)

//    fun saveResults(dictionaryEntry: DictionaryEntry,
//                    relatedWords: List<WordListEntry>)

    fun search(searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS,
               dictionary: String,
               onPageParsed: IDictionaryWebPage.OnPageParsed,
               lifecycleOwner: LifecycleOwner)

    interface OnQueryFinish{
        fun onQueryFinish(vocabularyList: List<Vocabulary>,
                          definitionList: LiveData<List<Definition>>,
                          relatedWords: List<WordListEntry>)
    }
}
package data.room.repository

import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.vocab.model.DictionaryEntry
import data.vocab.model.DictionaryWebPage
import data.vocab.shared.MatchType
import data.vocab.shared.WordListEntry

interface IVocabularyRepository {
    fun getLatest(onQueryFinish: OnQueryFinish)
    fun saveResults(dictionaryEntry: DictionaryEntry,
                    relatedWords: List<WordListEntry>)

    fun search(searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS,
               dictionary: String,
               webView: WebView,
               onPageParsed: DictionaryWebPage.OnPageParsed)

    interface OnQueryFinish{
        fun onQueryFinish(vocabularyList: LiveData<List<Vocabulary>>,
                          definitionList: List<LiveData<List<Definition>>>,
                          relatedWords: List<WordListEntry>)
    }
}
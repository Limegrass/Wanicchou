package data.room.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.core.OnDatabaseQuery
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.room.repository.IVocabularyRepository
import data.room.repository.VocabularyRepository
import data.vocab.shared.MatchType
import data.vocab.shared.WordListEntry


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class SearchViewModel(application: Application)
    : AndroidViewModel(application), OnDatabaseQuery {
    private val vocabularyRepository : IVocabularyRepository = VocabularyRepository(application)

    override fun onQueryFinish(vocabularyList: LiveData<List<Vocabulary>>,
                               definitionList: List<LiveData<List<Definition>>>,
                               relatedWords: List<WordListEntry>) {
        this.vocabularyList = vocabularyList
        this.definitionList = definitionList
        this.relatedWords = relatedWords
    }


    //AUtomatically display the first entry, and related definitions/tags/etc for it
    var relatedWords: List<WordListEntry> = listOf()
    var vocabularyList : LiveData<List<Vocabulary>> = object : LiveData<List<Vocabulary>>(){}
    var definitionList : List<LiveData<List<Definition>>> = listOf()

//    init {
//        vocabularyRepository.getLatest(this)
//    }

//    var tags : LiveData<List<Tag>> = vocabularyRepository.
//    var vocabularyNotes : LiveData<List<VocabularyNote>>
//            = vocabularyRepository.getVocabularyNotes()

    // Function to search, must take in the webView and all from the Activity
    // Should be void, but should initialize my object's live data and all
    // TODO: Figure out some way to handle requesting the search without passing webViews around
    fun search(webView: WebView,
               dictionary: String,
               searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS){
        // Perform the search, ask the repository to save everything resulting from the search
        val databaseCallback = this
        vocabularyRepository.search(webView,
                                    databaseCallback,
                                    dictionary,
                                    searchTerm,
                                    wordLanguageCode,
                                    definitionLanguageCode,
                                    matchType)
    }

//    fun navigateRelatedWord(webView)
}

package data.room.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.vocab.shared.WordListEntry


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var relatedWords: List<WordListEntry> = listOf()
    var vocabularyList : LiveData<List<Vocabulary>> = object : LiveData<List<Vocabulary>>(){}
    var definitionList : List<LiveData<List<Definition>>> = listOf()

//AUtomatically display the first entry, and related definitions/tags/etc for it
//    init {
//        vocabularyRepository.getLatest(this)
//    }

//    var tags : LiveData<List<Tag>> = vocabularyRepository.
//    var vocabularyNotes : LiveData<List<VocabularyNote>>
//            = vocabularyRepository.getVocabularyNotes()

    // Function to search, must take in the webView and all from the Activity
    // Should be void, but should initialize my object's live data and all

//    fun navigateRelatedWord(webView)
}

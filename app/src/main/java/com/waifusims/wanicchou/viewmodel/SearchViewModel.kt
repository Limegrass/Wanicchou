package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import data.arch.vocab.DefinitionLiveData
import data.arch.vocab.VocabularyLiveData
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.WordListEntry


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var relatedWords: List<WordListEntry> = getDefaultRelatedWord()
    var vocabularyList : MutableLiveData<List<Vocabulary>> = getDefaultVocabularyList()
    var definitionList : MutableLiveData<List<Definition>> = getDefaultDefinitionList()

    val vocabulary : Vocabulary
    get() {
        return vocabularyList.value!![currentVocabularyIndex]
    }

    val definitions : List<Definition>
    get() {
        return definitionList.value!!
    }

    fun moveToNextWord() {
        this.currentVocabularyIndex++
    }

    //TODO: Should change the list of definition/related word on vocab change.
    private var currentVocabularyIndex : Int = 0

    private fun getDefaultRelatedWord(): List<WordListEntry>{
        return listOf(WordListEntry("テスト"))
    }

    private fun getDefaultVocabularyList(): MutableLiveData<List<Vocabulary>>{
        val word = "和日帳"
        val pronunciation = "わにっちょう"
        val wordLanguageCode = "jp"

        val vocabulary = Vocabulary(word, wordLanguageCode, pronunciation)
        val liveData = MutableLiveData<List<Vocabulary>>()
        liveData.value = listOf(vocabulary)
        return liveData
    }

    private fun getDefaultDefinitionList(): MutableLiveData<List<Definition>>{
        val definitionText = "使えないアプリ。"
        val definitionLanguageCode = "jp"
        val definition = Definition(definitionText, definitionLanguageCode, 0, 0)
        val liveData = MutableLiveData<List<Definition>>()
        liveData.value = listOf(definition)
        return liveData
    }


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

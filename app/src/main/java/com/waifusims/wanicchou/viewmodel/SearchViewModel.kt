package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.WordListEntry


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var relatedWords: List<WordListEntry> = getDefaultRelatedWord()
//    var vocabularyList : LiveData<List<Vocabulary>> = getDefaultMutableLiveData()
//    var definitionList : List<LiveData<List<Definition>>> = getDefaultDefinitionList()
    private val vocabularyMutableLiveData : MutableLiveData<List<Vocabulary>> = MutableLiveData()
    private val definitionMutableLiveData : MutableLiveData<LiveData<List<Definition>>> = MutableLiveData()
    init {
        vocabularyMutableLiveData.value = listOf(getDefaultVocabulary())
        definitionMutableLiveData.value = object : LiveData<List<Definition>>(){
            override fun getValue() = getDefaultDefinitionList()
        }
    }

//    private val mediator : MediatorLiveData<LiveData<Vocabulary>> = MediatorLiveData()
    fun setVocabularyData (data : List<Vocabulary>){
        vocabularyMutableLiveData.value = data
    }

    fun setDefinitionData (data : LiveData<List<Definition>>) {
        definitionMutableLiveData.value = data
    }

    fun setVocabularyObserver(lifecycleOwner: LifecycleOwner, observer: Observer<List<Vocabulary>>){
        vocabularyMutableLiveData.observe(lifecycleOwner, observer)
    }
    fun setDefinitionObserver(lifecycleOwner: LifecycleOwner, observer: Observer<LiveData<List<Definition>>>){
        definitionMutableLiveData.observe(lifecycleOwner, observer)
    }

    val vocabulary : Vocabulary
    get() {
        return if (vocabularyMutableLiveData.value != null){
            vocabularyMutableLiveData.value!![wordIndex]
        }
        else {
            getDefaultVocabulary()
        }
    }

    val definitions : List<Definition>
    get() {
        return if (definitionMutableLiveData.value != null){
            definitionMutableLiveData.value!!.value!!
        }
        else{
            getDefaultDefinitionList()
        }
    }

    fun moveToPreviousWord() {
        this.wordIndex++
    }
    fun moveToNextWord() {
        this.wordIndex++
    }

    //TODO: Should change the list of definition/related word on vocab change.
    var wordIndex : Int = 0

    private fun getDefaultRelatedWord(): List<WordListEntry>{
        return listOf(WordListEntry(DEFAULT_RELATED_WORD))
    }

    private fun getDefaultVocabulary() : Vocabulary {
        val word = DEFAULT_WORD
        val pronunciation = DEFAULT_WORD_PRONUNCIATION
        val wordLanguageCode = DEFAULT_LANGUAGE_CODE

        return Vocabulary(word, wordLanguageCode, pronunciation)
    }

    private fun getDefaultDefinitionList(): List<Definition> {
        val definitionText = DEFAULT_DEFINITION
        val definitionLanguageCode = DEFAULT_LANGUAGE_CODE
        val definition = Definition(definitionText, definitionLanguageCode, 0, 0)
        return listOf(definition)
    }

    companion object {
        const val DEFAULT_DEFINITION = "使えないアプリ。"
        const val DEFAULT_LANGUAGE_CODE = "jp"
        const val DEFAULT_WORD_PRONUNCIATION = "わにっちょう"
        const val DEFAULT_WORD = "和日帳"
        const val DEFAULT_RELATED_WORD = "テスト"
    }




//Automatically display the first entry, and related definitions/tags/etc for it
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

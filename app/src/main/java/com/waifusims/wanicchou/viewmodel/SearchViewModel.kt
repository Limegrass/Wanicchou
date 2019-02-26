package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.WordListEntry
import data.room.entity.VocabularyInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var relatedWords: List<WordListEntry> = getDefaultRelatedWord()
//    var vocabularyList : LiveData<List<Vocabulary>> = getDefaultMutableLiveData()
//    var definitionList : List<LiveData<List<Definition>>> = getDefaultDefinitionList()
    private val vocabularyInformationLiveData : MediatorLiveData<List<VocabularyInformation>>
                                                = MediatorLiveData()
    init {
        val defaultVocabularyInformation = VocabularyInformation()
        defaultVocabularyInformation.vocabulary = getDefaultVocabulary()
        defaultVocabularyInformation.definitions = getDefaultDefinitionList()
        vocabularyInformationLiveData.value = listOf(defaultVocabularyInformation)
        relatedWords = getDefaultRelatedWord()
    }

    fun setVocabularyInformation(vocabularyInformation: LiveData<List<VocabularyInformation>>){
        vocabularyInformationLiveData.removeSource(vocabularyInformationLiveData)
        vocabularyInformationLiveData.addSource(vocabularyInformation) {
            vocabularyInformationLiveData.value = it
            if (vocabularyInformationLiveData.value != vocabularyInformation){
                vocabularyInformationLiveData.removeSource(vocabularyInformation)
            }
        }
    }

//    private val mediator : MediatorLiveData<LiveData<Vocabulary>> = MediatorLiveData()
    fun setVocabularyInformationObserver(lifecycleOwner: LifecycleOwner,
                                         observer: Observer<List<VocabularyInformation>>){
        vocabularyInformationLiveData.observe(lifecycleOwner, observer)
    }

    private val vocabularyInformation : List<VocabularyInformation>? = vocabularyInformationLiveData.value

    val vocabulary : Vocabulary
    get() {
        return if (vocabularyInformation != null
                && vocabularyInformation.isNotEmpty()){
            if (vocabularyInformation.size < this.wordIndex){
                wordIndex = 0
            }
            vocabularyInformation[wordIndex].vocabulary!!
        }
        else {
            getDefaultVocabulary()
        }
    }

    val definitions : List<Definition>
    get() {
        return if (vocabularyInformationLiveData.value != null){
            vocabularyInformationLiveData.value!![wordIndex].definitions
        }
        else{
            getDefaultDefinitionList()
        }
    }

    fun moveToPreviousWord() {
        if(wordIndex > 0){
            this.wordIndex--
        }
    }
    fun moveToNextWord() {
        if(wordIndex < vocabularyInformationLiveData.value!!.size - 1) {
            this.wordIndex++
        }
    }
    fun getWordIndex(): Int {
        return wordIndex
    }
    //TODO: Should change the list of definition/related word on vocab change.
    private var wordIndex : Int = 0

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

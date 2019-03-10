package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import data.room.entity.Vocabulary


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class VocabularyViewModel(application: Application) : AndroidViewModel(application) {
//    var vocabularyList : LiveData<List<Vocabulary>> = getDefaultMutableLiveData()
//    var relatedVocabularyList : List<LiveData<List<Definition>>> = getDefaultDefinitionList()
    private val vocabularyMediator : MediatorLiveData<List<Vocabulary>> = MediatorLiveData()
    private val wordIndexLiveData : MutableLiveData<Int> = MutableLiveData()

    init {
        vocabularyMediator.value = listOf(getDefaultVocabulary())
        wordIndexLiveData.value = 0
    }

    fun setVocabularyList(vocabularyList: List<Vocabulary>){
        vocabularyMediator.value = vocabularyList
    }

//    private val mediator : MediatorLiveData<LiveData<Vocabulary>> = MediatorLiveData()
    // TODO: Something better than this.
    // There has to be a way to propagate observer calls from wordIndex changes
    // to everything registered on the vocabulary.
    // May need to implement my own visitor pattern.
    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View?){
        val wordObserver = Observer<Int>{
            action(view)
        }
        val vocabularyObserver = Observer<List<Vocabulary>>{
            action(view)
        }

        vocabularyMediator.observe(lifecycleOwner, vocabularyObserver)
        wordIndexLiveData.observe(lifecycleOwner, wordObserver)
    }


    private val currentList : List<Vocabulary>
        get() {
            return vocabularyMediator.value!!
        }

    val vocabulary : Vocabulary
    get() {
        val currentLiveDataValue = currentList
        return if (currentLiveDataValue.isNotEmpty()){
            if (wordIndex > currentLiveDataValue.size){
                wordIndexLiveData.value = 0
            }
            currentLiveDataValue[wordIndex]
        }
        else {
            getDefaultVocabulary()
        }
    }

    fun resetWordIndex(){
        wordIndexLiveData.value = 0
    }

    fun moveToPreviousWord() {
        if(wordIndex > 0){
            val currentIndex = wordIndexLiveData.value!!
            wordIndexLiveData.value = currentIndex - 1
            definitionIndex = 0
        }
    }

    fun moveToNextWord() {
        if(wordIndex < currentList.size - 1){
            val currentIndex = wordIndexLiveData.value!!
            wordIndexLiveData.value = currentIndex + 1
            definitionIndex = 0
        }
    }

    private var definitionIndex : Int = 0

    //TODO: Should change the list of definition/related word on vocab change.
    val wordIndex : Int
        get() = wordIndexLiveData.value!!


    private fun getDefaultVocabulary() : Vocabulary {
        val word = DEFAULT_WORD
        val pronunciation = DEFAULT_WORD_PRONUNCIATION
        val wordLanguageCode = DEFAULT_LANGUAGE_CODE

        return Vocabulary(word, wordLanguageCode, pronunciation)
    }


    companion object {
        private val TAG : String = VocabularyViewModel::class.java.simpleName
        private const val DEFAULT_LANGUAGE_CODE = "jp"
        private const val DEFAULT_WORD_PRONUNCIATION = "わにっちょう"
        private const val DEFAULT_WORD = "和日帳"
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

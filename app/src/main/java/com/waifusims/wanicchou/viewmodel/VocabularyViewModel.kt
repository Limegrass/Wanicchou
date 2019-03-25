package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import data.room.entity.Vocabulary


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class VocabularyViewModel(application: Application) : AndroidViewModel(application) {
    private val vocabularyMediator : MediatorLiveData<List<Vocabulary>> = MediatorLiveData()
    private val wordIndexLiveData : MutableLiveData<Int> = MutableLiveData()

    init {
        vocabularyMediator.value = listOf(getDefaultVocabulary())
        wordIndexLiveData.value = 0
    }

    var vocabularyList: List<Vocabulary>
        get(){
            return vocabularyMediator.value!!
        }
        set(value){
            vocabularyMediator.value = value
        }

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



    val vocabulary : Vocabulary
    get() {
        return if (vocabularyList.isNotEmpty()){
            if (wordIndex > vocabularyList.size){
                wordIndexLiveData.value = 0
            }
            vocabularyList[wordIndex]
        }
        else {
            getDefaultVocabulary()
        }
    }

    //TODO: Should change the list of definition/related word on vocab change.
    var wordIndex : Int
        get() = wordIndexLiveData.value!!
        set(value){
            if(value > 0
                    && value < vocabularyList.size){
                wordIndexLiveData.value = value
            }
        }


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
}

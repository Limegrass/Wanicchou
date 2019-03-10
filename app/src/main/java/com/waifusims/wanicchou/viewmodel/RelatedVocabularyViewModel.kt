package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.Vocabulary

class RelatedVocabularyViewModel(application: Application) : AndroidViewModel(application){
    private val relatedVocabularyLiveData : MutableLiveData<List<Vocabulary>> = MutableLiveData()

    init{
        relatedVocabularyLiveData.value = listOf(getDefaultRelatedWord())
    }

    private val currentList : List<Vocabulary>
        get() {
            return relatedVocabularyLiveData.value!!
        }

    val relatedVocabularyList : List<Vocabulary>
        get() {
            val currentLiveDataValue = currentList
            return if (currentLiveDataValue.isNotEmpty()){
                currentLiveDataValue
            }
            else{
                listOf(getDefaultRelatedWord())
            }
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View? = null){
        val definitionObserver = Observer<List<Vocabulary>>{
            action(view)
        }
        relatedVocabularyLiveData.observe(lifecycleOwner, definitionObserver)
    }

    fun setDefinitionList(definitionList : List<Vocabulary>){
        relatedVocabularyLiveData.value = definitionList
    }

    //TODO: Make this just an initial database value
    private fun getDefaultRelatedWord(): Vocabulary{
        return Vocabulary(DEFAULT_RELATED_WORD,
                DEFAULT_LANGUAGE_CODE,
                DEFAULT_RELATED_WORD_PRONUNCIATION)
    }

    companion object {
        private const val DEFAULT_LANGUAGE_CODE = "jp"
        private const val DEFAULT_RELATED_WORD = "テスト"
        private const val DEFAULT_RELATED_WORD_PRONUNCIATION = "てすと"
    }
}

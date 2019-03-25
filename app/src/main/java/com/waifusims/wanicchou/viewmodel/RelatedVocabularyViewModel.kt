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
        relatedVocabularyLiveData.value = listOf()
    }

    var relatedVocabularyList : List<Vocabulary>
        get() {
            return relatedVocabularyLiveData.value!!
        }
        set(value){
            relatedVocabularyLiveData.value = value
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View? = null){
        val definitionObserver = Observer<List<Vocabulary>>{
            action(view)
        }
        relatedVocabularyLiveData.observe(lifecycleOwner, definitionObserver)
    }
}

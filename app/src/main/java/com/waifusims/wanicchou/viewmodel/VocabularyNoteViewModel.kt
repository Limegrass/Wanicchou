package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.VocabularyNote

class VocabularyNoteViewModel(application: Application)
    : AndroidViewModel(application){
    companion object {
        private val TAG = VocabularyNoteViewModel::class.java.simpleName
    }
    private val liveData : MutableLiveData<List<VocabularyNote>> = MutableLiveData()

    init{
        liveData.value = listOf()
    }

    private val currentList : List<VocabularyNote>
        get() {
            return liveData.value!!
        }

    val notes : List<VocabularyNote>
        get() {
            val currentLiveDataValue = currentList
            return if (currentLiveDataValue.isNotEmpty()){
                currentLiveDataValue
            }
            else{
                listOf()
            }
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View? = null){
        val definitionObserver = Observer<List<VocabularyNote>>{
            action(view)
        }
        liveData.observe(lifecycleOwner, definitionObserver)
    }

    fun setNotes(notes : List<VocabularyNote>){
        liveData.value = notes
    }

}

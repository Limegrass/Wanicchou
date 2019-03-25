package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.DefinitionNote

class DefinitionNoteViewModel(application: Application)
    : AndroidViewModel(application){
    companion object {
        private val TAG = DefinitionNoteViewModel::class.java.simpleName
    }
    private val liveData : MutableLiveData<List<DefinitionNote>> = MutableLiveData()

    init{
        liveData.value = listOf()
    }

    private val currentList : List<DefinitionNote>
        get() {
            return liveData.value!!
        }

    val notes : List<DefinitionNote>
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
        val definitionObserver = Observer<List<DefinitionNote>>{
            action(view)
        }
        liveData.observe(lifecycleOwner, definitionObserver)
    }

    fun setNotes(notes : List<DefinitionNote>){
        liveData.value = notes
    }
}

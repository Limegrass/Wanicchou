package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.Definition

class DefinitionViewModel(application: Application) : AndroidViewModel(application){
    private val definitionLiveData : MutableLiveData<List<Definition>> = MutableLiveData()

    init{
        definitionLiveData.value = listOf(getDefaultDefinition())
    }

    private val currentList : List<Definition>
        get() {
            return definitionLiveData.value!!
        }

    var definitionList : List<Definition>
        get() {
            val currentLiveDataValue = currentList
            return if (currentLiveDataValue.isNotEmpty()){
                currentLiveDataValue
            }
            else{
                listOf(getDefaultDefinition())
            }
        }
        set(value){
            definitionLiveData.value = value
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View? = null){
        val definitionObserver = Observer<List<Definition>>{
            action(view)
        }
        definitionLiveData.observe(lifecycleOwner, definitionObserver)
    }


    //TODO: Make this just an initial database value
    private fun getDefaultDefinition(): Definition {
        val definitionText = DEFAULT_DEFINITION
        val definitionLanguageCode = DEFAULT_LANGUAGE_CODE
        return Definition(definitionText, definitionLanguageCode, 1, 0)
    }



    companion object {
        private const val DEFAULT_DEFINITION = "ある使えないアプリ。"
        private const val DEFAULT_LANGUAGE_CODE = "jp"
    }
}
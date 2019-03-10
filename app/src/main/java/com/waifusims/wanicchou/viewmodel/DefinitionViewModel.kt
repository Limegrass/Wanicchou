package com.waifusims.wanicchou.viewmodel

import android.app.Application
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

    val definitionList : List<Definition>
        get() {
            val currentLiveDataValue = currentList
            return if (currentLiveDataValue.isNotEmpty()){
                currentLiveDataValue
            }
            else{
                listOf(getDefaultDefinition())
            }
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : () -> Unit){
        val definitionObserver = Observer<List<Definition>>{
            action()
        }
        definitionLiveData.observe(lifecycleOwner, definitionObserver)
    }

    fun setDefinitionList(definitionList : List<Definition>){
        definitionLiveData.value = definitionList
    }

    //TODO: Make this just an initial database value
    private fun getDefaultDefinition(): Definition {
        val definitionText = DEFAULT_DEFINITION
        val definitionLanguageCode = DEFAULT_LANGUAGE_CODE
        return Definition(definitionText, definitionLanguageCode, 1, 0)
    }



    companion object {
        private const val DEFAULT_DEFINITION = "使えないアプリ。"
        private const val DEFAULT_LANGUAGE_CODE = "jp"
    }
}
package com.waifusims.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import data.room.entity.Definition

class DefinitionViewModel(application: Application) : AndroidViewModel(application){
    private val definitionMediator : MediatorLiveData<List<Definition>> = MediatorLiveData()

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
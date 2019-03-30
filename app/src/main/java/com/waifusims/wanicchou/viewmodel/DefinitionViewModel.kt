package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Definition

// Temporarily a list due to simplify UI management for now.
// Need to change
// TODO: Change from recycler view
class DefinitionViewModel(application: Application)
    : ObservableViewModel<List<Definition>>(application){
    init {
        value = listOf(getDefaultDefinition())
    }
    //Always only one element. Not a list.
    val definition : Definition
    get () {
        return value!![0]
    }

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
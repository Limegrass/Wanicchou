package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Definition

class DefinitionViewModel(application: Application)
    : ObservableListViewModel<Definition>(application){
    init {
        list = listOf(getDefaultDefinition())
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
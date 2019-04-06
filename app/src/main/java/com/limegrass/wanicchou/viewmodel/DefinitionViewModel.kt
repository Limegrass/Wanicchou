package com.limegrass.wanicchou.viewmodel

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
        return Definition(DEFAULT_DEFINITION,
                            DEFAULT_LANGUAGE_ID,
                                1,
                                0)
    }

    companion object {
        private const val DEFAULT_DEFINITION = "ある使えないアプリ。"
        private const val DEFAULT_LANGUAGE_ID = 1L
    }
}
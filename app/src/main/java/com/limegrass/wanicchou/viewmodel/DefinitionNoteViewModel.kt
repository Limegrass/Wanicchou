package com.limegrass.wanicchou.viewmodel

import data.arch.models.IDefinition
import data.arch.models.INote

class DefinitionNoteViewModel
    : ObservableViewModel<List<INote<IDefinition>>>(){
    init{
        value = listOf()
    }
}

package com.limegrass.wanicchou.viewmodel

import data.models.IDefinition
import data.models.INote

class DefinitionNoteViewModel
    : ObservableViewModel<List<INote<IDefinition>>>(){
    init{
        value = listOf()
    }
}

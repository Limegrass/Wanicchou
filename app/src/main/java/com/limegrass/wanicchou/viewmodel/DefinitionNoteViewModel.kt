package com.limegrass.wanicchou.viewmodel

import data.room.entity.DefinitionNote

class DefinitionNoteViewModel
    : ObservableViewModel<List<DefinitionNote>>(){
    init{
        value = listOf()
    }
}

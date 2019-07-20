package com.limegrass.wanicchou.viewmodel

import data.room.entity.VocabularyNote

class VocabularyNoteViewModel
    : ObservableViewModel<List<VocabularyNote>>(){
    init {
        value = listOf()
    }
}

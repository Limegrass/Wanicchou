package com.limegrass.wanicchou.viewmodel

import data.arch.models.INote
import data.arch.models.IVocabulary

class VocabularyNoteViewModel
    : ObservableViewModel<List<INote<IVocabulary>>>(){
    init {
        value = listOf()
    }
}

package com.limegrass.wanicchou.viewmodel

import data.models.INote
import data.models.IVocabulary

class VocabularyNoteViewModel
    : ObservableViewModel<List<INote<IVocabulary>>>(){
    init {
        value = listOf()
    }
}

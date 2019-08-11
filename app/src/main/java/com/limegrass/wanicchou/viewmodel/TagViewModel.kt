package com.limegrass.wanicchou.viewmodel

import data.arch.models.ITaggedItem
import data.arch.models.IVocabulary

class TagViewModel
    : ObservableViewModel<List<ITaggedItem<IVocabulary>>>(){
    init {
        value = listOf()
    }
}

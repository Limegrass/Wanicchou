package com.limegrass.wanicchou.viewmodel

import data.models.ITaggedItem
import data.models.IVocabulary

class TagViewModel
    : ObservableViewModel<List<ITaggedItem<IVocabulary>>>(){
    init {
        value = listOf()
    }
}

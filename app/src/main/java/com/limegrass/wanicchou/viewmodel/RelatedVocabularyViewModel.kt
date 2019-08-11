package com.limegrass.wanicchou.viewmodel

import data.arch.models.IVocabulary

class RelatedVocabularyViewModel
    : ObservableViewModel<List<IVocabulary>>(){
    init{
        value = listOf()
    }
}

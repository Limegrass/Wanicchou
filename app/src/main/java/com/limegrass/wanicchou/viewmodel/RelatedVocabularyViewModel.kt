package com.limegrass.wanicchou.viewmodel

import data.models.IVocabulary

class RelatedVocabularyViewModel
    : ObservableViewModel<List<IVocabulary>>(){
    init{
        value = listOf()
    }
}

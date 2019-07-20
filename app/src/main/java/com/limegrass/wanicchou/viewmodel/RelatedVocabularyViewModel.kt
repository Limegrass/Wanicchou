package com.limegrass.wanicchou.viewmodel

import data.room.entity.Vocabulary

class RelatedVocabularyViewModel
    : ObservableViewModel<List<Vocabulary>>(){
    init{
        value = listOf()
    }
}

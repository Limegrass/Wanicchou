package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Vocabulary

class RelatedVocabularyViewModel(application: Application)
    : ObservableListViewModel<Vocabulary>(application){
    init{
        list = listOf()
    }
}

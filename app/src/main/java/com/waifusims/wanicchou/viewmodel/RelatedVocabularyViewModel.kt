package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Vocabulary

class RelatedVocabularyViewModel(application: Application)
    : ObservableViewModel<List<Vocabulary>>(application){
    init{
        value = listOf()
    }
}

package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.VocabularyNote

class VocabularyNoteViewModel(application: Application)
    : ObservableListViewModel<VocabularyNote>(application){
    init {
        list = listOf()
    }
}

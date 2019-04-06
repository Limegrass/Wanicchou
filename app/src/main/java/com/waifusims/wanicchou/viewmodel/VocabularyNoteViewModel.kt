package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.VocabularyNote

class VocabularyNoteViewModel(application: Application)
    : ObservableViewModel<List<VocabularyNote>>(application){
    init {
        value = listOf()
    }
}

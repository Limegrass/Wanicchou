package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.DefinitionNote

class DefinitionNoteViewModel(application: Application)
    : ObservableViewModel<List<DefinitionNote>>(application){
    init{
        value = listOf()
    }
}

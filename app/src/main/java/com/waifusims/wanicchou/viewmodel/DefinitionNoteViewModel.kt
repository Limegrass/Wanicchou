package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.DefinitionNote

class DefinitionNoteViewModel(application: Application)
    : ObservableListViewModel<DefinitionNote>(application){
    init{
        list = listOf()
    }
}

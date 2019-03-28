package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Tag

class TagViewModel(application: Application)
    : ObservableListViewModel<Tag>(application){
    init {
        list = listOf()
    }
}

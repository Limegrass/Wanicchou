package com.limegrass.wanicchou.viewmodel

import data.room.entity.Tag

class TagViewModel
    : ObservableViewModel<List<Tag>>(){
    init {
        value = listOf()
    }
}

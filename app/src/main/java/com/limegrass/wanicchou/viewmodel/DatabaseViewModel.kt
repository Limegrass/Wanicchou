package com.limegrass.wanicchou.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import data.room.entity.Vocabulary

class DatabaseViewModel(val vocabularyList : LiveData<List<Vocabulary>>) : ViewModel()

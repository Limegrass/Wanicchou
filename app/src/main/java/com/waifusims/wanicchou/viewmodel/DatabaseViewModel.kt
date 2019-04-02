package com.waifusims.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import data.room.VocabularyRepository
import data.room.entity.Vocabulary

class DatabaseViewModel(application: Application)
    : AndroidViewModel(application){
    val vocabularyList : LiveData<List<Vocabulary>>
    init {
        val repository = VocabularyRepository(application)
        vocabularyList = repository.getAllSavedVocabulary()
    }

}

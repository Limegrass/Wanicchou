package com.waifusims.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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

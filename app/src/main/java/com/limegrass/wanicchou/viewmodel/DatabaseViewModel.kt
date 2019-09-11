package com.limegrass.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import room.database.WanicchouDatabase
import room.dbo.entity.Vocabulary
import kotlinx.coroutines.runBlocking

class DatabaseViewModel(application: Application)
    : AndroidViewModel(application){
    val vocabularyList : LiveData<List<Vocabulary>> = runBlocking {
        // This is hot garbage but I don't care enough for this screen right now
        // Need to redo the entire Database Activity Screen
        val database = WanicchouDatabase(application)
        database.vocabularyDao().getAllWithDefinition()
    }
}

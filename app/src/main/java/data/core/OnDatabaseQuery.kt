package data.core

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.vocab.shared.WordListEntry


interface OnDatabaseQuery{
    fun onQueryFinish(vocabularyList: LiveData<List<Vocabulary>>,
                      definitionList: List<LiveData<List<Definition>>>,
                      relatedWords: List<WordListEntry>)
}
package data.core

import android.arch.lifecycle.MutableLiveData
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.vocab.shared.WordListEntry


interface OnDatabaseQuery{
    fun onQueryFinish(vocabularyList: List<MutableLiveData<Vocabulary>>,
                      definitionList: List<List<MutableLiveData<Definition>>>,
                      relatedWords: List<WordListEntry>)
}
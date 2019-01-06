package data.arch.vocab

import android.arch.lifecycle.LiveData
import data.room.entity.Vocabulary

class VocabularyLiveData(vocabularyList : List<Vocabulary>) : LiveData<List<Vocabulary>>() {
    //TODO: Update the database on change
}
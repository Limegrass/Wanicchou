package data.arch.vocab

import android.arch.lifecycle.LiveData
import data.room.entity.Definition

class DefinitionLiveData(definitionList : List<Definition>) : LiveData<List<Definition>>() {
    //TODO: Update the database on change
}

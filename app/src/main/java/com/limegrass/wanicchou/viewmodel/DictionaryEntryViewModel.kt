package com.limegrass.wanicchou.viewmodel

import data.models.IDictionaryEntry

class DictionaryEntryViewModel
    : ObservableViewModel<IDictionaryEntry>() {

    private var dictionaryEntries : List<IDictionaryEntry>? = null
    var availableDictionaryEntries : List<IDictionaryEntry>
        get() {
            return dictionaryEntries ?: listOf()
        }
        set(dictionaryEntries) {
            this.dictionaryEntries = dictionaryEntries
            dictionaryEntryIndex = 0
            value = dictionaryEntries.first{
                dictionaryEntryIndex++
                it.definitions.isNotEmpty()
            }
        }

    private var dictionaryEntryIndex = 0

    companion object {
        private val TAG : String = DictionaryEntryViewModel::class.java.simpleName
    }
}
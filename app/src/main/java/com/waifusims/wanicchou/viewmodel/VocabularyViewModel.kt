package com.waifusims.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Vocabulary


// TODO: Remove related words completely and just use the new scheme for queries to find same words
class VocabularyViewModel(application: Application)
    : ObservableViewModel<List<Vocabulary>>(application) {

    init {
        value = listOf(getDefaultVocabulary())
    }

    val vocabulary : Vocabulary
    get() {
        return if (!value.isNullOrEmpty()){
            value!![wordIndex]
        }
        else {
            getDefaultVocabulary()
        }
    }

    //TODO: Change to handle multiple returned vocabs
    val wordIndex : Int
        get() = 0

    private fun getDefaultVocabulary() : Vocabulary {
        return Vocabulary(DEFAULT_WORD,
                          DEFAULT_LANGUAGE_ID,
                          DEFAULT_WORD_PRONUNCIATION)
    }

    companion object {
        private val TAG : String = VocabularyViewModel::class.java.simpleName
        private const val DEFAULT_LANGUAGE_ID = 1L
        private const val DEFAULT_WORD_PRONUNCIATION = "わにっちょう"
        private const val DEFAULT_WORD = "和日帳"
    }
}

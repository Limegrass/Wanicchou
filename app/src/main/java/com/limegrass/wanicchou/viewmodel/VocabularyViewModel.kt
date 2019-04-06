package com.limegrass.wanicchou.viewmodel

import android.app.Application
import data.room.entity.Vocabulary


// TODO: Refactor s.t. I don't need this default
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
                          DEFAULT_WORD_PRONUNCIATION,
                    "",
                          DEFAULT_LANGUAGE_ID,
                1)
    }

    companion object {
        private val TAG : String = VocabularyViewModel::class.java.simpleName
        private const val DEFAULT_LANGUAGE_ID = 1L
        private const val DEFAULT_WORD_PRONUNCIATION = "わにっちょう"
        private const val DEFAULT_WORD = "和日帳"
    }
}

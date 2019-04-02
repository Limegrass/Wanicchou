package data.arch.info.vocabulary.shared

import data.room.entity.Vocabulary

internal interface IVocabularyStrategy {
    fun get(wordSource: String,
            wordLanguageID: Long) : Vocabulary
}
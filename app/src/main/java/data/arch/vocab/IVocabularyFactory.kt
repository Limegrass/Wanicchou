package data.arch.vocab

import data.room.entity.Vocabulary

interface IVocabularyFactory {
    fun getVocabulary(wordSource: String,
                      wordLanguageCode: String): Vocabulary
}
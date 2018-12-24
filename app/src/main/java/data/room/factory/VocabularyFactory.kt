package data.room.factory

import data.room.entity.Vocabulary
import data.vocab.model.DictionaryEntry

class VocabularyFactory {
    fun getVocabulary(dictionaryEntry: DictionaryEntry): Vocabulary {
        return Vocabulary(dictionaryEntry.word,
                dictionaryEntry.pronunciation,
                dictionaryEntry.pitch,
                dictionaryEntry.wordLanguageCode)
    }
}
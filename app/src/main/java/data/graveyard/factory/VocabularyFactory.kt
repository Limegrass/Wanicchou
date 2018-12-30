package data.graveyard.factory

import data.room.entity.Vocabulary
import data.graveyard.DictionaryEntry

class VocabularyFactory {
    fun getVocabulary(dictionaryEntry: DictionaryEntry): Vocabulary {
        return Vocabulary(dictionaryEntry.word,
                dictionaryEntry.pronunciation,
                dictionaryEntry.pitch,
                dictionaryEntry.wordLanguageCode)
    }
}
package data.room.factory

import data.room.entity.Definition
import data.vocab.model.DictionaryEntry

class DefinitionFactory {
    fun getDefinition(dictionaryEntry: DictionaryEntry, dictionaryID: Int, vocabularyID: Int): Definition {
        return Definition(dictionaryEntry.definition,
                          dictionaryEntry.definitionLanguageCode,
                          dictionaryID,
                          vocabularyID)
    }
}